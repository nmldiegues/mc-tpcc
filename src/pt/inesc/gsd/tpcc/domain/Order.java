package pt.inesc.gsd.tpcc.domain;

import java.util.Date;
import java.util.List;

import jvstm.VBox;

public class Order {

   private final long o_id;

   private final long o_d_id;

   private final long o_w_id;

   private final long o_c_id;

   private final long o_entry_d;

   private final long o_carrier_id;

   private final int o_ol_cnt;

   private final int o_all_local;

   public VBox<List<OrderLine>> orderLines;
   public VBox<List<NewOrder>> newOrders;

   public Order(long o_id, long o_d_id, long o_w_id, long o_c_id, Date o_entry_d, long o_carrier_id, int o_ol_cnt, int o_all_local) {
      this.o_id = o_id;
      this.o_d_id = o_d_id;
      this.o_w_id = o_w_id;
      this.o_c_id = o_c_id;
      this.o_entry_d = (o_entry_d == null) ? -1 : o_entry_d.getTime();
      this.o_carrier_id = o_carrier_id;
      this.o_ol_cnt = o_ol_cnt;
      this.o_all_local = o_all_local;
   }

   public long getO_id() {
      return o_id;
   }

   public long getO_d_id() {
      return o_d_id;
   }

   public long getO_w_id() {
      return o_w_id;
   }

   public long getO_c_id() {
      return o_c_id;
   }

   public Date getO_entry_d() {
      return o_entry_d == -1 ? null : new Date(o_entry_d);
   }

   public long getO_carrier_id() {
      return o_carrier_id;
   }

   public int getO_ol_cnt() {
      return o_ol_cnt;
   }

   public int getO_all_local() {
      return o_all_local;
   }

}
