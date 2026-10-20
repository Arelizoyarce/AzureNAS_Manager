package modelo;

/** Tabla area: areas de la organizacion que agrupan usuarios y son propietarias de recursos. */
public class Area {

    private Integer id;
    private String nombre;

    public Area() {
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

}
