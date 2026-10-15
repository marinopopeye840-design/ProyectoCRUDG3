package ni.edu.uam.proyectocrudg3.model;

public class Categoria {
    private int id_categoria;
    private String nombre;
    private String descripcion;
    private int estado;

    public Categoria() {

    }

    public Categoria(int id_categoria, int estado, String descripcion, String nombre) {
        this.id_categoria = id_categoria;
        this.estado = estado;
        this.descripcion = descripcion;
        this.nombre = nombre;
    }

    public int getId_categoria() {
        return id_categoria;
    }

    public void setId_categoria(int id_categoria) {
        this.id_categoria = id_categoria;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }
}
