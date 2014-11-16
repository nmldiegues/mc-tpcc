package pt.inesc.gsd.tpcc.domain;

import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

public class History {

   private static final AtomicLong idGenerator = new AtomicLong(0L);

   /* customer id */
   private long h_c_id;

   /* district id */
   private long h_c_d_id;

   /* warehouse id */
   private long h_c_w_id;

   private long h_d_id;

   private long h_w_id;

   private long h_date;

   private double h_amount;

   private String h_data;


   public History() {

   }

   public History(long h_c_id, long h_c_d_id, long h_c_w_id, long h_d_id, long h_w_id, Date h_date, double h_amount, String h_data) {
      this.h_c_id = h_c_id;
      this.h_c_d_id = h_c_d_id;
      this.h_c_w_id = h_c_w_id;
      this.h_d_id = h_d_id;
      this.h_w_id = h_w_id;
      this.h_date = (h_date == null) ? -1 : h_date.getTime();
      this.h_amount = h_amount;
      this.h_data = h_data;
   }

   public long getH_c_id() {
      return h_c_id;
   }

   public long getH_c_d_id() {
      return h_c_d_id;
   }

   public long getH_c_w_id() {
      return h_c_w_id;
   }

   public long getH_d_id() {
      return h_d_id;
   }

   public long getH_w_id() {
      return h_w_id;
   }

   public Date getH_date() {
      return (h_date == -1) ? null : new Date(h_date);
   }

   public double getH_amount() {
      return h_amount;
   }

   public String getH_data() {
      return h_data;
   }

   public void setH_c_id(long h_c_id) {
      this.h_c_id = h_c_id;
   }

   public void setH_c_d_id(long h_c_d_id) {
      this.h_c_d_id = h_c_d_id;
   }

   public void setH_c_w_id(long h_c_w_id) {
      this.h_c_w_id = h_c_w_id;
   }

   public void setH_d_id(long h_d_id) {
      this.h_d_id = h_d_id;
   }

   public void setH_w_id(long h_w_id) {
      this.h_w_id = h_w_id;
   }

   public void setH_date(Date h_date) {
      this.h_date = (h_date == null) ? -1 : h_date.getTime();
   }

   public void setH_amount(double h_amount) {
      this.h_amount = h_amount;
   }

   public void setH_data(String h_data) {
      this.h_data = h_data;
   }

}
