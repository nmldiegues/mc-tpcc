package pt.inesc.gsd.tpcc.domain;

import java.util.List;
import java.util.Map;

import jvstm.VBox;

public class District {

   private long d_id;

   /* warehouse id */
   private long d_w_id;

   /* size 10 */
   private String d_name;

   /* max size 20 */
   private String d_street1;

   /* max size 20 */
   private String d_street2;

   /* max size 20 */
   private String d_city;

   /* size 2 */
   private String d_state;

   /* size 9 */
   private String d_zip;

   private double d_tax;

   private VBox<Double> d_ytd;

   private VBox<Long> d_next_o_id;

   public VBox<List<Customer>> customers;
   public VBox<Map<String, List<Customer>>> customersByName;

   public District() {
   }

   public District(long d_w_id, long d_id, String d_name, String d_street1, String d_street2, String d_city, String d_state, String d_zip, double d_tax, double d_ytd, long d_next_o_id) {
      this.d_w_id = d_w_id;
      this.d_id = d_id;
      this.d_name = d_name;
      this.d_street1 = d_street1;
      this.d_street2 = d_street2;
      this.d_city = d_city;
      this.d_state = d_state;
      this.d_zip = d_zip;
      this.d_tax = d_tax;
      this.d_ytd = new VBox<Double>(d_ytd);
      this.d_next_o_id = new VBox<Long>(d_next_o_id);
   }


   public long getD_w_id() {
      return d_w_id;
   }

   public long getD_id() {
      return d_id;
   }

   public String getD_name() {
      return d_name;
   }

   public String getD_street1() {
      return d_street1;
   }

   public String getD_street2() {
      return d_street2;
   }

   public String getD_city() {
      return d_city;
   }

   public String getD_state() {
      return d_state;
   }

   public String getD_zip() {
      return d_zip;
   }

   public double getD_tax() {
      return d_tax;
   }

   public Double getD_ytd() {
	  return d_ytd.get();
   }

   public long getD_next_o_id() {
      return d_next_o_id.get();
   }

   public void setD_w_id(long d_w_id) {
      this.d_w_id = d_w_id;
   }

   public void setD_id(long d_id) {
      this.d_id = d_id;
   }

   public void setD_name(String d_name) {
      this.d_name = d_name;
   }

   public void setD_street1(String d_street1) {
      this.d_street1 = d_street1;
   }

   public void setD_street2(String d_street2) {
      this.d_street2 = d_street2;
   }

   public void setD_city(String d_city) {
      this.d_city = d_city;
   }

   public void setD_state(String d_state) {
      this.d_state = d_state;
   }

   public void setD_zip(String d_zip) {
      this.d_zip = d_zip;
   }

   public void setD_tax(double d_tax) {
      this.d_tax = d_tax;
   }

   public void setD_ytd(double d_ytd) {
	   this.d_ytd.put(d_ytd);
   }

   public void setD_next_o_id(long d_next_o_id) {
      this.d_next_o_id.put(d_next_o_id);
   }

}
