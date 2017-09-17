
This projects is intended to be used together with:
* https://hub.docker.com/r/dataminelab/dev-spark

Following examples are based on the _LearningSpark_ Project:
https://github.com/spirom/LearningSpark

The key changes:
* Can be used together with the customised Docker image for docker-spark: https://github.com/dataminelab/docker-spark
* Added cluster deployment using `docker-compose`
* Added TDD example with `FunSpec`
* Monitoring and UI notes
* Added aws cli examples for `EMR` deployment

# Docker

To run locally with the Docker

See instructions here on how to run local docker, both docker single and cluster:
https://github.com/dataminelab/docker-spark

```
# to run a specific example (from within the docker)
cd /var/examples
# this might take few minutes on the first run
sbt package
# submit a chosen class
$SPARK_HOME/bin/spark-submit --class Ex1_SimpleRDD ./target/scala-2.11/sparkexamples_2.11-1.0.jar
```

To run the example without spurious Spark logs:
```
spark-submit \
--conf "spark.driver.extraJavaOptions=-Dlog4j.configuration=file:///var/examples/src/main/resources/log4j.properties" \
--class Ex2_Computations ./target/scala-2.11/sparkexamples_2.11-1.0.jar
```

To see historical logs start the history server:
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

Options:
```
--deploy-mode cluster # Whether to launch the driver program locally ("client") or
                              on one of the worker machines inside the cluster ("cluster")
                              (Default: client)
--supervise # If given, restarts the driver on failure.
            # Watch out on too many restarts!
```

## EMR

To submit example from within the Spark master on EMR (first ssh to the master node):
```
# First run one of the standard examples just to confirm things work fine
spark-submit --driver-memory 512m --executor-memory 512m --class org.apache.spark.examples.SparkPi /usr/lib/spark/examples/jars/spark-examples.jar 10
```

To compile all examples and run them directly on the EMR cluster:
```
# ssh to the EMR master
# Download and compile source code
sudo yum install -y git
git clone https://github.com/dataminelab/LearningSpark
cd LearningSpark
# install and compile sbt
curl -s "https://get.sdkman.io" | bash
source "/home/hadoop/.sdkman/bin/sdkman-init.sh"
sdk install sbt
# This could take few minutes
sbt package
```

Alternative solutions:
```
# Upload the package to S3 using aws-cli
# Start from congiguring aws key/secret key
aws configure
# Upload package to S3 (create S3 bucket and replace mybucket with your name)
aws s3 cp /var/examples/target/scala-2.11/sparkexamples_2.11-1.0.jar s3://mybucket/
# Deploy to YARN using S3 (replace j-xxxxx with your cluster id)
aws emr add-steps --cluster-id j-xxxxx --steps Type=spark,Name=SparkWordCountApp,Args=[--deploy-mode,cluster,--master,yarn,--conf,spark.yarn.submit.waitAppCompletion=false,--num-executors,5,--executor-cores,5,--executor-memory,20g,s3://codelocation/wordcount.py,s3://inputbucket/input.txt,s3://outputbucket/],ActionOnFailure=CONTINUE
```
See for more details:
* You might as well deploy it manually: http://docs.aws.amazon.com/emr/latest/ReleaseGuide/emr-launch-custom-jar-cli.html
* Pass your custom file: s3://mybucket/sparkexamples_2.11-1.0.jar
* Use following options: --class Ex1_SimpleRDD

Do the same with the aws cli:
```
# TODO - add cluster step
```

* http://docs.aws.amazon.com/emr/latest/ReleaseGuide/emr-spark-submit-step.html
* https://aws.amazon.com/cli/

## EMR logs

* Install Chrome
* Install FoxyProxy Basic: https://chrome.google.com/webstore/search/foxy%20proxy
* Navigate to: chrome://extensions/ and click on FoxyProxy Options, choose Import/Export
* Follow instructions from http://docs.aws.amazon.com/emr/latest/ManagementGuide/emr-connect-master-node-proxy.html

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
| [dataset](src/main/scala/dataset) | A range of Dataset examples (queryable collection that is statically typed) -- see the local README.md in that directory for details. |
| [dataframe](src/main/scala/dataframe) | A range of DataFrame examples (queryable collection that is dynamically -- and weakly -- typed)-- see the local README.md in that directory for details. |
| [sql](src/main/scala/sql) | A range of SQL examples -- see the local README.md in that directory for details.  |
| [streaming](src/main/scala/streaming) | Streaming examples -- see the local README.md in that directory for details.  |
| [streaming/structured](src/main/scala/streaming/structured) | Structured streaming examples (Spark 2.0) -- see the local README.md in that directory for details.  |

