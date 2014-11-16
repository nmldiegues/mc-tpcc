package pt.inesc.gsd.tpcc;

import java.util.Map;

public class TpccBenchmarkStage {

   private int numOfThreads = 10;
   private long perThreadSimulTime = 180L;
   private int arrivalRate = 0;
   private int paymentWeight = 45;
   private int orderStatusWeight = 5;
   private String numberOfItemsInterval = null;
   private transient TpccStressor tpccStressor;

   public Map<String, String> executeOnSlave() {
      tpccStressor = new TpccStressor();
      tpccStressor.setNumOfThreads(this.numOfThreads);
      tpccStressor.setPerThreadSimulTime(this.perThreadSimulTime);
      tpccStressor.setArrivalRate(this.arrivalRate);
      tpccStressor.setPaymentWeight(this.paymentWeight);
      tpccStressor.setOrderStatusWeight(this.orderStatusWeight);
      tpccStressor.setNumberOfItemsInterval(numberOfItemsInterval);

      try {
         return tpccStressor.stress();
      } catch (Exception e) {
         System.err.println("Exception while initializing the test " + e);
         System.exit(1);
      }
      return null;
   }

   public void setNumOfThreads(int numOfThreads) {
      this.numOfThreads = numOfThreads;
   }

   public void setPerThreadSimulTime(long perThreadSimulTime) {
      this.perThreadSimulTime = perThreadSimulTime;
   }

   public void setArrivalRate(int arrivalRate) {
      this.arrivalRate = arrivalRate;
   }

   public void setPaymentWeight(int paymentWeight) {
      this.paymentWeight = paymentWeight;
   }

   public void setOrderStatusWeight(int orderStatusWeight) {
      this.orderStatusWeight = orderStatusWeight;
   }

   public void setNumberOfItemsInterval(String numberOfItemsInterval) {
      this.numberOfItemsInterval = numberOfItemsInterval;
   }

}
