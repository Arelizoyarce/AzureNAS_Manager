package modelo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** Tabla ejecucion_powershell (comandos ejecutados por administradores). */
public class EjecucionPowerShell {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private Long id;
    private Integer administradorId;
    private String comando;
    private String salida;
    private EstadoComando estado;
    private LocalDateTime fechaHora;
    private String ipOrigen;

    public EjecucionPowerShell() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getAdministradorId() {
        return administradorId;
    }

    public void setAdministradorId(Integer administradorId) {
        this.administradorId = administradorId;
    }

    public String getComando() {
        return comando;
    }

    public void setComando(String comando) {
        this.comando = comando;
    }

    public String getSalida() {
        return salida;
    }

    public void setSalida(String salida) {
        this.salida = salida;
    }

    public EstadoComando getEstado() {
        return estado;
    }

    public void setEstado(EstadoComando estado) {
        this.estado = estado;
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

    public String getIpOrigen() {
        return ipOrigen;
    }

    public void setIpOrigen(String ipOrigen) {
        this.ipOrigen = ipOrigen;
    }

}
