CREATE TABLE [dbo].[Localidade]
(
	[Id] INT NOT NULL PRIMARY KEY IDENTITY, 
    [Descricao] VARCHAR(100) NOT NULL, 
    [Longitude] FLOAT NOT NULL, 
    [Latitude] FLOAT NOT NULL
)
