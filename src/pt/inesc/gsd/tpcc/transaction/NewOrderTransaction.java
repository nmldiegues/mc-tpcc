package pt.inesc.gsd.tpcc.transaction;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jvstm.VBox;

import org.radargun.CacheWrapper;

import pt.inesc.gsd.tpcc.ElementNotFoundException;
import pt.inesc.gsd.tpcc.TpccTools;
import pt.inesc.gsd.tpcc.domain.Company;
import pt.inesc.gsd.tpcc.domain.Customer;
import pt.inesc.gsd.tpcc.domain.District;
import pt.inesc.gsd.tpcc.domain.Item;
import pt.inesc.gsd.tpcc.domain.NewOrder;
import pt.inesc.gsd.tpcc.domain.Order;
import pt.inesc.gsd.tpcc.domain.OrderLine;
import pt.inesc.gsd.tpcc.domain.Stock;
import pt.inesc.gsd.tpcc.domain.Warehouse;

public class NewOrderTransaction implements TpccTransaction {

   private final long warehouseID;
   private final long districtID;
   private final long customerID;
   private final int numItems;
   private int allLocal;

   private final long[] itemIDs;
   private final long[] supplierWarehouseIDs;
   private final long[] orderQuantities;

   public NewOrderTransaction(TpccTools tpccTools, int warehouseID) {

      if (warehouseID <= 0) {
         this.warehouseID = tpccTools.randomNumber(1, TpccTools.NB_WAREHOUSES);
      } else {
         this.warehouseID = warehouseID;
      }

      this.districtID = tpccTools.randomNumber(1, TpccTools.NB_MAX_DISTRICT);
      this.customerID = tpccTools.nonUniformRandom(TpccTools.C_C_ID, TpccTools.A_C_ID, 1, TpccTools.NB_MAX_CUSTOMER);

      this.numItems = (int) tpccTools.randomNumber(TpccTools.NUMBER_OF_ITEMS_INTERVAL[0],
                                                   TpccTools.NUMBER_OF_ITEMS_INTERVAL[1]); // o_ol_cnt
      this.itemIDs = new long[numItems];
      this.supplierWarehouseIDs = new long[numItems];
      this.orderQuantities = new long[numItems];
      this.allLocal = 1; // see clause 2.4.2.2 (dot 6)
      for (int i = 0; i < numItems; i++) // clause 2.4.1.5
      {
         itemIDs[i] = tpccTools.nonUniformRandom(TpccTools.C_OL_I_ID, TpccTools.A_OL_I_ID, 1, TpccTools.NB_MAX_ITEM);
         if (tpccTools.randomNumber(1, 100) > 1) {
            supplierWarehouseIDs[i] = this.warehouseID;
         } else //see clause 2.4.1.5 (dot 2)
         {
            do {
               supplierWarehouseIDs[i] = tpccTools.randomNumber(1, TpccTools.NB_WAREHOUSES);
            }
            while (supplierWarehouseIDs[i] == this.warehouseID && TpccTools.NB_WAREHOUSES > 1);
            allLocal = 0;// see clause 2.4.2.2 (dot 6)
         }
         orderQuantities[i] = tpccTools.randomNumber(1, TpccTools.NB_MAX_DISTRICT); //see clause 2.4.1.5 (dot 6)
      }
      // clause 2.4.1.5 (dot 1)
      if (tpccTools.randomNumber(1, 100) == 1)
         this.itemIDs[this.numItems - 1] = -12345;

   }

   @Override
   public void executeTransaction(CacheWrapper cacheWrapper) throws Throwable {
      newOrderTransaction(cacheWrapper);
   }

   @Override
   public boolean isReadOnly() {
      return false;
   }

   private void newOrderTransaction(CacheWrapper cacheWrapper) throws Throwable {
      long o_id = -1, s_quantity;
      String i_data, s_data;

      String ol_dist_info = null;
      double[] itemPrices = new double[numItems];
      double[] orderLineAmounts = new double[numItems];
      String[] itemNames = new String[numItems];
      long[] stockQuantities = new long[numItems];
      char[] brandGeneric = new char[numItems];
      long ol_supply_w_id, ol_i_id, ol_quantity;
      int s_remote_cnt_increment;
      double ol_amount, total_amount = 0;

      Warehouse w = Company.warehouses.get().get((int)warehouseID - 1);
      District d = w.districts.get().get((int)districtID - 1);
      Customer c = d.customers.get().get((int)customerID - 1);

      o_id = d.getD_next_o_id();
      d.setD_next_o_id(d.getD_next_o_id() + 1);

      Order o = new Order(o_id, districtID, warehouseID, customerID, new Date(), -1, numItems, allLocal);
      List<NewOrder> newOrders = new ArrayList<NewOrder>();
      NewOrder no = new NewOrder(o_id, districtID, warehouseID);
      newOrders.add(no);
      o.newOrders = new VBox<List<NewOrder>>(newOrders);
      List<Order> orders = new ArrayList<Order>(c.orders.get());
      orders.add(o);
      c.orders.put(orders);

      List<OrderLine> orderLines = new ArrayList<OrderLine>();

      // see clause 2.4.2.2 (dot 8)
      for (int ol_number = 1; ol_number <= numItems; ol_number++) {
         ol_supply_w_id = supplierWarehouseIDs[ol_number - 1];
         ol_i_id = itemIDs[ol_number - 1];
         ol_quantity = orderQuantities[ol_number - 1];

         // clause 2.4.2.2 (dot 8.1)
         Item i = Company.items.get().get((int)ol_i_id - 1);

         itemPrices[ol_number - 1] = i.getI_price();
         itemNames[ol_number - 1] = i.getI_name();
         // clause 2.4.2.2 (dot 8.2)

         Stock s = w.stocks.get().get((int)ol_i_id - 1);

         s_quantity = s.getS_quantity();
         stockQuantities[ol_number - 1] = s_quantity;
         // clause 2.4.2.2 (dot 8.2)
         if (s_quantity - ol_quantity >= 10) {
            s_quantity -= ol_quantity;
         } else {
            s_quantity += -ol_quantity + 91;
         }

         if (ol_supply_w_id == warehouseID) {
            s_remote_cnt_increment = 0;
         } else {
            s_remote_cnt_increment = 1;
         }
         // clause 2.4.2.2 (dot 8.2)
         s.setS_quantity(s_quantity);
         s.setS_ytd(s.getS_ytd() + ol_quantity);
         s.setS_remote_cnt(s.getS_remote_cnt() + s_remote_cnt_increment);
         s.setS_order_cnt(s.getS_order_cnt() + 1);


         // clause 2.4.2.2 (dot 8.3)
         ol_amount = ol_quantity * i.getI_price();
         orderLineAmounts[ol_number - 1] = ol_amount;
         total_amount += ol_amount;
         // clause 2.4.2.2 (dot 8.4)
         i_data = i.getI_data();
         s_data = s.getS_data();
         if (i_data.contains(TpccTools.ORIGINAL) && s_data.contains(TpccTools.ORIGINAL)) {
            brandGeneric[ol_number - 1] = 'B';
         } else {
            brandGeneric[ol_number - 1] = 'G';
         }

         switch ((int) districtID) {
            case 1:
               ol_dist_info = s.getS_dist_01();
               break;
            case 2:
               ol_dist_info = s.getS_dist_02();
               break;
            case 3:
               ol_dist_info = s.getS_dist_03();
               break;
            case 4:
               ol_dist_info = s.getS_dist_04();
               break;
            case 5:
               ol_dist_info = s.getS_dist_05();
               break;
            case 6:
               ol_dist_info = s.getS_dist_06();
               break;
            case 7:
               ol_dist_info = s.getS_dist_07();
               break;
            case 8:
               ol_dist_info = s.getS_dist_08();
               break;
            case 9:
               ol_dist_info = s.getS_dist_09();
               break;
            case 10:
               ol_dist_info = s.getS_dist_10();
               break;
         }
         // clause 2.4.2.2 (dot 8.5)

         OrderLine ol = new OrderLine(o_id, districtID, warehouseID, ol_number, ol_i_id, ol_supply_w_id, null,
                                      ol_quantity, ol_amount, ol_dist_info);
         
         orderLines.add(ol);

      }
      o.orderLines = new VBox<List<OrderLine>>(orderLines);

   }


}
