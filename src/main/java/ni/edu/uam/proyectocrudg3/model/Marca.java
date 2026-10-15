package ni.edu.uam.proyectocrudg3.model;

public class Marca {

    private int id_marca;
    private String nombre;
    private String pais_origen;
    private int estado;

    public Marca() {

    }

    public Marca(int id_marca, String nombre) {
        this.id_marca = id_marca;
        this.nombre = nombre;
    }

    public Marca(int id_marca, String nombre, String pais_origen, int estado) {
        this.id_marca = id_marca;
        this.nombre = nombre;
        this.pais_origen = pais_origen;
        this.estado = estado;
    }

    public int getId_marca() {
        return id_marca;
    }

    public void setId_marca(int id_marca) {
        this.id_marca = id_marca;
    }

    public int getIdMarca() {
        return id_marca;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPais_origen() {
        return pais_origen;
    }

    public void setPais_origen(String pais_origen) {
        this.pais_origen = pais_origen;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return nombre;
    }
}