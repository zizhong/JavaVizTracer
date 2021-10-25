# JavaVizTracer

Inspired by [viztracer](https://github.com/gaogaotiantian/viztracer), JavaVizTracer is to trace and profile Java applications.

JavaVizTracer implements Java instrumentation to log trace information, which can be viewed by [vizviewer](https://github.com/gaogaotiantian/viztracer#basic-usage).

### Usage
#### Profile the whole main function
1. Add -javaagent and needed options to your java application command line.
```shell
java -javaagent:<tracer.jar>  \
-Dorg.gz.viztracer.enableOnStart=true \
-Dorg.gz.viztracer.allowClassList="org.gz.examples"  \
org.gz.examples.AsyncExamples 
```
2. vizviwer output.json
TODO add png

#### Profile a request
1. Add -javaagent and needed options to your java application command line.
2.
```shell
curl localhost:11051/trace?cmd=enable
curl localhost:$app_port/req
curl localhost:11051/trace?cmd=disable
```
3. vizviwer output.json

### Disclaimer
This project is in alpha stage. Please report bugs to TODO. Your contribution is greatly welcomed!