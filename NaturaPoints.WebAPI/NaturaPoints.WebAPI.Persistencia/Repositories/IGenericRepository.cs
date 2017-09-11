using System;
using System.Collections.Generic;
using System.Linq;
using System.Linq.Expressions;
using System.Text;
using System.Threading.Tasks;

namespace NaturaPoints.WebAPI.Persistencia.Repositories
{
    public interface IGenericRepository<T>
    {
        void Cadastrar(T entidade);
        void Atualizar(T entidade);
        void Remover(int id);
        T BuscarPorId(int id);
        ICollection<T> Listar();
        ICollection<T> BuscarPor(Expression<Func<T, bool>> filtro);

    }
}
