package pt.inesc.gsd.tpcc.transaction;

import org.radargun.CacheWrapper;

public interface TpccTransaction {

   void executeTransaction(CacheWrapper cacheWrapper) throws Throwable;

   boolean isReadOnly();
}
