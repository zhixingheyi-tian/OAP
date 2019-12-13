# OAP Architecture Overview


* [Introduction](#introduction)
* [OAP Features](#oap-Features)
* [OAP Architecture](#oap-architecture)



## Introduction

Apache Spark is a fast and general-purpose cluster computing system. It provides high-level APIs in Java, Scala, Python and R, and an optimized engine that supports general execution graphs. It also supports a rich set of higher-level tools including Spark SQL for SQL and structured data processing, MLlib for machine learning, GraphX for graph processing, and Spark Streaming.

OAP is designed to leverage the user defined indices and smart fine-grained in-memory data caching strategy for boosting Spark SQL performance on ad-hoc queries.


![OAP-INTRODUCTION](./image/OAP-Introduction.PNG)

#### Usage Situation

Interactive queries usually processes on a large data set but return a small portion of data filtering out with a specific condition. Customers are facing big challenges in meeting the performance requirement of interactive queries as we wants the result returned in seconds instead of tens of minutes or even hours. 

For example, the following query wants to filter out a very small result set from a huge fact table.

```
select query, term, userid, planid, unitid, winfoid, bmm_type, cmatch, charge, wctrl, target_url, audience_targeting_tag, is_url_targeting_adv, pluto_idea_type
from basedata.fc_ad_wise
where (event_day='20180701' and query='xxx' and winfoid='65648180412')
limit 10
```

OAP is a package for Spark to speed up interactive queries (ad-hoc queries) by utilizing index and cache technologies. By properly using index and cache, the performance of some interactive queries can possible be improved by order of magnitude.

## OAP Features

OAP has two major Features:  index and cache, for boosting Spark SQL performance on ad-hoc queries.
Once the user creates indexes using DDL, index files mainly composed of index data and statistics will be generated in a specific directory.

### Index 

Users can use SQL DDL(create/drop/refresh/check/show index) to use OAP index functionality.

- BTREE, BITMAP Index is an optimization that is widely used in traditional databases. We also adopt this two most used index types in OAP project. BTREE index(default in 0.2.0) is intended for datasets that has a lot of distinct values, and distributed randomly, such as telephone number or ID number. BitMap index is intended for datasets with a limited total amount of distinct values, such as state or age.
- Statistics. Sometimes, reading index could bring extra cost for some queries. So we also support four statistics (MinMax, Bloom Filter, SampleBase and PartByValue) to help filter. With statistics, we can make sure we only use index if we can possibly boost the execution. Statistics data locates in the Index file, after all index data written into index file.


### Cache
- OAP cache use Off-Heap memory and stay out of JVM GC. Also OAP cache can use [DCPMM](https://www.intel.com/content/www/us/en/architecture-and-technology/optane-dc-persistent-memory.html) as high-performance, high-capacity, low-cost memory
- Cache-Locality. OAP can schedule computing task to one executor which holds needed data in cache, by implementing a cache aware mechanism based on Spark driver and executors communication.
- Cache granularity. A column in one RowGroup (equivalent to Stripe in ORC) of a column-oriented storage format file is loaded into a basic cache unit which is called "Fiber" in OAP.
- Cache Eviction. OAP cache eviction uses LRU policy, and automatically cache and evict data with transparently to end user.



## OAP Architecture


The following diagram shows the OAP architect design 

![OAP-ARCHITECTURE](./image/OAP-Architecture.PNG)


- OAP (Optimized Analytical Package for Spark) acts as a plugin jar for Spark SQL.  
- OAP implements unified cache representation adaptors for three fileformat: parquet, orc and oap(parquet-like columnar storage data format defined by OAP)  
- OAP's two major optimization functionality index & cache base on unified adaptors.
- Using Spark ThriftServer can unleash the power of OAP. Of course, using bin/spark-sql, bin/spark-shell or bin/pyspark also can.

Generally, the server's DRAM is used as the cache medium. [DCPMM](https://www.intel.com/content/www/us/en/architecture-and-technology/optane-dc-persistent-memory.html) can also be used as the cache medium, it provide a more cost effective solution for high performance environment requirement.



