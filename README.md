spring-boot-cf-uni-java-broker
=============================

Spring Boot project implementing SQL Server and Oracle databases.

Environment variables must be set. Fastest way to set variables is to modify the launch files directly at: <workspace>/.metadata/.plugins/.org.eclipse.debug.core/.launches/<runasfilename>.

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
mvn install:install-file -DlocalRepositoryPath=repo -DcreateChecksum=true -Dpackaging=jar -Dfile=lib/ojdbc7.jar -DgroupId=ojdbc7 -DartifactId=ojdbc7 -Dversion=7

SQL Server:
mvn install:install-file -DlocalRepositoryPath=repo -DcreateChecksum=true -Dpackaging=jar -Dfile=lib/sqljdbc41.jar -DgroupId=sqljdbc41 -DartifactId=sqljdbc41 -Dversion=4.1
