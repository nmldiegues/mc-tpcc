package pt.inesc.gsd.tpcc.transaction;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import pt.inesc.gsd.tpcc.TpccTerminal;
import pt.inesc.gsd.tpcc.TpccTools;
import pt.inesc.gsd.tpcc.domain.Company;
import pt.inesc.gsd.tpcc.domain.Customer;
import pt.inesc.gsd.tpcc.domain.District;
import pt.inesc.gsd.tpcc.domain.History;
import pt.inesc.gsd.tpcc.domain.Warehouse;

public class PaymentTransaction implements TpccTransaction {

   private final long terminalWarehouseID;

   private final long districtID;

   private final long customerDistrictID;

   private long customerWarehouseID;

   private final long customerID;

   private final boolean customerByName;

   private final String customerLastName;

   private final double paymentAmount;

   public PaymentTransaction(TpccTools tpccTools, int warehouseID) {

      if (warehouseID <= 0) {
         this.terminalWarehouseID = tpccTools.randomNumber(1, TpccTools.NB_WAREHOUSES);
      } else {
         this.terminalWarehouseID = warehouseID;
      }

      this.districtID = tpccTools.randomNumber(1, TpccTools.NB_MAX_DISTRICT);

      long x = tpccTools.randomNumber(1, 100);

      if (x <= 85) {
         this.customerDistrictID = this.districtID;
         this.customerWarehouseID = this.terminalWarehouseID;
      } else {
         this.customerDistrictID = tpccTools.randomNumber(1, TpccTools.NB_MAX_DISTRICT);
         do {
            this.customerWarehouseID = tpccTools.randomNumber(1, TpccTools.NB_WAREHOUSES);
         }
         while (this.customerWarehouseID == this.terminalWarehouseID && TpccTools.NB_WAREHOUSES > 1);
      }

      long y = tpccTools.randomNumber(1, 100);

      if (y <= 60) {
         this.customerByName = true;
         customerLastName = lastName((int) tpccTools.nonUniformRandom(TpccTools.C_C_LAST, TpccTools.A_C_LAST, 0, TpccTools.MAX_C_LAST));
      } else {
         this.customerByName = false;
         this.customerLastName = null;
      }
      this.customerID = tpccTools.nonUniformRandom(TpccTools.C_C_ID, TpccTools.A_C_ID, 1, TpccTools.NB_MAX_CUSTOMER);

      this.paymentAmount = tpccTools.randomNumber(100, 500000) / 100.0;


   }
   
   @Override
   public void executeTransaction() throws Throwable {
      paymentTransaction();
   }

   @Override
   public boolean isReadOnly() {
      return false;
   }

   private String lastName(int num) {
      return TpccTerminal.nameTokens[(num / 100) % TpccTerminal.nameTokens.length] + TpccTerminal.nameTokens[(num / 10) % TpccTerminal.nameTokens.length] + TpccTerminal.nameTokens[num % TpccTerminal.nameTokens.length];
   }

   private void paymentTransaction() throws Throwable {
      String w_name;
      String d_name;
      long nameCnt;

      String new_c_last;

      String c_data, c_new_data, h_data;

      Warehouse w = Company.warehouses.get().get((int)terminalWarehouseID - 1);
      w.setW_ytd(w.getW_ytd() + paymentAmount);

      District d = w.districts.get().get((int)districtID - 1);
      d.setD_ytd(d.getD_ytd() + paymentAmount);

      Customer c = null;
      if (customerByName) {
    	  Warehouse warehouse = Company.warehouses.get().get((int)terminalWarehouseID - 1);
    	  District district = warehouse.districts.get().get((int)districtID - 1);
    	  List<Customer> cList = district.customersByName.get().get(customerLastName);
    	  
    	  if(cList != null) {
         nameCnt = cList.size();

         if (nameCnt % 2 == 1) nameCnt++;
         Iterator<Customer> itr = cList.iterator();

         for (int i = 1; i <= nameCnt / 2; i++) {
            c = itr.next();
         }
    	  }
    	  else {
    		  c = district.customers.get().get((int)customerID - 1);
    	  }
      } else {
         // clause 2.6.2.2 (dot 3, Case 1)
    	  Warehouse warehouse = Company.warehouses.get().get((int)terminalWarehouseID - 1);
    	  District district = warehouse.districts.get().get((int)districtID - 1);
    	  c = district.customers.get().get((int)customerID - 1);
      }
      
      c.setC_balance(c.getC_balance() + paymentAmount);
      if (c.getC_credit().equals("BC")) {

         c_data = c.getC_data();

         c_new_data = c.getC_id() + " " + customerDistrictID + " " + customerWarehouseID + " " + districtID + " " + terminalWarehouseID + " " + paymentAmount + " |";
         if (c_data.length() > c_new_data.length()) {
            c_new_data += c_data.substring(0, c_data.length() - c_new_data.length());
         } else {
            c_new_data += c_data;
         }

         if (c_new_data.length() > 500) c_new_data = c_new_data.substring(0, 500);

         c.setC_data(c_new_data);

      } 

      w_name = w.getW_name();
      d_name = d.getD_name();

      if (w_name.length() > 10) w_name = w_name.substring(0, 10);
      if (d_name.length() > 10) d_name = d_name.substring(0, 10);
      h_data = w_name + "    " + d_name;

      History h = new History(c.getC_id(), customerDistrictID, customerWarehouseID, districtID, terminalWarehouseID, new Date(), paymentAmount, h_data);
      List<History> histories = new ArrayList<History>(c.histories.get());
      histories.add(h);
      c.histories.put(histories);
   }

}
