CREATE TABLE [dbo].[Usuario]
(
	[Id] INT NOT NULL PRIMARY KEY IDENTITY, 
    [Nome] VARCHAR(60) NOT NULL, 
	[Cpf] VARCHAR(11) NOT NULL,
    [DataNascimento] DATETIME2 NOT NULL, 
    [Sexo] BIT NOT NULL, 
    [Ponto] BIGINT NOT NULL, 
    [Foto] VARBINARY(MAX) NULL
    
)
