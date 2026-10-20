package servicio;

import dao.EjecucionPowerShellDAO;
import interfaces.IComandoExecutor;
import interfaces.IEjecucionPowerShellDAO;
import java.time.LocalDateTime;
import java.util.List;
import modelo.EjecucionPowerShell;
import modelo.EstadoComando;
import modelo.ResultadoOperacion;
import modelo.TipoOperacion;
import modelo.Usuario;

public class PowerShellServicio {

    private final IEjecucionPowerShellDAO dao = new EjecucionPowerShellDAO();
    private final IComandoExecutor executor;
    private final AuditoriaServicio auditoria = new AuditoriaServicio();

    public PowerShellServicio(IComandoExecutor executor) {
        this.executor = executor;
    }

    public EjecucionPowerShell ejecutar(Usuario admin, String comando, String ip) {
        UsuarioServicio.exigirAdmin(admin);
        if (comando == null || comando.trim().isEmpty()) {
            throw new ServicioException("Escribe un comando");
        }
        EjecucionPowerShell e = new EjecucionPowerShell();
        e.setAdministradorId(admin.getId());
        e.setComando(comando.trim());
        e.setEstado(EstadoComando.PENDIENTE);
        e.setFechaHora(LocalDateTime.now());
        e.setIpOrigen(ip);
        e.setId(dao.insertar(e));

        IComandoExecutor.Resultado r = executor.ejecutar(e.getComando());
        e.setEstado(r.exito ? EstadoComando.EXITOSO : EstadoComando.FALLIDO);
        e.setSalida(r.salida);
        dao.actualizarResultado(e);

        auditoria.registrar(admin.getId(), TipoOperacion.COMANDO_POWERSHELL, null, "", null,
                r.exito ? ResultadoOperacion.EXITO : ResultadoOperacion.FALLO, ip, e.getComando());
        return e;
    }

    public List<EjecucionPowerShell> ultimas(int limite) {
        return dao.listarUltimas(limite);
    }
}
