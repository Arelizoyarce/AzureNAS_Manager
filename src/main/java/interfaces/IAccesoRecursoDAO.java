package interfaces;

import java.util.List;
import modelo.AccesoRecurso;
import modelo.NivelAcceso;

public interface IAccesoRecursoDAO {

    NivelAcceso nivelDe(int usuarioId, int recursoId);

    List<AccesoRecurso> listarPorUsuario(int usuarioId);

    void asignar(AccesoRecurso a);

    void revocar(int usuarioId, int recursoId);
}
