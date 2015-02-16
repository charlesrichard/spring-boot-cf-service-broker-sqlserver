spring-boot-cf-service-broker-sqlserver
=============================

Spring Boot Cloud Foundry service broker exposing Microsoft SQL Server. This project extends [spring-boot-cf-service-broker](https://github.com/cloudfoundry-community/spring-boot-cf-service-broker). Please read the read.me on this project for general question on security and deployment

...*and*... yes, **there is a dashboard.**

# Setup
## Gradle
Build automation is provided by Gradle. The catch is that SQL Server configuration is set through environment variables. You may edit build.gradle to add the vars or do a build **WITHOUT** running tests. Command below:
```bat
mvn clean package -DskipTests=true
```
## Database
Since only Microsoft SQL Server has been implemented, the scope of this is only towards the same. As such, you'll need access to a SQL Server instance and need to create a required database, tables, and user. If you don't have an on prem SQL Server laying around, AWS is a great place to play around. A script has been included to do this and is located at [scripts/SQLServer/CreateAdminDatabase.sql](https://github.com/csvoboda-pivotal/spring-boot-cf-uni-java-broker/blob/master/Scripts/SQLServer/CreateAdminDatabase.sql).

## Eclipse
Import the maven project into your workspace as you normally would. Once imported, in order to run the tests, you will need to set service specific environment variables in the run/debug settings **for each test launch configuration**. Your choice in the granualarity of these tests, but I suggest doing it at the class level. Set variables by modifying the launch files directly at: <workspace>/.metadata/.plugins/.org.eclipse.debug.core/.launches/<runasfilename> or create/duplicate run and debug configurations for the tests.

```html
Example of these variables, for SQL Server, are below.
<mapAttribute key="org.eclipse.debug.core.environmentVariables">
<mapEntry key="CF_SB_SERVICE_HOST" value="localhost"/>
<mapEntry key="CF_SB_SERVICE_PASSWORD" value="Password1234"/>
<mapEntry key="CF_SB_SERVICE_PORT" value="1433"/>
<mapEntry key="CF_SB_SERVICE_USERNAME" value="cf_admin_sa"/>
<mapEntry key="CF_SB_SQL_SERVER_ADMIN_DATABASE_NAME" value="cf_admin"/>
</mapAttribute>
```
# Deploy
An example yaml file is included. Adjust the values for your environment. I hope you'll appreciate the flexibility, simplicity, and security granted by using environment variables to configure for concrete services from environment to environment and type to type.
