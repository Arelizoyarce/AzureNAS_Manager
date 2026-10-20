package interfaces;

import java.util.List;
import modelo.EjecucionPowerShell;

public interface IEjecucionPowerShellDAO {

    long insertar(EjecucionPowerShell e);

    void actualizarResultado(EjecucionPowerShell e);

    List<EjecucionPowerShell> listarUltimas(int limite);
}
