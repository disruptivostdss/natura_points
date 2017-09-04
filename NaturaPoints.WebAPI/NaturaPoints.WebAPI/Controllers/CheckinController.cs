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
    public class CheckinController : ApiController
    {
        private UnitOfWork _unit = new UnitOfWork();


        public IEnumerable<Checkin> Get()
        {
            return _unit.CheckinRepository.Listar();
        }


        public Checkin Get(int id)
        {
            return _unit.CheckinRepository.BuscarPorId(id);
        }

        
        public IHttpActionResult Post(Checkin checkin)
        {
            if (ModelState.IsValid)
            {
                _unit.CheckinRepository.Cadastrar(checkin);
                _unit.Salvar();
                var uri = Url.Link("DefaultApi", new { id = checkin.Id });
                return Created<Checkin>(new Uri(uri), checkin);
            }
            else
            {
                return BadRequest(ModelState);
            }
        }


        public IHttpActionResult Put(int id, Checkin checkin)
        {
            if (ModelState.IsValid)
            {
                checkin.Id = id;
                _unit.CheckinRepository.Atualizar(checkin);
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
            _unit.CheckinRepository.Remover(id);
            _unit.Salvar();
        }
    }
}