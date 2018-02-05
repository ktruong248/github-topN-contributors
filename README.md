Problem
===================

Develop REST API that allows a user to interact with the “GitHub” (https://github.com/github) organization.
The REST API should be able to provide Top-N contributors for the organization.

Developer Guide
---------------

Service is built on the [Play](https://www.playframework.com/) web framework and uses [Guice](https://github.com/google/guice) for dependency injection.

* [Play Java documentation](https://www.playframework.com/documentation/2.5.x/JavaHome) and [javadocs](https://www.playframework.com/documentation/2.5.x/api/java/index.html)
* [Guice user guide](https://github.com/google/guice/wiki/Motivation)

It uses [JUnit](http://junit.org/junit4/), [AssertJ](http://joel-costigliola.github.io/assertj/index.html), and [Mockito](http://site.mockito.org/) for unit testing.

* [AssertJ quick start](http://joel-costigliola.github.io/assertj/assertj-core-quick-start.html)
* [Mockito documentation](http://static.javadoc.io/org.mockito/mockito-core/2.2.21/org/mockito/Mockito.html)

###Prerequisites

* JDK 8

###IDE Configuration

IntelliJ IDEA users should import `ideaCodeStyle.xml` as the project code style scheme via `Preferences > Editor > Code Style > Java > Manage...`

###Run Test
Command line: `$ ./gradlew clean test`

###Run Service

Command line: `$ ./gradlew run` or `$ java -jar build/libs/github-stats-0.1.0-SNAPSHOT.jar`

Main class to run in IDE: `io.wiser.Service`

###Restful call
Command line: `$ curl http://localhost:9000/service/github/top_contributors`

The result is the array of contributor stats object sorted by commit totals in descending order

### Approach

* first fetch repositories by organization i.e "https://api.github.com/orgs/%1s/repos" see `CachedGithubRepos`
* for each repository in $repositories fetch https://api.github.com/repos/%1s/%2s/stats/contributors see `CachedGithubContributorStats.java`


In order to tackle rate limit, I implemented a simple cached solution. This can be improve further by doing async jobs 
that fetching data and store locally in a database so that each time server come up, it doesn't have to fetch from github.