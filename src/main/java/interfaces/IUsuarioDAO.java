package interfaces;

import java.util.List;
import modelo.Usuario;

public interface IUsuarioDAO {

    List<Usuario> listar();

    Usuario buscarPorId(int id);

    Usuario buscarPorUsername(String username);

    int insertar(Usuario u);

    void actualizarActivo(int id, boolean activo);

    void registrarAcceso(int id);

    int contar();
}
