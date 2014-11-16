package pt.inesc.gsd.tpcc;

import pt.inesc.gsd.tpcc.transaction.NewOrderTransaction;
import pt.inesc.gsd.tpcc.transaction.OrderStatusTransaction;
import pt.inesc.gsd.tpcc.transaction.PaymentTransaction;
import pt.inesc.gsd.tpcc.transaction.TpccTransaction;
import sun.rmi.runtime.Log;

public class TpccTerminal {

   public final static int NEW_ORDER = 1, PAYMENT = 2, ORDER_STATUS = 3, DELIVERY = 4, STOCK_LEVEL = 5;

   public final static String[] nameTokens = {"BAR", "OUGHT"/*, "ABLE", "PRI", "PRES", "ESE", "ANTI", "CALLY", "ATION", "EING"*/};

   private double paymentWeight;

   private double orderStatusWeight;

   private int localWarehouseID;

   private final TpccTools tpccTools;


   public TpccTerminal(double paymentWeight, double orderStatusWeight, int localWarehouseID) {
      this.paymentWeight = paymentWeight;
      this.orderStatusWeight = orderStatusWeight;
      this.localWarehouseID = localWarehouseID;
      tpccTools = TpccTools.newInstance();
   }

   public synchronized final TpccTransaction createTransaction(int type) {
      switch (type) {
         case PAYMENT:
            return new PaymentTransaction(tpccTools, localWarehouseID);
         case ORDER_STATUS:
            return new OrderStatusTransaction(tpccTools, localWarehouseID);
         case NEW_ORDER:
            return new NewOrderTransaction(tpccTools, localWarehouseID);
         case DELIVERY:
         case STOCK_LEVEL:
         default:
            return null;
      }
   }

   public synchronized final TpccTransaction choiceTransaction() {
      return createTransaction(chooseTransactionType());
   }

   public synchronized final int chooseTransactionType() {
      double transactionType = Math.min(tpccTools.doubleRandomNumber(1, 100), 100.0);

      double realPaymentWeight = paymentWeight, realOrderStatusWeight = orderStatusWeight;

      if (transactionType <= realPaymentWeight) {
         return PAYMENT;
      } else if (transactionType <= realPaymentWeight + realOrderStatusWeight) {
         return ORDER_STATUS;
      } else {
         return NEW_ORDER;
      }
   }

   public synchronized void change(int localWarehouseID, double paymentWeight, double orderStatusWeight) {
      setLocalWarehouseID(localWarehouseID);
      setPercentages(paymentWeight, orderStatusWeight);
   }

   public synchronized void setPercentages(double paymentWeight, double orderStatusWeight) {
      this.paymentWeight = paymentWeight;
      this.orderStatusWeight = orderStatusWeight;
   }

   public synchronized void setLocalWarehouseID(int localWarehouseID) {
      this.localWarehouseID = localWarehouseID;
   }

   @Override
   public String toString() {
      return "TpccTerminal{" +
            "paymentWeight=" + paymentWeight +
            ", orderStatusWeight=" + orderStatusWeight +            
            ", localWarehouseID=" + (localWarehouseID == -1 ? "random" : localWarehouseID) +
            '}';
   }
}
