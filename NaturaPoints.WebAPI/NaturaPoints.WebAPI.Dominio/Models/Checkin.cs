//------------------------------------------------------------------------------
// <auto-generated>
//     This code was generated from a template.
//
//     Manual changes to this file may cause unexpected behavior in your application.
//     Manual changes to this file will be overwritten if the code is regenerated.
// </auto-generated>
//------------------------------------------------------------------------------

namespace NaturaPoints.WebAPI.Dominio.Models
{
    using System;
    using System.Collections.Generic;
    
    public partial class Checkin
    {
        public int Id { get; set; }
        public System.DateTime Data { get; set; }
        public int UsuarioId { get; set; }
        public int LocalidadeId { get; set; }
    
        public virtual Localidade Localidade { get; set; }
        public virtual Usuario Usuario { get; set; }
    }
}