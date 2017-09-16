
# These examples are based on the _LearningSpark_ Project
https://github.com/spirom/LearningSpark

The main changes:
* Can be used together with the customised Docker image for docker-spark: https://github.com/dataminelab/docker-spark
* Added examples for cluster deployment using `docker-compose`
* Added TDD example with `FunSpec`
* Monitoring notes
* Added examples for `EMR` deployment

# Docker

To run locally with the Docker

See instructions here on how to run local docker image and connect to it with `bash`:
https://github.com/dataminelab/docker-spark

```
# to run a specific example from within the docker
cd /var/examples
# this might take few minutes
sbt package
# submit a chosen class
$SPARK_HOME/bin/spark-submit --class Ex1_SimpleRDD ./target/scala-2.11/sparkexamples_2.11-1.0.jar
```

To run the example without Spark logs:
```
spark-submit \
--conf "spark.driver.extraJavaOptions=-Dlog4j.configuration=file:///var/examples/src/main/resources/log4j.properties" \
--class Ex2_Computations ./target/scala-2.11/sparkexamples_2.11-1.0.jar
```

To see historical logs start history server:
```
mkdir /tmp/spark-events
$SPARK_HOME/sbin/start-history-server.sh
# see: localhost:18080
```

To add logs to the history server:
```
spark-submit \
--conf "spark.driver.extraJavaOptions=-Dlog4j.configuration=file:///var/examples/src/main/resources/log4j.properties" \
--conf spark.eventLog.enabled=true --conf spark.eventLog.dir=file:///tmp/spark-events/ \
--class Ex1_SimpleRDD ./target/scala-2.11/sparkexamples_2.11-1.0.jar
```

## Local cluster

To submit a job to a local cluster. Run `docker-compose up -d`, bash to the master server and:

```
spark-submit \
--master spark://master:7077 \
--deploy-mode cluster \
--conf "spark.driver.extraJavaOptions=-Dlog4j.configuration=file:///var/examples/src/main/resources/log4j.properties" \
--conf spark.eventLog.enabled=true --conf spark.eventLog.dir=file:///tmp/spark-events/ \
--class Ex1_SimpleRDD ./target/scala-2.11/sparkexamples_2.11-1.0.jar
```

Note:
* The config options can be moved to conf/master and conf/worker 

Good options to provide:
```
--deploy-mode cluster # Whether to launch the driver program locally ("client") or
                              on one of the worker machines inside the cluster ("cluster")
                              (Default: client)
--supervise # If given, restarts the driver on failure.
            # Watch out on too many restarts!
```

For a reference:
${SPARK_CMD} --master ${SPARK_MASTER} --deploy-mode cluster --supervise --class ${MAIN_CLASS} ${APP_FILE} ${SPARK_WORKERS} ${KAFKA_TOPIC} 2>&1 | tee submitter.log


## Dependencies

The project was created with IntelliJ Idea 14 Community Edition,
currently using JDK 1.8, Scala 2.11.8 and Spark 2.2.0 on Ubuntu Linux.


## Scala Examples

The examples can be found under src/main/scala. The best way to use them is to start by reading the code and its comments. Then, since each file contains an object definition with a main method, run it and consider the output. Relevant blog posts and StackOverflow answers are listed in the various package README.md files.

| Package or File                  | What's Illustrated    |
|---------------------------------|-----------------------|
|          rdd/Ex1_SimpleRDD         | How to execute your first, very simple, Spark Job. See also [An easy way to start learning Spark](http://www.river-of-bytes.com/2014/11/an-easy-way-to-start-learning-spark.html).
|          rdd/Ex2_Computations      | How RDDs work in more complex computations. See also [Spark computations](http://www.river-of-bytes.com/2014/11/spark-computations.html). |
|          rdd/Ex3_CombiningRDDs     | Operations on multiple RDDs |
|          rdd/Ex4_MoreOperationsOnRDDs | More complex operations on individual RDDs |
|          rdd/Ex5_Partitions        | Explicit control of partitioning for performance and scalability. |
|          rdd/Ex6_Accumulators | How to use Spark accumulators to efficiently gather the results of distributed computations. |
| [hiveql](src/main/scala/hiveql)  | Using HiveQL features in a HiveContext. See the local README.md in that directory for details. |
| [special](src/main/scala/special) | Special/adbanced RDD examples -- see the local README.md in that directory for details. |
| [dataset](src/main/scala/dataset) | A range of Dataset examples (queryable collection that is statically typed) -- see the local README.md in that directory for details. |
| [dataframe](src/main/scala/dataframe) | A range of DataFrame examples (queryable collection that is dynamically -- and weakly -- typed)-- see the local README.md in that directory for details. |
| [sql](src/main/scala/sql) | A range of SQL examples -- see the local README.md in that directory for details.  |
| [streaming](src/main/scala/streaming) | Streaming examples -- see the local README.md in that directory for details.  |
| [streaming/structured](src/main/scala/streaming/structured) | Structured streaming examples (Spark 2.0) -- see the local README.md in that directory for details.  |
| [graphx](src/main/scala/graphx) | A range of GraphX examples -- see the local README.md in that directory for details. |

