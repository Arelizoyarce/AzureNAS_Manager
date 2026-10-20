package modelo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** Tabla transferencia (subida, descarga o sincronizacion). */
public class Transferencia {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private Long id;
    private Integer usuarioId;
    private TipoTransferencia tipo;
    private Integer recursoId;
    private String rutaRemota;
    private String nombreArchivo;
    private Integer recursoDestinoId;
    private String rutaDestino;
    private Long tamanoBytes;
    private boolean sobrescribir;
    private Integer objetosTotales;
    private Integer objetosProcesados;
    private int progreso;
    private EstadoTransferencia estado;
    private String mensajeError;
    private LocalDateTime fechaSolicitud;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;

    public Transferencia() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Integer usuarioId) {
        this.usuarioId = usuarioId;
    }

    public TipoTransferencia getTipo() {
        return tipo;
    }

    public void setTipo(TipoTransferencia tipo) {
        this.tipo = tipo;
    }

    public Integer getRecursoId() {
        return recursoId;
    }

    public void setRecursoId(Integer recursoId) {
        this.recursoId = recursoId;
    }

    public String getRutaRemota() {
        return rutaRemota;
    }

    public void setRutaRemota(String rutaRemota) {
        this.rutaRemota = rutaRemota;
    }

    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }

    public Integer getRecursoDestinoId() {
        return recursoDestinoId;
    }

    public void setRecursoDestinoId(Integer recursoDestinoId) {
        this.recursoDestinoId = recursoDestinoId;
    }

    public String getRutaDestino() {
        return rutaDestino;
    }

    public void setRutaDestino(String rutaDestino) {
        this.rutaDestino = rutaDestino;
    }

    public Long getTamanoBytes() {
        return tamanoBytes;
    }

    public void setTamanoBytes(Long tamanoBytes) {
        this.tamanoBytes = tamanoBytes;
    }

    public boolean isSobrescribir() {
        return sobrescribir;
    }

    public void setSobrescribir(boolean sobrescribir) {
        this.sobrescribir = sobrescribir;
    }

    public Integer getObjetosTotales() {
        return objetosTotales;
    }

    public void setObjetosTotales(Integer objetosTotales) {
        this.objetosTotales = objetosTotales;
    }

    public Integer getObjetosProcesados() {
        return objetosProcesados;
    }

    public void setObjetosProcesados(Integer objetosProcesados) {
        this.objetosProcesados = objetosProcesados;
    }

    public int getProgreso() {
        return progreso;
    }

    public void setProgreso(int progreso) {
        this.progreso = progreso;
    }

    public EstadoTransferencia getEstado() {
        return estado;
    }

    public void setEstado(EstadoTransferencia estado) {
        this.estado = estado;
    }

    public String getMensajeError() {
        return mensajeError;
    }

    public void setMensajeError(String mensajeError) {
        this.mensajeError = mensajeError;
    }

    public LocalDateTime getFechaSolicitud() {
        return fechaSolicitud;
    }

    public void setFechaSolicitud(LocalDateTime fechaSolicitud) {
        this.fechaSolicitud = fechaSolicitud;
    }

    public String getFechaSolicitudTexto() {
        return fechaSolicitud == null ? "" : fechaSolicitud.format(FMT);
    }

    public LocalDateTime getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDateTime fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public String getFechaInicioTexto() {
        return fechaInicio == null ? "" : fechaInicio.format(FMT);
    }

    public LocalDateTime getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDateTime fechaFin) {
        this.fechaFin = fechaFin;
    }

    public String getFechaFinTexto() {
        return fechaFin == null ? "" : fechaFin.format(FMT);
    }

    public String getTamanoLegible() {
        return util.Formato.bytes(tamanoBytes);
    }

}
