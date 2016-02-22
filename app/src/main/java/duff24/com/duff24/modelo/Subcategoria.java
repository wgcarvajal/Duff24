package duff24.com.duff24.modelo;

/**
 * Created by geovanny on 18/01/16.
 */
public class Subcategoria
{
    public static final int CONDESCRIPCION=1;
    public static final int SINDESCRIPCION=2;
    public static final int ANUNCIO=3;

    private String nombreIngles;
    private String nombreEspanol;
    private String nombreCategoria;
    private String id;
    private int tipoFragment;

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

    public int getTipoFragment()
    {
        return tipoFragment;
    }

    public void setTipoFragment(int tipoFragment)
    {
        this.tipoFragment=tipoFragment;
    }

    public String getID()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id=id;
    }
}
