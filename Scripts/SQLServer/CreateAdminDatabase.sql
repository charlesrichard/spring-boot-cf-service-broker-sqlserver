USE [master]
GO

CREATE DATABASE cf_admin;
GO

CREATE LOGIN [cf_admin_sa] WITH PASSWORD=N'Password1234', DEFAULT_DATABASE=[cf_admin], CHECK_EXPIRATION=OFF, CHECK_POLICY=OFF
GO
ALTER SERVER ROLE [dbcreator] ADD MEMBER [cf_admin_sa]
GO
ALTER SERVER ROLE [securityadmin] ADD MEMBER [cf_admin_sa]
GO

USE [cf_admin]
GO

CREATE TABLE [dbo].[instance](
	[instance_id] [varchar](50) NOT NULL,
	[organization_id] [varchar](50) NOT NULL,
	[space_id] [varchar](50) NOT NULL,
	[service_definition_id] [varchar](50) NOT NULL,
	[plan_id] [varchar](50) NOT NULL,
	[instance_db] [varchar](50) NOT NULL,
	[created_on] [datetime] NOT NULL
) ON [PRIMARY];
GO

CREATE TABLE [dbo].[binding](
	[binding_id] [varchar](50) NOT NULL,
	[instance_id] [varchar](50) NOT NULL,
	[application_id] [varchar](50) NOT NULL,
	[username] [varchar](50) NOT NULL,
	[password] [varchar](50) NOT NULL,
	[created_on] [datetime] NOT NULL
) ON [PRIMARY];
GO

CREATE USER [cf_admin_sa] FOR LOGIN [cf_admin_sa]
GO
ALTER USER [cf_admin_sa] WITH DEFAULT_SCHEMA=[dbo]
GO
ALTER ROLE [db_owner] ADD MEMBER [cf_admin_sa]
GO

