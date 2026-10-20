package servicio;

import dao.AccesoRecursoDAO;
import dao.CuentaAlmacenamientoDAO;
import dao.RecursoDAO;
import interfaces.IAccesoRecursoDAO;
import interfaces.IAlmacenamientoClient;
import interfaces.ICuentaAlmacenamientoDAO;
import interfaces.IRecursoDAO;
import java.io.IOException;
import java.util.List;
import modelo.AccesoRecurso;
import modelo.CuentaAlmacenamiento;
import modelo.NivelAcceso;
import modelo.Recurso;
import modelo.ResultadoOperacion;
import modelo.RolUsuario;
import modelo.TipoCredencial;
import modelo.TipoOperacion;
import modelo.TipoRecurso;
import modelo.Usuario;
import util.CifradoUtil;

public class RecursoServicio {

    private final IRecursoDAO recursoDAO = new RecursoDAO();
    private final IAccesoRecursoDAO accesoDAO = new AccesoRecursoDAO();
    private final ICuentaAlmacenamientoDAO cuentaDAO = new CuentaAlmacenamientoDAO();
    private final IAlmacenamientoClient cliente;
    private final AuditoriaServicio auditoria = new AuditoriaServicio();

    public RecursoServicio(IAlmacenamientoClient cliente) {
        this.cliente = cliente;
    }

    public List<Recurso> buscar(Usuario u, String texto, TipoRecurso tipo) {
        if (u.isAdmin()) {
            return recursoDAO.buscar(texto, tipo, null, null);
        }
        return recursoDAO.buscar(texto, tipo, u.getId(), u.getAreaId());
    }

    public Recurso obtener(int id) {
        Recurso r = recursoDAO.buscarPorId(id);
        if (r == null) {
            throw new ServicioException("El recurso no existe");
        }
        return r;
    }

    /** Admin: todo. Area propietaria: lectura/escritura. Otros: segun acceso_recurso. LECTOR nunca escribe. */
    public boolean tieneAcceso(Usuario u, Recurso r, NivelAcceso requerido) {
        if (u.isAdmin()) {
            return true;
        }
        if (requerido != NivelAcceso.LECTURA && u.getRol() == RolUsuario.LECTOR) {
            return false;
        }
        if (u.getAreaId() != null && u.getAreaId().equals(r.getAreaId()) && requerido != NivelAcceso.ADMIN) {
            return true;
        }
        NivelAcceso n = accesoDAO.nivelDe(u.getId(), r.getId());
        return n != null && n.ordinal() >= requerido.ordinal();
    }

    public Recurso exigir(Usuario u, int recursoId, NivelAcceso requerido, String ip) {
        Recurso r = obtener(recursoId);
        if (!tieneAcceso(u, r, requerido)) {
            auditoria.registrar(u.getId(), TipoOperacion.CONSULTA, r.getId(), "", null, ResultadoOperacion.DENEGADO, ip,
                    "Acceso denegado (requiere " + requerido + ")");
            throw new ServicioException("No tienes permiso para esta operacion sobre el recurso");
        }
        return r;
    }

    public List<String> explorar(Usuario u, int recursoId, String ruta, String ip) {
        Recurso r = exigir(u, recursoId, NivelAcceso.LECTURA, ip);
        try {
            return cliente.listar(r, ruta);
        } catch (IOException e) {
            throw new ServicioException("No se pudo listar la ruta: " + e.getMessage(), e);
        }
    }

    public List<CuentaAlmacenamiento> listarCuentas() {
        return cuentaDAO.listar();
    }

    public void crearCuenta(Usuario admin, String nombre, String region, String tipoCredencial,
            String credencial, Integer espacioGb, String ip) {
        UsuarioServicio.exigirAdmin(admin);
        if (nombre == null || !nombre.matches("[a-z0-9]{3,24}")) {
            throw new ServicioException("Nombre de cuenta: 3-24 caracteres, solo minusculas y numeros");
        }
        if (region == null || region.trim().isEmpty() || region.length() > 50) {
            throw new ServicioException("La region es obligatoria (max. 50)");
        }
        if (credencial == null || credencial.isEmpty()) {
            throw new ServicioException("La credencial es obligatoria");
        }
        if (espacioGb == null || espacioGb <= 0) {
            throw new ServicioException("El espacio asignado debe ser mayor que 0 GB");
        }
        CuentaAlmacenamiento c = new CuentaAlmacenamiento();
        c.setNombre(nombre);
        c.setRegion(region.trim());
        try {
            c.setTipoCredencial(TipoCredencial.valueOf(tipoCredencial));
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new ServicioException("Tipo de credencial invalido");
        }
        c.setCredencialCifrada(CifradoUtil.cifrar(credencial));
        c.setEspacioAsignadoBytes(espacioGb * 1024L * 1024L * 1024L);
        int id = cuentaDAO.insertar(c);
        auditoria.registrar(admin.getId(), TipoOperacion.ADMINISTRACION, null, "cuenta/" + id, null,
                ResultadoOperacion.EXITO, ip, "Cuenta de almacenamiento creada: " + nombre);
    }

    public void crearRecurso(Usuario admin, Integer cuentaId, Integer areaId, String tipo, String nombre,
            Integer cuotaGb, String ip) {
        UsuarioServicio.exigirAdmin(admin);
        if (cuentaId == null || areaId == null) {
            throw new ServicioException("Selecciona cuenta y area");
        }
        if (nombre == null || !nombre.matches("[a-z0-9][a-z0-9-]{1,61}[a-z0-9]")) {
            throw new ServicioException("Nombre del recurso: 3-63 caracteres, minusculas, numeros y guiones");
        }
        Recurso r = new Recurso();
        r.setCuentaId(cuentaId);
        r.setAreaId(areaId);
        r.setNombre(nombre);
        r.setCuotaGb(cuotaGb);
        try {
            r.setTipo(TipoRecurso.valueOf(tipo));
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new ServicioException("Tipo de recurso invalido");
        }
        int id = recursoDAO.insertar(r);
        auditoria.registrar(admin.getId(), TipoOperacion.ADMINISTRACION, id, nombre, null,
                ResultadoOperacion.EXITO, ip, "Recurso creado (" + r.getTipo() + ")");
    }

    public void asignarAcceso(Usuario admin, Integer usuarioId, Integer recursoId, String nivel, String ip) {
        UsuarioServicio.exigirAdmin(admin);
        if (usuarioId == null || recursoId == null) {
            throw new ServicioException("Selecciona usuario y recurso");
        }
        AccesoRecurso a = new AccesoRecurso();
        a.setUsuarioId(usuarioId);
        a.setRecursoId(recursoId);
        try {
            a.setNivel(NivelAcceso.valueOf(nivel));
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new ServicioException("Nivel de acceso invalido");
        }
        accesoDAO.asignar(a);
        auditoria.registrar(admin.getId(), TipoOperacion.ADMINISTRACION, recursoId, "", null,
                ResultadoOperacion.EXITO, ip, "Acceso " + a.getNivel() + " asignado al usuario " + usuarioId);
    }
}
