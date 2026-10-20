package interfaces;

import java.util.List;
import modelo.Transferencia;

public interface ITransferenciaDAO {

    long insertar(Transferencia t);

    void actualizar(Transferencia t);

    Transferencia buscarPorId(long id);

    List<Transferencia> listarPorUsuario(int usuarioId, int limite);

    List<Transferencia> listarRecientes(int limite);
}
