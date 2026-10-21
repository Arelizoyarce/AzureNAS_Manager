package servicio;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.UncheckedExecutionException;
import dao.AreaDAO;
import interfaces.IAreaDAO;
import interfaces.INotificador;
import interfaces.IUsuarioDAO;
import java.util.List;
import java.util.concurrent.TimeUnit;
import modelo.Area;
import modelo.ResultadoOperacion;
import modelo.RolUsuario;
import modelo.TipoOperacion;
import modelo.Usuario;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.PasswordUtil;

public class UsuarioServicio {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioServicio.class);

    /** Cache (Guava) de las areas: cambian poco, asi se evita consultar la BD en cada pantalla. */
    private static final String CLAVE_AREAS = "todas";
    private static final LoadingCache<String, List<Area>> CACHE_AREAS = CacheBuilder.newBuilder()
            .expireAfterWrite(60, TimeUnit.SECONDS)
            .build(new CacheLoader<String, List<Area>>() {
                @Override
                public List<Area> load(String clave) {
                    return ImmutableList.copyOf(new AreaDAO().listar());
                }
            });

    private final IUsuarioDAO usuarioDAO;
    private final IAreaDAO areaDAO = new AreaDAO();
    private final INotificador notificador;
    private final AuditoriaServicio auditoria = new AuditoriaServicio();

    public UsuarioServicio(IUsuarioDAO usuarioDAO, INotificador notificador) {
        this.usuarioDAO = usuarioDAO;
        this.notificador = notificador;
    }

    /** Valida credenciales y registra el intento en auditoria. */
    public Usuario autenticar(String username, String password, String ip) {
        if (StringUtils.isBlank(username) || StringUtils.isEmpty(password)) {
            throw new ServicioException("Ingresa usuario y contrasena");
        }
        Usuario u = usuarioDAO.buscarPorUsername(username.trim());
        if (u == null) {
            auditoria.registrar(null, TipoOperacion.LOGIN, null, "", null, ResultadoOperacion.FALLO, ip,
                    "Usuario inexistente: " + username);
            throw new ServicioException("Usuario o contrasena incorrectos");
        }
        if (!u.isActivo()) {
            auditoria.registrar(u.getId(), TipoOperacion.LOGIN, null, "", null, ResultadoOperacion.DENEGADO, ip, "Usuario inactivo");
            throw new ServicioException("El usuario esta desactivado");
        }
        if (!PasswordUtil.verificar(password, u.getPasswordHash())) {
            auditoria.registrar(u.getId(), TipoOperacion.LOGIN, null, "", null, ResultadoOperacion.FALLO, ip, "Contrasena incorrecta");
            throw new ServicioException("Usuario o contrasena incorrectos");
        }
        usuarioDAO.registrarAcceso(u.getId());
        auditoria.registrar(u.getId(), TipoOperacion.LOGIN, null, "", null, ResultadoOperacion.EXITO, ip, "Inicio de sesion");
        logger.info("Usuario autenticado: {}", u.getUsername());
        return u;
    }

    public void cerrarSesion(Usuario u, String ip) {
        if (u != null) {
            auditoria.registrar(u.getId(), TipoOperacion.LOGOUT, null, "", null, ResultadoOperacion.EXITO, ip, "Cierre de sesion");
        }
    }

    public List<Usuario> listar() {
        return usuarioDAO.listar();
    }

    public List<Area> listarAreas() {
        try {
            return CACHE_AREAS.getUnchecked(CLAVE_AREAS);
        } catch (UncheckedExecutionException e) {
            if (e.getCause() instanceof RuntimeException) {
                throw (RuntimeException) e.getCause();
            }
            throw e;
        }
    }

    public int contar() {
        return usuarioDAO.contar();
    }

    public void registrarUsuario(Usuario admin, String username, String nombreCompleto, String password,
            String rol, Integer areaId, String ip) {
        exigirAdmin(admin);
        if (username == null || !username.matches("[A-Za-z0-9._-]{3,50}")) {
            throw new ServicioException("El usuario debe tener 3-50 caracteres (letras, numeros, . _ -)");
        }
        if (StringUtils.isBlank(nombreCompleto) || nombreCompleto.length() > 150) {
            throw new ServicioException("El nombre completo es obligatorio (max. 150)");
        }
        if (StringUtils.length(password) < 8) {
            throw new ServicioException("La contrasena debe tener al menos 8 caracteres");
        }
        if (usuarioDAO.buscarPorUsername(username) != null) {
            throw new ServicioException("Ya existe un usuario con ese nombre");
        }
        RolUsuario r;
        try {
            r = RolUsuario.valueOf(rol);
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new ServicioException("Rol invalido");
        }
        Usuario u = new Usuario();
        u.setUsername(username);
        u.setNombreCompleto(nombreCompleto.trim());
        u.setPasswordHash(PasswordUtil.hash(password));
        u.setRol(r);
        u.setAreaId(areaId);
        u.setActivo(true);
        int id = usuarioDAO.insertar(u);
        auditoria.registrar(admin.getId(), TipoOperacion.ADMINISTRACION, null, "usuario/" + id, null,
                ResultadoOperacion.EXITO, ip, "Usuario creado: " + username + " (" + r + ")");
        notificador.notificar(username, "Cuenta creada", "Se creo tu cuenta en AzureNAS Manager.");
    }

    public void cambiarEstado(Usuario admin, int id, boolean activo, String ip) {
        exigirAdmin(admin);
        if (admin.getId() == id && !activo) {
            throw new ServicioException("No puedes desactivar tu propio usuario");
        }
        usuarioDAO.actualizarActivo(id, activo);
        auditoria.registrar(admin.getId(), TipoOperacion.ADMINISTRACION, null, "usuario/" + id, null,
                ResultadoOperacion.EXITO, ip, activo ? "Usuario activado" : "Usuario desactivado");
    }

    public void crearArea(Usuario admin, String nombre, String ip) {
        exigirAdmin(admin);
        if (StringUtils.isBlank(nombre) || nombre.length() > 100) {
            throw new ServicioException("El nombre del area es obligatorio (max. 100)");
        }
        Area a = new Area();
        a.setNombre(nombre.trim());
        int id = areaDAO.insertar(a);
        CACHE_AREAS.invalidateAll();
        auditoria.registrar(admin.getId(), TipoOperacion.ADMINISTRACION, null, "area/" + id, null,
                ResultadoOperacion.EXITO, ip, "Area creada: " + a.getNombre());
    }

    static void exigirAdmin(Usuario u) {
        if (u == null || !u.isAdmin()) {
            throw new ServicioException("Acceso denegado: se requiere rol administrador");
        }
    }
}
