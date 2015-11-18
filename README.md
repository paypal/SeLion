[![Build Status](https://travis-ci.org/paypal/SeLion.svg?branch=develop)](https://travis-ci.org/paypal/SeLion)

[![Gitter](https://badges.gitter.im/Join Chat.svg)](https://gitter.im/paypal/SeLion?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

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

1. Complete and return either the [Personal](https://docs.google.com/forms/d/1t_Rqly6-qVP536O8ESltIRCHM0qBsmahWUdJda0oo3U/viewform) or [Corporate CLA](https://docs.google.com/forms/d/1qngimdtbwKcxMIJAxeV0DEqVYPCGHcYegFX0PVgMD78/viewform).
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
- The [ios-driver](https://ios-driver.github.io/ios-driver/) project.
- The [Selendroid](http://selendroid.io/) project.
- [Appium](http://appium.io/)

Current version
---------------
1.0.0

Development version
-------------------
1.1.0-SNAPSHOT

License
-----------
Code - [Apache Software License v2.0](http://www.apache.org/licenses/LICENSE-2.0)

Documentation and Site - [Creative Commons Attribution 4.0 International License](http://creativecommons.org/licenses/by/4.0/deed.en_US)
