TPC-C Benchmark for Multi-Core Synchronization Evaluation
=======

This is a rough porting of TPC-C, without intent of being complete in terms of durability/logic, to stress a Transactional Memory based concurrency control scheme in a multi-core setting.

For this I used the Java Versioned Software Transactional Memory (JVSTM) API, on which this code depends, although that is well confined to the domain and transactions so it should be fairly easy to change to another implementation (or proxy the API).

