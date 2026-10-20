package modelo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** Tabla registro_auditoria (con hash de integridad SHA-256). */
public class RegistroAuditoria {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private Long id;
    private LocalDateTime fechaHora;
    private Integer usuarioId;
    private TipoOperacion operacion;
    private Integer recursoId;
    private String ruta;
    private Long transferenciaId;
    private ResultadoOperacion resultado;
    private String ipOrigen;
    private String detalle;
    private String hashIntegridad;
    private boolean integro;

    public RegistroAuditoria() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getFechaHoraTexto() {
        return fechaHora == null ? "" : fechaHora.format(FMT);
    }

    public Integer getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Integer usuarioId) {
        this.usuarioId = usuarioId;
    }

    public TipoOperacion getOperacion() {
        return operacion;
    }

    public void setOperacion(TipoOperacion operacion) {
        this.operacion = operacion;
    }

    public Integer getRecursoId() {
        return recursoId;
    }

    public void setRecursoId(Integer recursoId) {
        this.recursoId = recursoId;
    }

    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }

    public Long getTransferenciaId() {
        return transferenciaId;
    }

    public void setTransferenciaId(Long transferenciaId) {
        this.transferenciaId = transferenciaId;
    }

    public ResultadoOperacion getResultado() {
        return resultado;
    }

    public void setResultado(ResultadoOperacion resultado) {
        this.resultado = resultado;
    }

    public String getIpOrigen() {
        return ipOrigen;
    }

    public void setIpOrigen(String ipOrigen) {
        this.ipOrigen = ipOrigen;
    }

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }

    public String getHashIntegridad() {
        return hashIntegridad;
    }

    public void setHashIntegridad(String hashIntegridad) {
        this.hashIntegridad = hashIntegridad;
    }

    public boolean isIntegro() {
        return integro;
    }

    public void setIntegro(boolean integro) {
        this.integro = integro;
    }

}
