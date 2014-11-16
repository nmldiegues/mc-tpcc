package pt.inesc.gsd.tpcc.domain;

import jvstm.VBox;


public class Stock {

   private long s_i_id;

   private long s_w_id;

   private VBox<Long> s_quantity;

   private String s_dist_01;

   private String s_dist_02;

   private String s_dist_03;

   private String s_dist_04;

   private String s_dist_05;

   private String s_dist_06;

   private String s_dist_07;

   private String s_dist_08;

   private String s_dist_09;

   private String s_dist_10;

   private VBox<Long> s_ytd;

   private VBox<Integer> s_order_cnt;

   private VBox<Integer> s_remote_cnt;

   private String s_data;


   public Stock() {

   }

   public Stock(long s_i_id, long s_w_id, long s_quantity, String s_dist_01, String s_dist_02, String s_dist_03, String s_dist_04, String s_dist_05, String s_dist_06, String s_dist_07, String s_dist_08, String s_dist_09, String s_dist_10, long s_ytd, int s_order_cnt, int s_remote_cnt, String s_data) {
      this.s_i_id = s_i_id;
      this.s_w_id = s_w_id;
      this.s_quantity = new VBox<Long>(s_quantity);
      this.s_dist_01 = s_dist_01;
      this.s_dist_02 = s_dist_02;
      this.s_dist_03 = s_dist_03;
      this.s_dist_04 = s_dist_04;
      this.s_dist_05 = s_dist_05;
      this.s_dist_06 = s_dist_06;
      this.s_dist_07 = s_dist_07;
      this.s_dist_08 = s_dist_08;
      this.s_dist_09 = s_dist_09;
      this.s_dist_10 = s_dist_10;
      this.s_ytd = new VBox<Long>(s_ytd);
      this.s_order_cnt = new VBox<Integer>(s_order_cnt);
      this.s_remote_cnt = new VBox<Integer>(s_remote_cnt);
      this.s_data = s_data;
   }

   public long getS_i_id() {
      return s_i_id;
   }

   public long getS_w_id() {
      return s_w_id;
   }

   public long getS_quantity() {
      return s_quantity.get();
   }

   public String getS_dist_01() {
      return s_dist_01;
   }

   public String getS_dist_02() {
      return s_dist_02;
   }

   public String getS_dist_03() {
      return s_dist_03;
   }

   public String getS_dist_04() {
      return s_dist_04;
   }

   public String getS_dist_05() {
      return s_dist_05;
   }

   public String getS_dist_06() {
      return s_dist_06;
   }

   public String getS_dist_07() {
      return s_dist_07;
   }

   public String getS_dist_08() {
      return s_dist_08;
   }

   public String getS_dist_09() {
      return s_dist_09;
   }

   public String getS_dist_10() {
      return s_dist_10;
   }

   public long getS_ytd() {
      return s_ytd.get();
   }

   public int getS_order_cnt() {
      return s_order_cnt.get();
   }

   public int getS_remote_cnt() {
      return s_remote_cnt.get();
   }

   public String getS_data() {
      return s_data;
   }

   public void setS_i_id(long s_i_id) {
      this.s_i_id = s_i_id;
   }

   public void setS_w_id(long s_w_id) {
      this.s_w_id = s_w_id;
   }

   public void setS_quantity(long s_quantity) {
      this.s_quantity.put(s_quantity);
   }

   public void setS_dist_01(String s_dist_01) {
      this.s_dist_01 = s_dist_01;
   }

   public void setS_dist_02(String s_dist_02) {
      this.s_dist_02 = s_dist_02;
   }

   public void setS_dist_03(String s_dist_03) {
      this.s_dist_03 = s_dist_03;
   }

   public void setS_dist_04(String s_dist_04) {
      this.s_dist_04 = s_dist_04;
   }

   public void setS_dist_05(String s_dist_05) {
      this.s_dist_05 = s_dist_05;
   }

   public void setS_dist_06(String s_dist_06) {
      this.s_dist_06 = s_dist_06;
   }

   public void setS_dist_07(String s_dist_07) {
      this.s_dist_07 = s_dist_07;
   }

   public void setS_dist_08(String s_dist_08) {
      this.s_dist_08 = s_dist_08;
   }

   public void setS_dist_09(String s_dist_09) {
      this.s_dist_09 = s_dist_09;
   }

   public void setS_dist_10(String s_dist_10) {
      this.s_dist_10 = s_dist_10;
   }

   public void setS_ytd(long s_ytd) {
      this.s_ytd.put(s_ytd);
   }

   public void setS_order_cnt(int s_order_cnt) {
      this.s_order_cnt.put(s_order_cnt);
   }

   public void setS_remote_cnt(int s_remote_cnt) {
      this.s_remote_cnt.put(s_remote_cnt);
   }

   public void setS_data(String s_data) {
      this.s_data = s_data;
   }

}
