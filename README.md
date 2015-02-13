spring-boot-cf-uni-java-broker
=============================

Spring Boot Cloud Foundry service broker built for easy extension through a factory pattern. In the spirit of Cloud Foundry to support .Net natively in the the very near future, this broker support SQL Server out of the box... others come later. 
To extend, build new classes implementing ServiceInstanceService, ServiceInstanceBindingService, CatalogService, and DashboardService. Wire up the factories. Implementation and concrete backends of the services are up to you. The SQLServerRepository class is a good example of what to do.

...*and*... yes, **there is a dashboard.**

# Setup
## Maven
This project is mavenized and targeted towards STS. However, since the JDBC drivers for this project are not stored in a public repo, I've decided to create a local repo and reference the same in the pom. As such, once the project is cloned, run the following commands in the root of the project directory.
#### Oracle:
```bat
mvn install:install-file -DlocalRepositoryPath=repo -DcreateChecksum=true -Dpackaging=jar -Dfile=lib/ojdbc7.jar -DgroupId=ojdbc7 -DartifactId=ojdbc7 -Dversion=7
```
#### SQL Server:
```bat
mvn install:install-file -DlocalRepositoryPath=repo -DcreateChecksum=true -Dpackaging=jar -Dfile=lib/sqljdbc41.jar -DgroupId=sqljdbc41 -DartifactId=sqljdbc41 -Dversion=4.1
```
Once you setup your local repo, do a clean build **WITHOUT** running tests. Command below:
```bat
mvn clean package -DskipTests=true
```
## Database
Since only Microsoft SQL Server has been implemented, the scope of this is only towards the same. As such, you'll need access to a SQL Server instance and need to create a required database, tables, and user. A script has been included to do this and is located at [scripts/SQLServer/CreateAdminDatabase.sql](https://github.com/csvoboda-pivotal/spring-boot-cf-uni-java-broker/blob/master/Scripts/SQLServer/CreateAdminDatabase.sql).

## Eclipse
Import the maven project into your workspace as you normally would. Once imported, in order to run the tests, you will need to set service specific environment variables in the run/debug settings **for each test launch configuration**. Your choice in the granualarity of these tests, but I suggest doing it at the class level. Set variables by modifying the launch files directly at: <workspace>/.metadata/.plugins/.org.eclipse.debug.core/.launches/<runasfilename> or create/duplicate run and debug configurations for the tests.

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
# Deploy
An example yaml file is included. Adjust the values for your environment. I hope you'll appreciate the flexibility and simplicity granted by using environment variables to configure for concrete services from environment to environment and type to type.
