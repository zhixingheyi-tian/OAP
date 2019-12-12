# OAP Developer Guide

* [Integration with Spark](#integration-with-spark)
* [OAP without DCPMM](#OAP-without-dcpmm)
* [OAP with DCPMM](#OAP-with-dcpmm)
* [Enable Numa binding for DCPMM in Spark](#enable-numa-binding-for-dcpmm-in-spark)


## Integration with Spark

Although OAP acts as a plugin jar to Spark, there are still a few tricks to note when integration with Spark. 
We made a few improvements or changes to the Spark Source Codes for OAP features. So when integrating OAP on Spark, you need to check whether you are running an unmodified Community Spark or a modified customized Spark.

#### Integrate with Community Spark

You don't need to care about anything. Just refer to xxx

#### Integrate with customized Spark

OAP modified 13 Spark source code files. As follows.

```
•	antlr4/org/apache/spark/sql/catalyst/parser/SqlBase.g4  
		Add index related command in this file, such as "create/show/drop oindex". 
•	org/apache/spark/scheduler/DAGScheduler.scala           
		Add the oap cache location to aware task scheduling.
•	org/apache/spark/sql/execution/DataSourceScanExec.scala   
		Add the metrics info to OapMetricsManager and schedule the task to read from the cached hosts.
•	org/apache/spark/sql/execution/datasources/FileFormatWriter.scala
		Return the result of write task to driver.
•	org/apache/spark/sql/execution/datasources/OutputWriter.scala  
		Add new API to support return the result of write task to driver.
•	org/apache/spark/sql/hive/thriftserver/HiveThriftServer2.scala
		Add OapEnv.init() and OapEnv.stop
•	org/apache/spark/sql/hive/thriftserver/SparkSQLCLIDriver.scala
		Add OapEnv.init() and OapEnv.stop in SparkSQLCLIDriver
•	org/apache/spark/status/api/v1/OneApplicationResource.scala    
		Update the metric data to spark web UI.
•	org/apache/spark/SparkEnv.scala
		Add OapRuntime.stop() to stop OapRuntime instance.
•	org/apache/spark/sql/execution/datasources/parquet/VectorizedColumnReader.java
		Change the private access of variable to protected
•	org/apache/spark/sql/execution/datasources/parquet/VectorizedPlainValuesReader.java
		Change the private access of variable to protected
•	org/apache/spark/sql/execution/datasources/parquet/VectorizedRleValuesReader.java
		Change the private access of variable to protected
•	org/apache/spark/sql/execution/vectorized/OnHeapColumnVector.java
		Add the get and set method for the changed protected variable.

```
You need to check if OAP's modified sources codes conflict with your customized Spark. If conflicts exist, you need to merge these codes and rebulid OAP.



## OAP without DCPMM

#### Building
OAP is built using [Apache Maven](http://maven.apache.org/).

```
git clone -b branch-0.6-spark-2.3.x  https://github.com/Intel-bigdata/OAP.git
cd OAP
mvn clean -DskipTests package
```

#### Running Test

To run all the tests, use
```
mvn clean test
```
To run any specific test suite, for example `OapDDLSuite`, use
```
mvn -DwildcardSuites=org.apache.spark.sql.execution.datasources.oap.OapDDLSuite test
```
**NOTE**: Log level of OAP unit tests currently default to ERROR, please override src/test/resources/log4j.properties if needed.


## OAP with DCPMM

#### Prerequisites for building

Please make sure the following Dependencies libraries are installed in the development environment.

```
cmake
libpthread-stubs0-dev
libnuma-dev
memkind 
numactl 
numactl-devel
```

#### Building
OAP is built using [Apache Maven](http://maven.apache.org/).

```
git clone -b branch-0.6-spark-2.3.x  https://github.com/Intel-bigdata/OAP.git
cd OAP
mvn clean -q -Ppersistent-memory -DskipTests package
```

#### Running Test

To run all the tests, use
```
mvn clean -q -Ppersistent-memory test
```
To run any specific test suite, for example `OapDDLSuite`, use
```
mvn -DwildcardSuites=org.apache.spark.sql.execution.datasources.oap.OapDDLSuite test
```
**NOTE**: Log level of OAP unit tests currently default to ERROR, please override src/test/resources/log4j.properties if needed.

#### Revert "hashjoin metrics" for performance

When using Community Spark-2.3.2, [SPARK-21052](https://issues.apache.org/jira/browse/SPARK-21052) causes performance degradation, so we create the [hashjoin-revert.patch](./hashjoin-revert.patch) to revert the "hashjoin metrics" for Spark-2.3.2.

Download src for [Spark-2.3.2](https://archive.apache.org/dist/spark/spark-2.3.2/spark-2.3.2.tgz).

Apply this patch and [rebuild](https://spark.apache.org/docs/latest/building-spark.html) Spark package.
```
git apply hashjoin-revert.patch
```

## Enable Numa binding for DCPMM in Spark

When using DCPMM as a cache medium, if you want to obtain higher performance gains, you can use our [Numa](https://www.kernel.org/doc/html/v4.18/vm/numa.html) Binding patch: [Spark.2.3.2.numa.patch](./Spark.2.3.2.numa.patch)

Download src for [Spark-2.3.2](https://archive.apache.org/dist/spark/spark-2.3.2/spark-2.3.2.tgz).

Apply this patch and [rebuild](https://spark.apache.org/docs/latest/building-spark.html) Spark package.
```
git apply  Spark.2.3.2.numa.patch
```

When deploying OAP to Spark, please add a configuration item to enable Numa binding.

$SPARK_HOME/conf/spark-defaults.conf

```
spark.yarn.numa.enabled true 
```
Note: If you are using a customized Spark, there may be conflicts in applying the patch, you may need to manually resolve the conflicts.

#### Use pre-built patched Spark packages 

If you think it is cumbersome to apply patches, you can use our pre-built [spark-2.3.2-bin-hadoop2.7-patched.tgz](spark-2.3.2-bin-hadoop2.7-patched.tgz) to deploy directly.


