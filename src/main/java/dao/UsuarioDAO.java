package dao;

import interfaces.IUsuarioDAO;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import modelo.RolUsuario;
import modelo.Usuario;

public class UsuarioDAO implements IUsuarioDAO {

    private static final String COLS
            = "id, username, password_hash, nombre_completo, rol, area_id, activo, fecha_creacion, ultimo_acceso";

    private static Usuario map(ResultSet rs) throws SQLException {
        Usuario u = new Usuario();
        u.setId(rs.getInt("id"));
        u.setUsername(rs.getString("username"));
        u.setPasswordHash(rs.getString("password_hash"));
        u.setNombreCompleto(rs.getString("nombre_completo"));
        u.setRol(JdbcUtil.getEnum(rs, "rol", RolUsuario.class));
        u.setAreaId(JdbcUtil.getInt(rs, "area_id"));
        u.setActivo(rs.getBoolean("activo"));
        u.setFechaCreacion(JdbcUtil.getFecha(rs, "fecha_creacion"));
        u.setUltimoAcceso(JdbcUtil.getFecha(rs, "ultimo_acceso"));
        return u;
    }

    @Override
    public List<Usuario> listar() {
        return JdbcUtil.consultar("SELECT " + COLS + " FROM usuario ORDER BY username", UsuarioDAO::map);
    }

    @Override
    public Usuario buscarPorId(int id) {
        return JdbcUtil.uno("SELECT " + COLS + " FROM usuario WHERE id = ?", UsuarioDAO::map, id);
    }

    @Override
    public Usuario buscarPorUsername(String username) {
        return JdbcUtil.uno("SELECT " + COLS + " FROM usuario WHERE username = ?", UsuarioDAO::map, username);
    }

    @Override
    public int insertar(Usuario u) {
        return (int) JdbcUtil.insertar(
                "INSERT INTO usuario (username, password_hash, nombre_completo, rol, area_id, activo, fecha_creacion) "
                + "VALUES (?, ?, ?, ?, ?, ?, SYSDATETIME())",
                u.getUsername(), u.getPasswordHash(), u.getNombreCompleto(), u.getRol(), u.getAreaId(), u.isActivo());
    }

    @Override
    public void actualizarActivo(int id, boolean activo) {
        JdbcUtil.ejecutar("UPDATE usuario SET activo = ? WHERE id = ?", activo, id);
    }

    @Override
    public void registrarAcceso(int id) {
        JdbcUtil.ejecutar("UPDATE usuario SET ultimo_acceso = SYSDATETIME() WHERE id = ?", id);
    }

    @Override
    public int contar() {
        Integer n = JdbcUtil.uno("SELECT COUNT(*) AS n FROM usuario", rs -> rs.getInt("n"));
        return n == null ? 0 : n;
    }
}
