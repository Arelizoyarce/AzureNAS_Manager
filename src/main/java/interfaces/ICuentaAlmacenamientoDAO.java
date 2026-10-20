package interfaces;

import java.util.List;
import modelo.CuentaAlmacenamiento;

public interface ICuentaAlmacenamientoDAO {

    List<CuentaAlmacenamiento> listar();

    CuentaAlmacenamiento buscarPorId(int id);

    int insertar(CuentaAlmacenamiento c);
}
