spring-boot-cf-uni-java-broker
=============================

Spring Boot Cloud Foundry service broker built for easy extension through a factory pattern. Supports SQL Server OOTB... others come later. To extend, build new classes implementing ServiceInstanceService, ServiceInstanceBindingService, CatalogService, and DashboardService. Wire up the factories. Implementation and concrete backends of the services are up to you. The SQLServerRepository class is a good example of what to do.

...*and*... yes **There is a dashboard to report back the credentials.**

# Setup
This project is mavenized and targeted towards STS. However, since the JDBC drivers for this project are not stored in a public repo, I've decided to create a local repo and reference the same in the pom. As such, once the project is cloned, run the following commands in the root of the project directory.
## Oracle:
```bat
mvn install:install-file -DlocalRepositoryPath=repo -DcreateChecksum=true -Dpackaging=jar -Dfile=lib/ojdbc7.jar -DgroupId=ojdbc7 -DartifactId=ojdbc7 -Dversion=7
```
## SQL Server:
```bat
mvn install:install-file -DlocalRepositoryPath=repo -DcreateChecksum=true -Dpackaging=jar -Dfile=lib/sqljdbc41.jar -DgroupId=sqljdbc41 -DartifactId=sqljdbc41 -Dversion=4.1
```
Once you setup your local repo, do a clean build **WITHOUT** running test. Command below:
```bat
mvn clean package -DskipTests=true
```

Environment variables must be set to run JUnit tests. Set variables by modifying the launch files directly at: <workspace>/.metadata/.plugins/.org.eclipse.debug.core/.launches/<runasfilename> or create/duplicate run and debug configurations for the tests.

```html
Example of these variables, for SQL Server, are below.
<mapAttribute key="org.eclipse.debug.core.environmentVariables">
<mapEntry key="CF_SB_SERVICE_HOST" value="localhost"/>
<mapEntry key="CF_SB_SERVICE_PASSWORD" value="Password1234"/>
<mapEntry key="CF_SB_SERVICE_PORT" value="1433"/>
<mapEntry key="CF_SB_SERVICE_TYPE" value="SQLSERVER"/>
<mapEntry key="CF_SB_SERVICE_USERNAME" value="cf_admin_sa"/>
<mapEntry key="CF_SB_SQL_SERVER_ADMIN_DATABASE_NAME" value="cf_admin"/>
</mapAttribute>
```

The SQL Server or Oracle libs will need to be added to a local maven repo to buid correctly. The pom is updated accordingly, but the following commands should be run the project root.

Oracle:
```bat
mvn install:install-file -DlocalRepositoryPath=repo -DcreateChecksum=true -Dpackaging=jar -Dfile=lib/ojdbc7.jar -DgroupId=ojdbc7 -DartifactId=ojdbc7 -Dversion=7
```

SQL Server:
```bat
mvn install:install-file -DlocalRepositoryPath=repo -DcreateChecksum=true -Dpackaging=jar -Dfile=lib/sqljdbc41.jar -DgroupId=sqljdbc41 -DartifactId=sqljdbc41 -Dversion=4.1
```

To build the project without running tests, execute the following:
```bat
mvn clean package -DskipTests=true
```
