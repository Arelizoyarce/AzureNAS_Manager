package modelo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** Tabla acceso_recurso (PK compuesta usuario_id + recurso_id). */
public class AccesoRecurso {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private Integer usuarioId;
    private Integer recursoId;
    private NivelAcceso nivel;
    private LocalDateTime fechaAsignacion;

    public AccesoRecurso() {
    }

    public Integer getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Integer usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Integer getRecursoId() {
        return recursoId;
    }

    public void setRecursoId(Integer recursoId) {
        this.recursoId = recursoId;
    }

    public NivelAcceso getNivel() {
        return nivel;
    }

    public void setNivel(NivelAcceso nivel) {
        this.nivel = nivel;
    }

    public LocalDateTime getFechaAsignacion() {
        return fechaAsignacion;
    }

    public void setFechaAsignacion(LocalDateTime fechaAsignacion) {
        this.fechaAsignacion = fechaAsignacion;
    }

    public String getFechaAsignacionTexto() {
        return fechaAsignacion == null ? "" : fechaAsignacion.format(FMT);
    }

}
