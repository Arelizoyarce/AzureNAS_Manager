package interfaces;

import java.util.List;
import modelo.Recurso;
import modelo.TipoRecurso;

public interface IRecursoDAO {

    /** Busqueda avanzada. usuarioId/areaId null = sin restriccion (administrador). */
    List<Recurso> buscar(String texto, TipoRecurso tipo, Integer usuarioId, Integer areaId);

    Recurso buscarPorId(int id);

    int insertar(Recurso r);

    void actualizarTamano(int id, long tamanoBytes);
}
