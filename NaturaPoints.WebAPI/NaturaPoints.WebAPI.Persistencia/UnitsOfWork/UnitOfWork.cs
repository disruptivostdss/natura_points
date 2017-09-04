using NaturaPoints.WebAPI.Dominio.Models;
using NaturaPoints.WebAPI.Persistencia.Repositories;
using System;

namespace NaturaPoints.WebAPI.Persistencia.UnitsOfWork
{
    public class UnitOfWork : IDisposable
    {

        #region FIELDS
        private PortalContext _context = new PortalContext();

        private IGenericRepository<Usuario> _usuarioRepository;

        private IGenericRepository<Localidade> _localidadeRepository;

        private IGenericRepository<Checkin> _checkinRepository;

        #endregion

        #region GETS
        

        public IGenericRepository<Usuario> UsuarioRespository
        {
            get
            {
                if(_usuarioRepository == null)
                {
                    _usuarioRepository = new GenericRepository<Usuario>(_context);
                }
                return _usuarioRepository;
            }
            
        }

        public IGenericRepository<Localidade> LocalidadeRespository
        {
            get
            {
                if (_localidadeRepository == null)
                {
                    _localidadeRepository = new GenericRepository<Localidade>(_context);
                }
                return _localidadeRepository;
            }

        }

        public IGenericRepository<Checkin> CheckinRespository
        {
            get
            {
                if (_checkinRepository == null)
                {
                    _checkinRepository = new GenericRepository<Checkin>(_context);
                }
                return _checkinRepository;
            }

        }

        #endregion

        #region SALVAR
        public void Salvar()
        {
                _context.SaveChanges();
        }
        #endregion

        #region DISPOSE
        public void Dispose()
        {
            if(_context != null)
            {
                _context.Dispose();
            }
            GC.SuppressFinalize(this);
        }
        #endregion
    }
}
