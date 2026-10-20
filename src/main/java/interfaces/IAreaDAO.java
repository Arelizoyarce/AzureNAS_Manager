package interfaces;

import java.util.List;
import modelo.Area;

public interface IAreaDAO {

    List<Area> listar();

    Area buscarPorId(int id);

    int insertar(Area a);
}
