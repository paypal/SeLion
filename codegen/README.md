SeLion Code Generator CLI
=======
Used to generate `.java` "page object" classes from PageYAML.

_Example Usage_
```shell
$ java -jar target/SeLion-Code-Generator-CLI-{version}-jar-with-dependencies.jar -h
Usage: <main class> [options]
  Options:
    -androidCustomElements
       Comma separated list of "android" custom elements to be included during
       code generation. For example: bar.foo.A,bar.foo.B
       Default: []
    -baseFolder
       Represents the base folder used for reading page asset files such as
       PageYAML.
       Default: GUIData
    -basePackage
       Represents the base package used for generated java classes.
       Default: com.paypal.selion.testcomponents
    -excludeDomains
       Comma separated list of "domains" to exclude during code generation. For
       example: foo,bar,baz
       Default: []
    -generatedSourcesDir
       The generated source directory to output to.
       Default: target/generated-test-sources
    -htmlCustomElements
       Comma separated list of "html" custom elements to be included during code
       generation. For example: bar.foo.A,bar.foo.B
       Default: []
    -iosCustomElements
       Comma separated list of "ios" custom elements to be included during code
       generation. For example: bar.foo.A,bar.foo.B
       Default: []
    -mobileCustomElements
       Comma separated list of "mobile" custom elements to be included during
       code generation. For example: bar.foo.A,bar.foo.B
       Default: []
    -resourcesDir
       The resources directory which contains the -baseFolder.
       Default: src/test/resources
    -sourceDir
       The source directory for classes which might depend on generated code
       (E.g. {PageObject}Ext classes)
       Default: src/test/java
    -workingDir
       The working directory.
       Default: ./

```
