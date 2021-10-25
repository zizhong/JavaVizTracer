# JavaVizTracer

Inspired by [viztracer](https://github.com/gaogaotiantian/viztracer), JavaVizTracer is to trace and profile Java applications.

JavaVizTracer implements Java instrumentation to log trace information, which can be viewed by [vizviewer](https://github.com/gaogaotiantian/viztracer#basic-usage).

### Usage
#### Profile the whole application
1. Add -javaagent and needed options to your java application command line.
```shell
java -javaagent:<tracer.jar>  \
-Dorg.gz.viztracer.enableOnStart=true \
-Dorg.gz.viztracer.allowClassList="org.gz.examples"  \
org.gz.examples.AsyncExamples 
```
2. vizviwer output.json
![AsyncExample](https://github.com/zizhong/JavaVizTracer/blob/main/Examples/asyncExample.1.png?raw=true)

#### Profile a request for a long live application
1. Add -javaagent and needed options to your java application command line.
2.
```shell
curl localhost:11051/trace?cmd=enable
curl localhost:$app_port/req
curl localhost:11051/trace?cmd=disable
```
3. vizviwer output.json

### Java Command Options

| Name      | Default Value | Note|
| ----------- | ----------- |-----|
| org.gz.viztracer.verbose| false| for debugging |
| org.gz.viztracer.enableOnStart |    false     |     |
| org.gz.viztracer.allowClassList | "org.gz.examples"        |  ',' seperated  |
| org.gz.viztracer.denyClassList |  "com.sun.,sun.,java.,javax.,org.slf4j,org.gz.viztracer"   |  ',' seperated   |
| org.gz.viztracer.outputFile | "~/trace.output.json"    |    |
| org.gz.viztracer.maxTraceEvents |  1000000   |    |
| org.gz.viztracer.minDurationInNano |  0   |    |

### Disclaimer
This project is in alpha stage. Please report bugs [here](https://github.com/zizhong/JavaVizTracer/issues). Your contribution is greatly welcomed!