package interfaces;

import java.util.List;
import modelo.RegistroAuditoria;

public interface IAuditoriaDAO {

    long insertar(RegistroAuditoria r);

    List<RegistroAuditoria> listarUltimos(int limite);
}
