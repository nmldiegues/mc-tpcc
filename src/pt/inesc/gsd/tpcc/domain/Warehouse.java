package pt.inesc.gsd.tpcc.domain;

import java.util.List;

import jvstm.VBox;

import org.radargun.CacheWrapper;

public class Warehouse {

   private long w_id;

   private String w_name;

   private String w_street1;

   private String w_street2;

   private String w_city;

   private String w_state;

   private String w_zip;

   private double w_tax;

   private VBox<Double> w_ytd;

   public VBox<List<Stock>> stocks;
   
   public VBox<List<District>> districts;

   public Warehouse() {

   }

   public Warehouse(CacheWrapper wrapper, long w_id, String w_name, String w_street1, String w_street2, String w_city, String w_state, String w_zip, double w_tax, double w_ytd) {
      this.w_id = w_id;
      this.w_name = w_name;
      this.w_street1 = w_street1;
      this.w_street2 = w_street2;
      this.w_city = w_city;
      this.w_state = w_state;
      this.w_zip = w_zip;
      this.w_tax = w_tax;
      this.w_ytd = new VBox<Double>(w_ytd);
   }

   public void setW_id(long w_id) {
      this.w_id = w_id;
   }

   public void setW_name(String w_name) {
      this.w_name = w_name;
   }

   public void setW_street1(String w_street1) {
      this.w_street1 = w_street1;
   }

   public void setW_street2(String w_street2) {
      this.w_street2 = w_street2;
   }

   public void setW_city(String w_city) {
      this.w_city = w_city;
   }

   public void setW_state(String w_state) {
      this.w_state = w_state;
   }

   public void setW_zip(String w_zip) {
      this.w_zip = w_zip;
   }

   public void setW_tax(double w_tax) {
      this.w_tax = w_tax;
   }

   public void setW_ytd(double w_ytd) {
      this.w_ytd.put(w_ytd);
   }
   
   public long getW_id() {

      return w_id;
   }

   public String getW_name() {
      return w_name;
   }

   public String getW_street1() {
      return w_street1;
   }

   public String getW_street2() {
      return w_street2;
   }

   public String getW_city() {
      return w_city;
   }

   public String getW_state() {
      return w_state;
   }

   public String getW_zip() {
      return w_zip;
   }

   public double getW_tax() {
      return w_tax;
   }

   public Double getW_ytd() {
	  return w_ytd.get();
   }

}
