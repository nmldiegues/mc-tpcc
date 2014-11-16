
package pt.inesc.gsd.tpcc;


public class TpccPopulationStage {

   private int numWarehouses = 1;
   private long cLastMask = 255;
   private long olIdMask = 8191;
   private long cIdMask = 1023;

   public void populate() {
      TpccPopulationStressor populationStressor = new TpccPopulationStressor();
      populationStressor.setNumWarehouses(numWarehouses);
      populationStressor.setCLastMask(this.cLastMask);
      populationStressor.setOlIdMask(this.olIdMask);
      populationStressor.setCIdMask(this.cIdMask);
      populationStressor.stress();
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
