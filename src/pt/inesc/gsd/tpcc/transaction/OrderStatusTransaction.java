package pt.inesc.gsd.tpcc.transaction;

import java.util.Iterator;
import java.util.List;

import pt.inesc.gsd.tpcc.TpccTerminal;
import pt.inesc.gsd.tpcc.TpccTools;
import pt.inesc.gsd.tpcc.domain.Company;
import pt.inesc.gsd.tpcc.domain.Customer;
import pt.inesc.gsd.tpcc.domain.District;
import pt.inesc.gsd.tpcc.domain.Order;
import pt.inesc.gsd.tpcc.domain.Warehouse;

public class OrderStatusTransaction implements TpccTransaction {

   private final long terminalWarehouseID;

   private final long districtID;

   private final String customerLastName;

   private final long customerID;

   private final boolean customerByName;

   public OrderStatusTransaction(TpccTools tpccTools, int warehouseID) {

      if (warehouseID <= 0) {
         this.terminalWarehouseID = tpccTools.randomNumber(1, TpccTools.NB_WAREHOUSES);
      } else {
         this.terminalWarehouseID = warehouseID;
      }

      // clause 2.6.1.2
      this.districtID = tpccTools.randomNumber(1, TpccTools.NB_MAX_DISTRICT);

      long y = tpccTools.randomNumber(1, 100);

      if (y <= 60) {
         // clause 2.6.1.2 (dot 1)
         this.customerByName = true;
         this.customerLastName = lastName((int) tpccTools.nonUniformRandom(TpccTools.C_C_LAST, TpccTools.A_C_LAST, 0, TpccTools.MAX_C_LAST));
      } else {
         // clause 2.6.1.2 (dot 2)
         customerByName = false;
         this.customerLastName = null;
      }
      customerID = tpccTools.nonUniformRandom(TpccTools.C_C_ID, TpccTools.A_C_ID, 1, TpccTools.NB_MAX_CUSTOMER);

   }

   @Override
   public void executeTransaction() throws Throwable {
      orderStatusTransaction();
   }

   @Override
   public boolean isReadOnly() {
      return true;
   }

   private String lastName(int num) {
      return TpccTerminal.nameTokens[(num / 100) % TpccTerminal.nameTokens.length] + TpccTerminal.nameTokens[(num / 10) % TpccTerminal.nameTokens.length] + TpccTerminal.nameTokens[num % TpccTerminal.nameTokens.length];
   }

   private void orderStatusTransaction() throws Throwable {
      long nameCnt;

      Customer c = null;
      if (customerByName) {
    	  Warehouse warehouse = Company.warehouses.get().get((int)terminalWarehouseID - 1);
    	  District district = warehouse.districts.get().get((int)districtID - 1);
    	  List<Customer> cList = district.customersByName.get().get(customerLastName);
    	  
    	  if (cList != null) {
    		  nameCnt = cList.size();

    		  if (nameCnt % 2 == 1) nameCnt++;
    		  Iterator<Customer> itr = cList.iterator();

    		  for (int i = 1; i <= nameCnt / 2; i++) {
    			  c = itr.next();
    		  }
    	  } else {
    		  c = district.customers.get().get((int)customerID - 1);
    	  }

      } else {
         // clause 2.6.2.2 (dot 3, Case 1)
    	  Warehouse warehouse = Company.warehouses.get().get((int)terminalWarehouseID - 1);
    	  District district = warehouse.districts.get().get((int)districtID - 1);
    	  c = district.customers.get().get((int)customerID - 1);
      }

      // clause 2.6.2.2 (dot 4)
      List<Order> orders = c.orders.get();
      Order o = orders.get(orders.size() - 1);

      // clause 2.6.2.2 (dot 5)
      o.orderLines.get();
   }


}
