package pt.inesc.gsd.tpcc;

import java.util.Map;

public class TpccPopulationStressor {

   private int numWarehouses;

   private long cLastMask = 255L;

   
   private long olIdMask = 8191L;

   private long cIdMask = 1023L;

   public Map<String, String> stress() {
      try {
         performPopulationOperations();
      } catch (Exception e) {
         System.err.println("Received exception during cache population" + e.getMessage());
         System.exit(-1);
      }
      return null;
   }

   public void performPopulationOperations() throws Exception {

      TpccPopulation tpccPopulation = new TpccPopulation(this.numWarehouses, this.cLastMask, this.olIdMask, this.cIdMask);
      tpccPopulation.performPopulation();
   }

   public void setNumWarehouses(int numWarehouses) {
      this.numWarehouses = numWarehouses;
   }

   public void setCLastMask(long cLastMask) {
      this.cLastMask = cLastMask;
   }

   public void setOlIdMask(long olIdMask) {
      this.olIdMask = olIdMask;
   }

   public void setCIdMask(long cIdMask) {
      this.cIdMask = cIdMask;
   }

}
