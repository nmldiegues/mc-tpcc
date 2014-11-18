package pt.inesc.gsd.tpcc;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;

import jvstm.Transaction;
import pt.inesc.gsd.tpcc.transaction.NewOrderTransaction;
import pt.inesc.gsd.tpcc.transaction.PaymentTransaction;
import pt.inesc.gsd.tpcc.transaction.TpccTransaction;


public class TpccStressor {

   private int numOfThreads = 10;
   private long perThreadSimulTime = 30L;
   private int arrivalRate = 0;
   private int paymentWeight = 45;
   private int orderStatusWeight = 5;
   private String numberOfItemsInterval = null;
   private int parallelNestedThreads = 0;

   private long startTime;
   private long endTime;
   private volatile CountDownLatch startPoint;
   private boolean running = true;

   private final Timer finishBenchmarkTimer = new Timer("Finish-Benchmark-Timer");

   private final List<Stressor> stressors = new LinkedList<Stressor>();
   private final List<Integer> listLocalWarehouses = new LinkedList<Integer>();

   public Map<String, String> stress() {
      validateTransactionsWeight();
      updateNumberOfItemsInterval();

      initializeToolsParameters();

      startTime = System.currentTimeMillis();

      finishBenchmarkTimer.schedule(new TimerTask() {
         @Override
         public void run() {
            finishBenchmark();
         }
      }, perThreadSimulTime * 1000);

      try {
         executeOperations();
      } catch (Exception e) {
         throw new RuntimeException(e);
      }
      finishBenchmarkTimer.cancel();
      return processResults(stressors);
   }

   private void validateTransactionsWeight() {
      int sum = orderStatusWeight + paymentWeight;
      if (sum < 0 || sum > 100) {
         throw new IllegalArgumentException("The sum of the transactions weights must be higher or equals than zero " +
                                                  "and less or equals than one hundred");
      }
   }

   private void updateNumberOfItemsInterval() {
      if (numberOfItemsInterval == null) {
         return;
      }
      String[] split = numberOfItemsInterval.split(",");

      if (split.length != 2) {
         System.err.println("Cannot update the min and max values for the number of items in new order transactions. " +
                        "Using the default values");
         return;
      }

      try {
         TpccTools.NUMBER_OF_ITEMS_INTERVAL[0] = Integer.parseInt(split[0]);
      } catch (NumberFormatException nfe) {
         System.err.println("Min value is not a number. " + nfe.getLocalizedMessage());
      }

      try {
         TpccTools.NUMBER_OF_ITEMS_INTERVAL[1] = Integer.parseInt(split[1]);
      } catch (NumberFormatException nfe) {
         System.err.println("Max value is not a number. " + nfe.getLocalizedMessage());
      }
   }

   private void initializeToolsParameters() {
      try {
    	  TpccTools tpccTools = TpccTools.newInstance();
         TpccTools.C_C_LAST = tpccTools.randomNumber(0, TpccTools.A_C_LAST);
         TpccTools.C_C_ID = tpccTools.randomNumber(0, TpccTools.A_C_ID);
         TpccTools.C_OL_I_ID = tpccTools.randomNumber(0, TpccTools.A_OL_I_ID);

      } catch (Exception e) {
         System.err.println("Error in initialize parameters: " + e);
      }
   }

   private Map<String, String> processResults(List<Stressor> stressors) {

      long duration = 0;

      int reads = 0;
      int writes = 0;
      int newOrderTransactions = 0;
      int paymentTransactions = 0;

      int failures = 0;
      int rdFailures = 0;
      int wrFailures = 0;
      int newOrderFailures = 0;
      int paymentFailures = 0;
      int appFailures = 0;

      for (Stressor stressor : stressors) {
         reads += stressor.reads;
         writes += stressor.writes;
         newOrderTransactions += stressor.newOrder;
         paymentTransactions += stressor.payment;

         failures += stressor.nrFailures;
         rdFailures += stressor.nrRdFailures;
         wrFailures += stressor.nrWrFailures;
         newOrderFailures += stressor.nrNewOrderFailures;
         paymentFailures += stressor.nrPaymentFailures;
         appFailures += stressor.appFailures;
      }

      Map<String, String> results = new LinkedHashMap<String, String>();

      duration = endTime - startTime;

      results.put("DURATION (msec)", str(duration));
      double requestPerSec = (reads + writes) / (duration / 1000.0);
      results.put("REQ_PER_SEC", str(requestPerSec));

      double wrtPerSec = 0;
      double rdPerSec = 0;
      double newOrderPerSec = 0;
      double paymentPerSec = 0;

      if (duration == 0)
         results.put("READS_PER_SEC", str(0));
      else {
         rdPerSec = reads / (duration / 1000.0);
         results.put("READS_PER_SEC", str(rdPerSec));
      }

      if (duration == 0)
         results.put("WRITES_PER_SEC", str(0));
      else {
         wrtPerSec = writes / (duration / 1000.0);
         results.put("WRITES_PER_SEC", str(wrtPerSec));
      }

      if (duration == 0)
         results.put("NEW_ORDER_PER_SEC", str(0));
      else {
         newOrderPerSec = newOrderTransactions / (duration / 1000.0);
         results.put("NEW_ORDER_PER_SEC", str(newOrderPerSec));
      }
      if (duration == 0)
         results.put("PAYMENT_PER_SEC", str(0));
      else {
         paymentPerSec = paymentTransactions / (duration / 1000.0);
         results.put("PAYMENT_PER_SEC", str(paymentPerSec));
      }

      results.put("READ_COUNT", str(reads));
      results.put("WRITE_COUNT", str(writes));
      results.put("NEW_ORDER_COUNT", str(newOrderTransactions));
      results.put("PAYMENT_COUNT", str(paymentTransactions));
      results.put("FAILURES", str(failures));
      results.put("APPLICATION_FAILURES", str(appFailures));
      results.put("WRITE_FAILURES", str(wrFailures));
      results.put("NEW_ORDER_FAILURES", str(newOrderFailures));
      results.put("PAYMENT_FAILURES", str(paymentFailures));
      results.put("READ_FAILURES", str(rdFailures));

      System.out.println("Finished generating report. Nr of failed operations on this node is: " + failures +
                     ". Test duration is: " + Utils.getMillisDurationString(System.currentTimeMillis() - startTime));
      return results;
   }

   private List<Stressor> executeOperations() throws Exception {
      startPoint = new CountDownLatch(1);
      for (int threadIndex = 0; threadIndex < numOfThreads; threadIndex++) {
         Stressor stressor = createStressor(threadIndex);
         stressors.add(stressor);
         stressor.start();
      }
      startPoint.countDown();
      blockWhileRunning();
      for (Stressor stressor : stressors) {
         stressor.join();
      }

      endTime = System.currentTimeMillis();
      return stressors;
   }

   private class Stressor extends Thread {

      private final TpccTerminal terminal;

      private final int parallelNestedTxs;
      
      private int nrFailures = 0;
      private int nrWrFailures = 0;
      private int nrRdFailures = 0;
      private int nrNewOrderFailures = 0;
      private int nrPaymentFailures = 0;
      private int appFailures = 0;

      private long reads = 0L;
      private long writes = 0L;
      private long payment = 0L;
      private long newOrder = 0L;

      private boolean running = true;
      private boolean active = true;

      public Stressor(int localWarehouseID, int threadIndex, double arrivalRate,
                      double paymentWeight, double orderStatusWeight, int parallelNestedTxs) {
         super("Stressor-" + threadIndex);
         this.terminal = new TpccTerminal(paymentWeight, orderStatusWeight, localWarehouseID);
         this.parallelNestedTxs = parallelNestedTxs;
      }

      @Override
      public void run() {

         try {
            startPoint.await();
            System.out.println("Starting thread: " + getName());
         } catch (InterruptedException e) {
            System.err.println("Interrupted while waiting for starting in " + getName());
         }

         TpccTransaction transaction;

         boolean isReadOnly;
         boolean successful;
         transaction = null;
         boolean notfound = false;

         while (assertRunning()) {
             notfound = false;
            successful = true;

            if (transaction == null) {
        	    transaction = terminal.choiceTransaction();
            }
            isReadOnly = transaction.isReadOnly();

            Transaction tx = Transaction.begin(isReadOnly);

            try {
               transaction.executeTransaction(parallelNestedTxs);
            } catch (Throwable e) {
            	tx.abort();
               successful = false;
            }

            //here we try to finalize the transaction
            //if any read/write has failed we abort
            try {
               if (successful) {
            	   tx.commit();
               }

               if (!successful) {
                  nrFailures++;
                  if (!isReadOnly) {
                     nrWrFailures++;
                     if (transaction instanceof NewOrderTransaction) {
                        nrNewOrderFailures++;
                     } else if (transaction instanceof PaymentTransaction) {
                        nrPaymentFailures++;
                     }

                  } else {
                     nrRdFailures++;
                  }

               }
            } catch (Throwable rb) {
         	   tx.abort();
         	   
               nrFailures++;

               if (!isReadOnly) {
                  nrWrFailures++;
                  if (transaction instanceof NewOrderTransaction) {
                     nrNewOrderFailures++;
                  } else if (transaction instanceof PaymentTransaction) {
                     nrPaymentFailures++;
                  }
               } else {
                  nrRdFailures++;
               }
               successful = false;
            }

            if (!isReadOnly) {
               if (successful) {
                  writes++;
                  if (transaction instanceof PaymentTransaction) {
                     payment++;
                  } else if (transaction instanceof NewOrderTransaction) {
                     newOrder++;
                  }
               }
            } else {
               if (successful) {
                  reads++;
               }
            }

            if (successful || notfound) {
        	transaction = null;
            }
            
            blockIfInactive();
         }
      }

      private synchronized boolean assertRunning() {
         return running;
      }

      public final synchronized void finish() {
         active = true;
         running = false;
         notifyAll();
      }

      private synchronized void blockIfInactive() {
         while (!active) {
            try {
               wait();
            } catch (InterruptedException e) {
               Thread.currentThread().interrupt();
            }
         }
      }
   }

   private String str(Object o) {
      return String.valueOf(o);
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
   
   public void setParallelNestedSiblings(int siblings) {
       this.parallelNestedThreads = siblings;
   }

   private synchronized void finishBenchmark() {
      if (!running) {
         return;
      }
      running = false;
      for (Stressor stressor : stressors) {
         stressor.finish();
      }
      notifyAll();
   }

   private Stressor createStressor(int threadIndex) {
      int localWarehouse = getWarehouseForThread(threadIndex);
      return new Stressor(localWarehouse, threadIndex, arrivalRate, paymentWeight,orderStatusWeight, this.parallelNestedThreads);
   }

   private synchronized void blockWhileRunning() throws InterruptedException {
      while (running) {
         wait();
      }
   }

   private int getWarehouseForThread(int threadIdx) {
      return listLocalWarehouses.isEmpty() ? -1 : listLocalWarehouses.get(threadIdx % listLocalWarehouses.size());
   }

}
