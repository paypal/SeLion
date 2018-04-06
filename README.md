[![Build Status](https://travis-ci.org/paypal/SeLion.svg?branch=develop)](https://travis-ci.org/paypal/SeLion)

[![Codacy Badge](https://api.codacy.com/project/badge/grade/a885c7bf48794b27aa72dfe8e85ee22b)](https://www.codacy.com/app/SeLion/paypal-SeLion)

[![Gitter](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/paypal/SeLion?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)



SeLion
=======
Enabling Test Automation in Java

SeLion builds on top of TestNG and Selenium to provide a set of capabilities that get you up and running with WebDriver in a short time. It can be used for testing web and mobile applications.

- A client module which you can add as a Maven dependency.
  - Annotation based WebDriver session management.
  - Runtime Reporter for real-time test execution status.
  - Swap out browser targets at run-time.
  - Automate native, hybrid, and web applications on iOS and Android devices.
  - and more
- A set of TestNG compatible data providers which allow you to access test data from
  Excel, YAML, JSON, and XML.
- A maven archetype for creating new SeLion based projects.
- A customized Selenium Grid2 component.
- A Code generator Maven plugin for generating Java "page objects" out of YAML.

Documentation
-------------
Project documentation including prerequisites, compilation, usage, and more is at http://paypal.github.io/SeLion/html/documentation.html

Create a new project using the SeLion maven archetype
```
mvn archetype:generate -B -DartifactId=Sample -Dversion=1.0.0 -DgroupId=com.mycompany.myproject \
 -DarchetypeGroupId=com.paypal.selion -DarchetypeArtifactId=SeLion-Archetype -DarchetypeVersion=1.2.0
```

Client module (includes SeLion DataProviders)
```xml
<dependency>
    <groupId>com.paypal.selion</groupId>
    <artifactId>SeLion</artifactId>
    <version>1.2.0</version>
</dependency>
```

SeLion-DataProviders only
```xml
<dependency>
  <groupId>com.paypal.selion</groupId>
  <artifactId>SeLion-DataProviders</artifactId>
  <version>1.2.0</version>
</dependency
```

SeLion Grid enhancements -- Required for local run support with the Client module
```xml
<dependency>
  <groupId>com.paypal.selion</groupId>
  <artifactId>SeLion-Grid</artifactId>
  <version>1.2.0</version>
</dependency
```

SeLion Code Generator Maven plugin -- Adds "page object" code generation
```xml
<plugin>
    <groupId>com.paypal.selion</groupId>
    <artifactId>SeLion-Code-Generator</artifactId>
    <version>1.2.0</version>
    <executions>
        <execution>
            <phase>generate-sources</phase>
            <goals>
                <goal>generate</goal>
            </goals>
        </execution>
    </executions>
    <configuration>
        <basePackage>coo.foo.bar</basePackage>
    </configuration>
</plugin>
```

Run the SeLion Grid as a standalone selenium server
```
java -jar SeLion-Grid-1.2.0-jar-with-dependencies.jar
```
Run the SeLion Grid as a selenium hub
```
java -jar SeLion-Grid-1.2.0-jar-with-dependencies.jar -role hub
```
Run the SeLion Grid as a selenium web node
```
java -jar SeLion-Grid-1.2.0-jar-with-dependencies.jar -role node
```
Run the SeLion Grid as a Selendroid node (beta feature)
```
java -cp SeLion-Grid-1.2.0-jar-with-dependencies.jar com.paypal.selion.grid.SelendroidJarSpawner
```
Run the SeLion Grid as an ios-driver node (beta feature)
```
java -cp SeLion-Grid-1.2.0-jar-with-dependencies.jar com.paypal.selion.grid.IOSDriverJarSpawner
```
Run the SeLion Grid as an Appium node (beta feature)
```
java -cp SeLion-Grid-1.2.0-jar-with-dependencies.jar com.paypal.selion.grid.AppiumSpawner
```
Run the SeLion Grid as a Sauce labs proxy
```
java -cp SeLion-Grid-1.2.0-jar-with-dependencies.jar -role hub -type sauce
```

Contact
-------
Feel free to ask questions and/or share ideas.

- [Raise a GitHub Issue](https://github.com/paypal/SeLion/issues).
- [Join our Google Group](https://groups.google.com/forum/#!forum/selion).
- Find us on [Gitter](https://gitter.im/paypal/SeLion)
- Initiate a pull request, if you have a contribution.

Submitting bugs and feature requests
------------------------------------
We use GitHub for tracking issues. Please scan the list of [GitHub Issues](https://github.com/paypal/SeLion/issues) before filing a new one.

Contributing
-------------
Your contribution is welcome and appreciated!

- Code Development is done on the <code>develop</code> branch.
- Documentation is done on the <code>gh-pages</code> branch.

1. Complete and return either the [Personal](https://docs.google.com/forms/d/1wKMEHtrkxj0BU0W_3m0pEPVsoVJxj2yygC91YoIuX3I/viewform) or [Corporate CLA](https://docs.google.com/forms/d/1nw0gefZNgBHaMfC-YaDKnHUPnI7vqcUMsrQgjjt8Q8A/viewform).
2. Make sure your <code>~/.gitconfig</code> file has your full name with proper use of case as <code>user.name</code> and your real email address as <code>user.email</code>. GitHub has [a nice write-up on this](https://help.github.com/articles/setting-your-username-in-git).
3. Make sure you rebase off of the latest upstream version before submitting your pull request.

Areas that need contribution
-----------------------------
1. Project Documentation and/or Project Website.
2. Project Tests and/or Testing.
3. Any open item from [GitHub Issues](https://github.com/paypal/SeLion/issues).
4. Support for additional data formats such as CSV for data driven testing.

Projects we depend on that need contributors
---------------------------------------------
- [Appium](http://appium.io/)
- The [Selendroid](http://selendroid.io/) project.
- The [ios-driver](https://ios-driver.github.io/ios-driver/) project.

Current version
---------------
[1.2.0](http://search.maven.org/#search%7Cga%7C1%7Cselion) (Selenium 2.53.x based)

Development version
-------------------
2.0.0-SNAPSHOT (Selenium 3.x based)

License
-----------
Code - [Apache Software License v2.0](http://www.apache.org/licenses/LICENSE-2.0)

Documentation and Site - [Creative Commons Attribution 4.0 International License](http://creativecommons.org/licenses/by/4.0/deed.en_US)
