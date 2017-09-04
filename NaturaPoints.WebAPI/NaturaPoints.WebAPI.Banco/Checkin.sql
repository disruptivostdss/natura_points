CREATE TABLE [dbo].[Checkin]
(
	[Id] INT NOT NULL PRIMARY KEY IDENTITY, 
    [Data] DATETIME2 NOT NULL, 
    [UsuarioId] INT NOT NULL, 
    [LocalidadeId] INT NOT NULL, 
    CONSTRAINT [FK_Checkin_Usuario] FOREIGN KEY ([UsuarioId]) REFERENCES [dbo].[Usuario]([Id]), 
    CONSTRAINT [FK_Checkin_Localidade] FOREIGN KEY ([LocalidadeId]) REFERENCES [dbo].[Localidade]([Id])
)
