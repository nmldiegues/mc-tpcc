package pt.inesc.gsd.tpcc.transaction;

public interface TpccTransaction {

   void executeTransaction() throws Throwable;

   boolean isReadOnly();
}
