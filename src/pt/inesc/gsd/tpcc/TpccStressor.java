package pt.inesc.gsd.tpcc;

import java.util.Iterator;
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
      int nrWrFailuresOnCommit = 0;
      int newOrderFailures = 0;
      int paymentFailures = 0;
      int appFailures = 0;

      long readsDurations = 0L;
      long writesDurations = 0L;
      long newOrderDurations = 0L;
      long paymentDurations = 0L;
      long successful_writesDurations = 0L;
      long successful_readsDurations = 0L;
      long writeServiceTimes = 0L;
      long readServiceTimes = 0L;
      long newOrderServiceTimes = 0L;
      long paymentServiceTimes = 0L;

      long successful_commitWriteDurations = 0L;
      long aborted_commitWriteDurations = 0L;
      long commitWriteDurations = 0L;

      long writeInQueueTimes = 0L;
      long readInQueueTimes = 0L;
      long newOrderInQueueTimes = 0L;
      long paymentInQueueTimes = 0L;
      long numWritesDequeued = 0L;
      long numReadsDequeued = 0L;
      long numNewOrderDequeued = 0L;
      long numPaymentDequeued = 0L;

      for (Stressor stressor : stressors) {
         //duration += stressor.totalDuration(); //in nanosec
         //readsDurations += stressor.readDuration; //in nanosec
         //writesDurations += stressor.writeDuration; //in nanosec
         newOrderDurations += stressor.newOrderDuration; //in nanosec
         paymentDurations += stressor.paymentDuration; //in nanosec
         successful_writesDurations += stressor.successful_writeDuration; //in nanosec
         successful_readsDurations += stressor.successful_readDuration; //in nanosec

         successful_commitWriteDurations += stressor.successful_commitWriteDuration; //in nanosec
         aborted_commitWriteDurations += stressor.aborted_commitWriteDuration; //in nanosec
         commitWriteDurations += stressor.commitWriteDuration; //in nanosec;

         writeServiceTimes += stressor.writeServiceTime;
         readServiceTimes += stressor.readServiceTime;
         newOrderServiceTimes += stressor.newOrderServiceTime;
         paymentServiceTimes += stressor.paymentServiceTime;

         reads += stressor.reads;
         writes += stressor.writes;
         newOrderTransactions += stressor.newOrder;
         paymentTransactions += stressor.payment;

         failures += stressor.nrFailures;
         rdFailures += stressor.nrRdFailures;
         wrFailures += stressor.nrWrFailures;
         nrWrFailuresOnCommit += stressor.nrWrFailuresOnCommit;
         newOrderFailures += stressor.nrNewOrderFailures;
         paymentFailures += stressor.nrPaymentFailures;
         appFailures += stressor.appFailures;

         writeInQueueTimes += stressor.writeInQueueTime;
         readInQueueTimes += stressor.readInQueueTime;
         newOrderInQueueTimes += stressor.newOrderInQueueTime;
         paymentInQueueTimes += stressor.paymentInQueueTime;
         numWritesDequeued += stressor.numWriteDequeued;
         numReadsDequeued += stressor.numReadDequeued;
         numNewOrderDequeued += stressor.numNewOrderDequeued;
         numPaymentDequeued += stressor.numPaymentDequeued;
      }

      //duration = duration / 1000000; // nanosec to millisec
      //readsDurations = readsDurations / 1000; //nanosec to microsec
      //writesDurations = writesDurations / 1000; //nanosec to microsec
      //newOrderDurations = newOrderDurations / 1000; //nanosec to microsec
      //paymentDurations = paymentDurations / 1000;//nanosec to microsec
      successful_readsDurations = successful_readsDurations / 1000; //nanosec to microsec
      successful_writesDurations = successful_writesDurations / 1000; //nanosec to microsec
      successful_commitWriteDurations = successful_commitWriteDurations / 1000; //nanosec to microsec
      aborted_commitWriteDurations = aborted_commitWriteDurations / 1000; //nanosec to microsec
      commitWriteDurations = commitWriteDurations / 1000; //nanosec to microsec
      writeServiceTimes = writeServiceTimes / 1000; //nanosec to microsec
      readServiceTimes = readServiceTimes / 1000; //nanosec to microsec
      newOrderServiceTimes = newOrderServiceTimes / 1000; //nanosec to microsec
      paymentServiceTimes = paymentServiceTimes / 1000; //nanosec to microsec

      writeInQueueTimes = writeInQueueTimes / 1000;//nanosec to microsec
      readInQueueTimes = readInQueueTimes / 1000;//nanosec to microsec
      newOrderInQueueTimes = newOrderInQueueTimes / 1000;//nanosec to microsec
      paymentInQueueTimes = paymentInQueueTimes / 1000;//nanosec to microsec

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

      if ((reads + writes) != 0)
         results.put("AVG_SUCCESSFUL_DURATION (usec)", str((successful_writesDurations + successful_readsDurations) / (reads + writes)));
      else
         results.put("AVG_SUCCESSFUL_DURATION (usec)", str(0));


      if (reads != 0)
         results.put("AVG_SUCCESSFUL_READ_DURATION (usec)", str(successful_readsDurations / reads));
      else
         results.put("AVG_SUCCESSFUL_READ_DURATION (usec)", str(0));


      if (writes != 0)
         results.put("AVG_SUCCESSFUL_WRITE_DURATION (usec)", str(successful_writesDurations / writes));
      else
         results.put("AVG_SUCCESSFUL_WRITE_DURATION (usec)", str(0));


      if (writes != 0) {
         results.put("AVG_SUCCESSFUL_COMMIT_WRITE_DURATION (usec)", str((successful_commitWriteDurations / writes)));
      } else {
         results.put("AVG_SUCCESSFUL_COMMIT_WRITE_DURATION (usec)", str(0));
      }

      if (nrWrFailuresOnCommit != 0) {
         results.put("AVG_ABORTED_COMMIT_WRITE_DURATION (usec)", str((aborted_commitWriteDurations / nrWrFailuresOnCommit)));
      } else {
         results.put("AVG_ABORTED_COMMIT_WRITE_DURATION (usec)", str(0));
      }


      if (writes + nrWrFailuresOnCommit != 0) {
         results.put("AVG_COMMIT_WRITE_DURATION (usec)", str((commitWriteDurations / (writes + nrWrFailuresOnCommit))));
      } else {
         results.put("AVG_COMMIT_WRITE_DURATION (usec)", str(0));
      }

      if ((reads + rdFailures) != 0)
         results.put("AVG_RD_SERVICE_TIME (usec)", str(readServiceTimes / (reads + rdFailures)));
      else
         results.put("AVG_RD_SERVICE_TIME (usec)", str(0));

      if ((writes + wrFailures) != 0)
         results.put("AVG_WR_SERVICE_TIME (usec)", str(writeServiceTimes / (writes + wrFailures)));
      else
         results.put("AVG_WR_SERVICE_TIME (usec)", str(0));

      if ((newOrderTransactions + newOrderFailures) != 0)
         results.put("AVG_NEW_ORDER_SERVICE_TIME (usec)", str(newOrderServiceTimes / (newOrderTransactions + newOrderFailures)));
      else
         results.put("AVG_NEW_ORDER_SERVICE_TIME (usec)", str(0));

      if ((paymentTransactions + paymentFailures) != 0)
         results.put("AVG_PAYMENT_SERVICE_TIME (usec)", str(paymentServiceTimes / (paymentTransactions + paymentFailures)));
      else
         results.put("AVG_PAYMENT_SERVICE_TIME (usec)", str(0));

      if (numWritesDequeued != 0)
         results.put("AVG_WR_INQUEUE_TIME (usec)", str(writeInQueueTimes / numWritesDequeued));
      else
         results.put("AVG_WR_INQUEUE_TIME (usec)", str(0));
      if (numReadsDequeued != 0)
         results.put("AVG_RD_INQUEUE_TIME (usec)", str(readInQueueTimes / numReadsDequeued));
      else
         results.put("AVG_RD_INQUEUE_TIME (usec)", str(0));
      if (numNewOrderDequeued != 0)
         results.put("AVG_NEW_ORDER_INQUEUE_TIME (usec)", str(newOrderInQueueTimes / numNewOrderDequeued));
      else
         results.put("AVG_NEW_ORDER_INQUEUE_TIME (usec)", str(0));
      if (numPaymentDequeued != 0)
         results.put("AVG_PAYMENT_INQUEUE_TIME (usec)", str(paymentInQueueTimes / numPaymentDequeued));
      else
         results.put("AVG_PAYMENT_INQUEUE_TIME (usec)", str(0));

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
      private int threadIndex;
      private double arrivalRate;

      private final TpccTerminal terminal;

      private int nrFailures = 0;
      private int nrWrFailures = 0;
      private int nrWrFailuresOnCommit = 0;
      private int nrRdFailures = 0;
      private int nrNewOrderFailures = 0;
      private int nrPaymentFailures = 0;
      private int appFailures = 0;

      private long readDuration = 0L;
      private long writeDuration = 0L;
      private long newOrderDuration = 0L;
      private long paymentDuration = 0L;
      private long successful_commitWriteDuration = 0L;
      private long aborted_commitWriteDuration = 0L;
      private long commitWriteDuration = 0L;

      private long writeServiceTime = 0L;
      private long newOrderServiceTime = 0L;
      private long paymentServiceTime = 0L;
      private long readServiceTime = 0L;

      private long successful_writeDuration = 0L;
      private long successful_readDuration = 0L;

      private long reads = 0L;
      private long writes = 0L;
      private long payment = 0L;
      private long newOrder = 0L;

      private long numWriteDequeued = 0L;
      private long numReadDequeued = 0L;
      private long numNewOrderDequeued = 0L;
      private long numPaymentDequeued = 0L;

      private long writeInQueueTime = 0L;
      private long readInQueueTime = 0L;
      private long newOrderInQueueTime = 0L;
      private long paymentInQueueTime = 0L;

      private boolean running = true;
      private boolean active = true;

      public Stressor(int localWarehouseID, int threadIndex, double arrivalRate,
                      double paymentWeight, double orderStatusWeight) {
         super("Stressor-" + threadIndex);
         this.threadIndex = threadIndex;
         this.arrivalRate = arrivalRate;
         this.terminal = new TpccTerminal(paymentWeight, orderStatusWeight, localWarehouseID);
      }

      @Override
      public void run() {

         try {
            startPoint.await();
            System.out.println("Starting thread: " + getName());
         } catch (InterruptedException e) {
            System.err.println("Interrupted while waiting for starting in " + getName());
         }

         long end;

         long commit_start = 0L;

         TpccTransaction transaction;

         boolean isReadOnly;
         boolean successful;
         transaction = null;
         boolean notfound = false;

         while (assertRunning()) {
             notfound = false;
            successful = true;

            long start = System.nanoTime();
            if (transaction == null) {
        	    transaction = terminal.choiceTransaction();
            }
            isReadOnly = transaction.isReadOnly();

            long startService = System.nanoTime();
         
            Transaction tx = Transaction.begin(isReadOnly);

            try {
               transaction.executeTransaction();
            } catch (Throwable e) {
            	tx.abort();
               successful = false;
               if (e instanceof ElementNotFoundException) {
            	   notfound = true;
                  this.appFailures++;
               }
            }

            //here we try to finalize the transaction
            //if any read/write has failed we abort
            boolean measureCommitTime = false;
            try {
               /* In our tests we are interested in the commit time spent for write txs*/
               if (successful && !isReadOnly) {
                  commit_start = System.nanoTime();
                  measureCommitTime = true;
               }

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
                  nrWrFailuresOnCommit++;
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

            end = System.nanoTime();

            if (this.arrivalRate == 0.0) {  //Closed system
               start = startService;
            }

            if (!isReadOnly) {
               writeDuration += end - start;
               writeServiceTime += end - startService;
               if (transaction instanceof NewOrderTransaction) {
                  newOrderDuration += end - start;
                  newOrderServiceTime += end - startService;
               } else if (transaction instanceof PaymentTransaction) {
                  paymentDuration += end - start;
                  paymentServiceTime += end - startService;
               }
               if (successful) {
                  successful_writeDuration += end - startService;
                  writes++;
                  if (transaction instanceof PaymentTransaction) {
                     payment++;
                  } else if (transaction instanceof NewOrderTransaction) {
                     newOrder++;
                  }
               }
            } else {
               readDuration += end - start;
               readServiceTime += end - startService;
               if (successful) {
                  reads++;
                  successful_readDuration += end - startService;
               }
            }

            if (measureCommitTime) {
               if (successful) {
                  this.successful_commitWriteDuration += end - commit_start;
               } else {
                  this.aborted_commitWriteDuration += end - commit_start;
               }
               this.commitWriteDuration += end - commit_start;
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

      public final synchronized void inactive() {
         active = false;
      }

      public final synchronized void active() {
         active = true;
         notifyAll();
      }

      public final synchronized void finish() {
         active = true;
         running = false;
         notifyAll();
      }

      public final synchronized boolean isActive() {
         return active;
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

   public final synchronized void setNumberOfRunningThreads(int numOfThreads) {
      if (numOfThreads < 1 || !running) {
         return;
      }
      Iterator<Stressor> iterator = stressors.iterator();
      while (numOfThreads > 0 && iterator.hasNext()) {
         Stressor stressor = iterator.next();
         if (!stressor.isActive()) {
            stressor.active();
         }
         numOfThreads--;
      }

      if (numOfThreads > 0) {
         int threadIdx = stressors.size();
         while (numOfThreads-- > 0) {
            Stressor stressor = createStressor(threadIdx++);
            stressor.start();
            stressors.add(stressor);
         }
      } else {
         while (iterator.hasNext()) {
            iterator.next().inactive();
         }
      }
   }

   public final synchronized int getNumberOfThreads() {
      return stressors.size();
   }

   public final synchronized int getNumberOfActiveThreads() {
      int count = 0;
      for (Stressor stressor : stressors) {
         if (stressor.isActive()) {
            count++;
         }
      }
      return count;
   }

   private Stressor createStressor(int threadIndex) {
      int localWarehouse = getWarehouseForThread(threadIndex);
      return new Stressor(localWarehouse, threadIndex, arrivalRate, paymentWeight,orderStatusWeight);
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
