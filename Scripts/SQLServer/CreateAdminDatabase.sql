USE [master]
GO

CREATE DATABASE cf_admin;
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

CREATE TABLE [dbo].[binding](
	[binding_id] [varchar](50) NOT NULL,
	[instance_id] [varchar](50) NOT NULL,
	[application_id] [varchar](50) NOT NULL,
	[username] [varchar](50) NOT NULL,
	[password] [varchar](50) NOT NULL,
	[created_on] [datetime] NOT NULL
) ON [PRIMARY];

