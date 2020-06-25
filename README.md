# TIBCO EMS metrics
This project was created in order to allow logging of TIBCO EMS metrics into the Graphite database using TCP plain text connection.
![Grafana Dashboard](pics/grafana_dashboard.jpg?raw=true)

## Preparation
#### TIBCO JMS client libraries
Copy JMS client libraries (version 8.5.1) into the *lib* directory:
```
jms-2.0.jar
tibjms.jar
tibjmsadmin.jar
```
and execute below command:
```bash 
./import_tibco_dependencies.sh 
```
You can get them from your local TIBCO installation, or download with TIBCO EMS Community Edition.
In the latest release *8.5.1* it is a *TIB_ems-ce_8.5.1_linux_x86_64.zip* file.
Inside the archive there is a *tar* folder with *TIB_ems-ce_8.5.1_linux_x86_64-java_client.tar.gz*.
Inside that folder you should find all the mentioned files.

#### Configuration
In order to start the application, [metrics.properties](metrics.properties) file should be present in the same directory as the application.
Example properties file:
```properties
ems.connection.admin.url = tcp://localhost:7222
ems.connection.admin.user = admin
ems.connection.admin.password =
ems.connection.retry.count = 100
ems.conenction.retry.interval_seconds = 10
  
graphite.connection.host = localhost
graphite.connection.port = 2003
graphite.connection.retry.count = 100
graphite.connection.retry.interval_seconds = 10

metrics.prefix = TIBCO\.DEV\.EMS\.localhost
metrics.interval_millis = 5000
metrics.enabled.destinations = false
```

## Building
#### Classic Java application
Execution of the command below, will create JAR (including all the dependencies) in the target directory.
```bash
mvn package
```

Start it by executing:
```bash
java -jar ems-metrics-1.0.0-SNAPSHOT-jar-with-dependencies.jar
```

#### Native executable
In order to use native image compilation, you need to have a GraalVM installed.
In order to verify it is installed:
```bash
mvn --version
Apache Maven 3.6.1
Maven home: /usr/share/maven
Java version: 11.0.6, vendor: Oracle Corporation, runtime: /usr/lib/jvm/graalvm-ce-java11-20.0.0
Default locale: en_US, platform encoding: UTF-8
OS name: "linux", version: "5.3.0-51-generic", arch: "amd64", family: "unix"
```
Below command execution will build native-image executeble in the *target* directory.
```bash
mvn package -Pnative
```
This application uses *--no-fallback* switch in order to build image independent from JVM.
It results in a slightly bigger image size, but the final RAM Memory usage is a lot smaller.

Application can be started using:
```bash 
./ems-metrics-1.0.0-SNAPSHOT
```

## Run it with EMS, Graphite and Grafana
Application comes with [docker-compose.yml](docker/docker-compose.yml) containing basic test-bench configuration:
* EMS (you should build you own image)
* Graphite
* Grafana

## TODO
- [ ] Implementation of Pickle Protocol for Graphite: https://graphite.readthedocs.io/en/latest/feeding-carbon.html
- [ ] Properties refactoring
- [ ] Implementation of Influx DB adapter
- [ ] Dashboard for destinations
- [ ] Moving hardcoded strings into properties