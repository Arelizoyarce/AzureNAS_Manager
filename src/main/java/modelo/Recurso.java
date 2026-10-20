package modelo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** Tabla recurso (File Share o Blob Container). */
public class Recurso {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private Integer id;
    private Integer cuentaId;
    private Integer areaId;
    private TipoRecurso tipo;
    private String nombre;
    private Long tamanoBytes;
    private Integer cuotaGb;
    private LocalDateTime ultimaModificacion;
    private LocalDateTime metadatosActualizadosEn;

    public Recurso() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCuentaId() {
        return cuentaId;
    }

    public void setCuentaId(Integer cuentaId) {
        this.cuentaId = cuentaId;
    }

    public Integer getAreaId() {
        return areaId;
    }

    public void setAreaId(Integer areaId) {
        this.areaId = areaId;
    }

    public TipoRecurso getTipo() {
        return tipo;
    }

    public void setTipo(TipoRecurso tipo) {
        this.tipo = tipo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Long getTamanoBytes() {
        return tamanoBytes;
    }

    public void setTamanoBytes(Long tamanoBytes) {
        this.tamanoBytes = tamanoBytes;
    }

    public Integer getCuotaGb() {
        return cuotaGb;
    }

    public void setCuotaGb(Integer cuotaGb) {
        this.cuotaGb = cuotaGb;
    }

    public LocalDateTime getUltimaModificacion() {
        return ultimaModificacion;
    }

    public void setUltimaModificacion(LocalDateTime ultimaModificacion) {
        this.ultimaModificacion = ultimaModificacion;
    }

    public String getUltimaModificacionTexto() {
        return ultimaModificacion == null ? "" : ultimaModificacion.format(FMT);
    }

    public LocalDateTime getMetadatosActualizadosEn() {
        return metadatosActualizadosEn;
    }

    public void setMetadatosActualizadosEn(LocalDateTime metadatosActualizadosEn) {
        this.metadatosActualizadosEn = metadatosActualizadosEn;
    }

    public String getMetadatosActualizadosEnTexto() {
        return metadatosActualizadosEn == null ? "" : metadatosActualizadosEn.format(FMT);
    }

    public String getTamanoLegible() {
        return util.Formato.bytes(tamanoBytes);
    }

}
