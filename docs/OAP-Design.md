# OAP Design


* [Introduction](#introduction)
* [OAP Architecture](#oap-architecture)
* [OAP Components](#oap-components)


## Introduction

Apache Spark is a fast and general-purpose cluster computing system. It provides high-level APIs in Java, Scala, Python and R, and an optimized engine that supports general execution graphs. It also supports a rich set of higher-level tools including Spark SQL for SQL and structured data processing, MLlib for machine learning, GraphX for graph processing, and Spark Streaming.

OAP is designed to leverage the user defined indices and smart fine-grained in-memory data caching strategy for boosting Spark SQL performance on ad-hoc queries.


![OAP-INTRODUCTION](./image/OAP-Introduction.PNG)

## OAP Architecture

OAP is designed to leverage the user defined indices and smart fine-grained in-memory data caching strategy for boosting Spark SQL performance on ad-hoc queries.

![OAP-ARCHITECTURE](./image/OAP-Architecture.PNG)

By using DCPMM (AEP) as index and data cache, we can provide a more cost effective solutions for high performance environment requirement.


## OAP Components
### Index 

![OAP-INDEX](./image/OAP-Index.PNG)

### Cache
Cache Aware
![CACHE-AWARE](./image/Cache-Aware.PNG)

### OAPFileFormat

![OAPFILEFORMAT](./image/OAPFileFormat.PNG)
