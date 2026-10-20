package modelo;

/** Tabla cuenta_almacenamiento (Azure Storage Account simulada). */
public class CuentaAlmacenamiento {

    private Integer id;
    private String nombre;
    private String region;
    private TipoCredencial tipoCredencial;
    private String credencialCifrada;
    private Long espacioAsignadoBytes;

    public CuentaAlmacenamiento() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public TipoCredencial getTipoCredencial() {
        return tipoCredencial;
    }

    public void setTipoCredencial(TipoCredencial tipoCredencial) {
        this.tipoCredencial = tipoCredencial;
    }

    public String getCredencialCifrada() {
        return credencialCifrada;
    }

    public void setCredencialCifrada(String credencialCifrada) {
        this.credencialCifrada = credencialCifrada;
    }

    public Long getEspacioAsignadoBytes() {
        return espacioAsignadoBytes;
    }

    public void setEspacioAsignadoBytes(Long espacioAsignadoBytes) {
        this.espacioAsignadoBytes = espacioAsignadoBytes;
    }

    public String getEspacioLegible() {
        return util.Formato.bytes(espacioAsignadoBytes);
    }

}
