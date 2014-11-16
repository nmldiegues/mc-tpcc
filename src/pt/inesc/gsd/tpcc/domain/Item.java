package pt.inesc.gsd.tpcc.domain;

import org.radargun.CacheWrapper;

public class Item {

   private long i_id;

   private long i_im_id;

   private String i_name;

   private double i_price;

   private String i_data;

   public Item() {

   }

   public Item(long i_id, long i_im_id, String i_name, double i_price, String i_data) {
      this.i_id = i_id;
      this.i_im_id = i_im_id;
      this.i_name = i_name;
      this.i_price = i_price;
      this.i_data = i_data;
   }

   public long getI_id() {
      return i_id;
   }

   public long getI_im_id() {
      return i_im_id;
   }

   public String getI_name() {
      return i_name;
   }

   public double getI_price() {
      return i_price;
   }

   public String getI_data() {
      return i_data;
   }

   public void setI_id(long i_id) {
      this.i_id = i_id;
   }

   public void setI_im_id(long i_im_id) {
      this.i_im_id = i_im_id;
   }

   public void setI_name(String i_name) {
      this.i_name = i_name;
   }

   public void setI_price(double i_price) {
      this.i_price = i_price;
   }

   public void setI_data(String i_data) {
      this.i_data = i_data;
   }

}
