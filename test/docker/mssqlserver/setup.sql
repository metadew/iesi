CREATE DATABASE iesi;
GO
USE iesi;
GO
CREATE TABLE Table1 ( Field1 int, Field2 nvarchar(max), Field3  nvarchar(max), Field4  nvarchar(max), Field5  nvarchar(max), Field6  nvarchar(max), Field7  nvarchar(max), Field8  nvarchar(max), Field9  nvarchar(max), Field10  nvarchar(max) );
GO
CREATE PROCEDURE GetTable1All AS select Field1, Field2, Field3, Field4, Field5, Field6, Field7, Field8, Field9, Field10 from Table1;
GO
CREATE PROCEDURE GetTable1ByField1 @Field1 int AS select Field1, Field2, Field3, Field4, Field5, Field6, Field7, Field8, Field9, Field10 from Table1 where Field1 = @Field1;
GO