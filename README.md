[![Build Status](https://travis-ci.org/djb61/parallel-cucumber-jvm.svg?branch=master)](https://travis-ci.org/djb61/parallel-cucumber-jvm)
# Parallel Cucumber JVM
This is a wrapper around [cucumber-jvm](https://github.com/cucumber/cucumber-jvm) to allow parallel execution of tests at a feature level.

## Maven Artifact
Binaries are available in [jCenter](https://bintray.com/bintray/jcenter?filterByPkgName=parallel-cucumber-jvm)

[ ![Download](https://api.bintray.com/packages/djb61/maven/parallel-cucumber-jvm/images/download.svg) ](https://bintray.com/djb61/maven/parallel-cucumber-jvm/_latestVersion)
```
groupId:    com.bishnet
artifactId: parallel-cucumber-jvm 
```

## How to use it
To start a CLI test run use the main method in this class
``` java
com.bishnet.cucumber.parallel.cli.Main
```
The command line API matches that of cucumber-jvm as described [here](https://raw.githubusercontent.com/cucumber/cucumber-jvm/v1.2.2/core/src/main/resources/cucumber/api/cli/USAGE.txt).
A single additional argument is supported to control the level of parallelism.
```
--num-threads <n> # Defaults to the number of available cores if not specified
```

## Features

##### Thread timeline report

Report for simplify troubleshooting of multithreaded tests

![Report example](http://i.imgur.com/xfJfZAx.png)

Usage

``` java
--plugin thread-report:threadReportDir/
```

##### Failed scenarios report 

Creates single, aggregated from all threads, rerun formatted report file of all failed scenarios. 

Usage
``` java
--plugin rerun:rerunReportFileName
```
##### Rerun failed scenarios

Reruns all failed scenarios in a separate thread and creates "flaky" tests reports in Cucumber json format. Params: 
- rerun-count - number of attempts to rerun failed scenarios (default - zero), 
- flaky-report - path where flaky_*.json reports will be created (default - root report folder). 

Usage
``` java
--plugin rerun-count:3
--plugin flaky-report:report/
```

## Limitations
1. CLI is the only supported execution mechanism.
2. Only the cucumber-java backend has been tested. Other backends may or may not work correctly.

## How it works
1. The list of features to be executed is gathered based on the command line constraints provided.
2. The list of features is split into a number of chunks equal to the number of threads required.
3. Rerun formatted files are written to the JVM temp directory, one for each thread containing all the scenarios the thread should run.
4. A cucumber-jvm runtime is started in each thread taking the temporary rerun file as input. The output is written to an HTML and/or JSON report for the thread in the JVM temp directory dependent on the output formats requested on the command line.
5. The temporary HTML and/or JSON reports generated by the individual threads are merged into a single report and placed in the location specified on the command line.
6. Failed upon initial run scenarios are taken for rerun for a given attempts count: 
- If all flaky scenarios pass upon rerun, then test run build passes (becomes green);
- if at least one failing scenario remains after all attempts spent, then test run build fails (remains red);
- Scenario initially failed, but passed upon rerun regarded as "flaky"; 
- Standard Cucumber json reports of failed tests (with stacktrace) are created with names: "flaky_n.json", where n -
 is rerun attempt number;
