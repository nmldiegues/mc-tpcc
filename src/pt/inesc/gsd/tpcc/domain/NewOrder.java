package pt.inesc.gsd.tpcc.domain;

import org.radargun.CacheWrapper;

public class NewOrder  {

   private long no_o_id;

   private long no_d_id;

   private long no_w_id;

   public NewOrder() {

   }

   public NewOrder(long no_o_id, long no_d_id, long no_w_id) {
      this.no_o_id = no_o_id;
      this.no_d_id = no_d_id;
      this.no_w_id = no_w_id;
   }


   public long getNo_o_id() {
      return no_o_id;
   }

   public long getNo_d_id() {
      return no_d_id;
   }

   public long getNo_w_id() {
      return no_w_id;
   }

   public void setNo_o_id(long no_o_id) {
      this.no_o_id = no_o_id;
   }

   public void setNo_d_id(long no_d_id) {
      this.no_d_id = no_d_id;
   }

   public void setNo_w_id(long no_w_id) {
      this.no_w_id = no_w_id;
   }

}
