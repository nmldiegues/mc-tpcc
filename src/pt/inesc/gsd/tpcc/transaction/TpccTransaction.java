package pt.inesc.gsd.tpcc.transaction;

public interface TpccTransaction {

   void executeTransaction(int parallelNestedSiblings) throws Throwable;

   boolean isReadOnly();
}
