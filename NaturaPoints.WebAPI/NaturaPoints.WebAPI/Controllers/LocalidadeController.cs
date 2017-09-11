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
    public class LocalidadeController : ApiController
    {
        private UnitOfWork _unit = new UnitOfWork();


        public IEnumerable<Localidade> Get()
        {
            return _unit.LocalidadeRepository.Listar();
        }


        public Localidade Get(int id)
        {
            return _unit.LocalidadeRepository.BuscarPorId(id);
        }

        
        public IHttpActionResult Post(Localidade localidade)
        {
            if (ModelState.IsValid)
            {
                _unit.LocalidadeRepository.Cadastrar(localidade);
                _unit.Salvar();
                var uri = Url.Link("DefaultApi", new { id = localidade.Id });
                return Created<Localidade>(new Uri(uri), localidade);
            }
            else
            {
                return BadRequest(ModelState);
            }
        }


        public IHttpActionResult Put(int id, Localidade localidade)
        {
            if (ModelState.IsValid)
            {
                localidade.Id = id;
                _unit.LocalidadeRepository.Atualizar(localidade);
                _unit.Salvar();
                return Ok(localidade);
            }
            else
            {
                return BadRequest(ModelState);
            }
        }

        public void Delete(int id)
        {
            _unit.LocalidadeRepository.Remover(id);
            _unit.Salvar();
        }

    }
}