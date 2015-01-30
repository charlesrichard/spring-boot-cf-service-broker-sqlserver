spring-boot-cf-uni-java-broker
=============================

Spring Boot project implementing SQL Server and Oracle databases.

Environment variables must be set. Fastest way to set variables is to modify the launch files directly at: <workspace>/.metadata/.plugins/.org.eclipse.debug.core/.launches/<runasfilename>.

Example of these variables, for SQL Server, are below.
<mapAttribute key="org.eclipse.debug.core.environmentVariables">
<mapEntry key="CF_SB_SERVICE_HOST" value="localhost"/>
<mapEntry key="CF_SB_SERVICE_PASSWORD" value="Password1234"/>
<mapEntry key="CF_SB_SERVICE_PORT" value="1433"/>
<mapEntry key="CF_SB_SERVICE_TYPE" value="SQLSERVER"/>
<mapEntry key="CF_SB_SERVICE_USERNAME" value="cf_admin_sa"/>
<mapEntry key="CF_SB_SQL_SERVER_ADMIN_DATABASE_NAME" value="cf_admin"/>
</mapAttribute>
