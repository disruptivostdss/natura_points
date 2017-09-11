using NaturaPoints.WebAPI.Dominio.Models;
using NaturaPoints.WebAPI.Persistencia.UnitsOfWork;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Http;
using System.Web.Http.Cors;
using System.Web.Mvc;

namespace NaturaPoints.WebAPI.Controllers
{
    [EnableCors(origins: "*", headers: "*", methods: "*")]
    public class UsuarioController : ApiController
    {
        private UnitOfWork _unit = new UnitOfWork();


        public IEnumerable<Usuario> Get()
        {
            return _unit.UsuarioRepository.Listar();
        }


        public Usuario Get(int id)
        {
            return _unit.UsuarioRepository.BuscarPorId(id);
        }

        public IEnumerable<Usuario> Get(string nome)
        {
            return _unit.UsuarioRepository.BuscarPor(u => u.Nome == nome);
        }

        public IHttpActionResult Post(Usuario checkin)
        {
            if (ModelState.IsValid)
            {
                _unit.UsuarioRepository.Cadastrar(checkin);
                _unit.Salvar();
                var uri = Url.Link("DefaultApi", new { id = checkin.Id });
                return Created<Usuario>(new Uri(uri), checkin);
            }
            else
            {
                return BadRequest(ModelState);
            }
        }


        public IHttpActionResult Put(int id, Usuario checkin)
        {
            if (ModelState.IsValid)
            {
                checkin.Id = id;
                _unit.UsuarioRepository.Atualizar(checkin);
                _unit.Salvar();
                return Ok(checkin);
            }
            else
            {
                return BadRequest(ModelState);
            }
        }

        public void Delete(int id)
        {
            _unit.UsuarioRepository.Remover(id);
            _unit.Salvar();
        }

    }
}