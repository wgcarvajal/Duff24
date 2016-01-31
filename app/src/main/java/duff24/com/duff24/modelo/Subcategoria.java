package duff24.com.duff24.modelo;

/**
 * Created by geovanny on 18/01/16.
 */
public class Subcategoria
{
    String nombreIngles;
    String nombreEspanol;
    String nombreCategoria;

    public int getPosicion() {
        return posicion;
    }

    public void setPosicion(int posicion) {
        this.posicion = posicion;
    }

    int posicion;

    public void Subcategoria()
    {

    }

    public String getNombreIngles() {
        return nombreIngles;
    }

    public void setNombreIngles(String nombreIngles) {
        this.nombreIngles = nombreIngles;
    }

    public String getNombreEspanol() {
        return nombreEspanol;
    }

    public void setNombreEspanol(String nombreEspanol) {
        this.nombreEspanol = nombreEspanol;
    }

    public String getNombreCategoria() {
        return nombreCategoria;
    }

    public void setNombreCategoria(String nombreCategoria) {
        this.nombreCategoria = nombreCategoria;
    }
}
