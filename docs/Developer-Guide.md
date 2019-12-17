# OAP Developer Guide

* [OAP Building without DCPMM](#OAP-Building-without-dcpmm)
* [OAP Building with DCPMM](#OAP-Building-with-dcpmm)
* [Integration with Spark](#integration-with-spark)
* [Enable Numa binding for DCPMM in Spark](#enable-numa-binding-for-dcpmm-in-spark)



## OAP Building

#### Building
OAP is built using [Apache Maven](http://maven.apache.org/).

To build OAP package.

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


#### OAP Building with DCPMM

If you want to use OAP with DCPMM,  you can follow the below build steps 

###### Prerequisites for building

You will need to install required packages on the build system:

- gcc-c++
- [cmake](https://help.directadmin.com/item.php?id=494)
- [Menkind](https://github.com/memkind/memkind)


###### Building package

```
mvn clean -q -Ppersistent-memory -DskipTests package
```


## Integration with Spark

Although OAP acts as a plugin jar to Spark, there are still a few tricks to note when integration with Spark. 
Basically, OAP explored Spark extension & data source API to perform its core functionality. But there are other functionality aspects that cannot achieved by Spark extension and data source API. We made a few improvements or changes to the Spark internals to achieve the functionality. So when integrating OAP on Spark, you need to check whether you are running an unmodified Community Spark or a modified customized Spark.

#### Integrate with Community Spark

If you are running an Community Spark, things will be much simple. Refer to [basic steps](basic steps)

#### Integrate with customized Spark

It will be more complicated to integrate OAP with a customized Spark. Steps needed for this case is to check whether the OAP changes of Spark internals will conflict or override with your private changes. 
- If no conflicts or overrides happens, the steps are the same as the steps of unmodified version of Spark described above. 
- If conflicts or overrides happen, you need to have a merge plan of the source code to make sure the changes you made in a file appears in the corresponding file changed in OAP project. Once merged, please rebuild OAP.

The following files needs to be checked/compared

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


