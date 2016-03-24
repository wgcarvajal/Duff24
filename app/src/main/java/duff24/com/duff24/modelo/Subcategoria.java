package duff24.com.duff24.modelo;

/**
 * Created by geovanny on 18/01/16.
 */
public class Subcategoria
{
    public static final int CONDESCRIPCION=1;
    public static final int SINDESCRIPCION=2;
    public static final int ANUNCIO=3;

    private String subcatnombre;
    private String subcatnombresp;
    private String objectId;
    private int tipoFragment;

    public String getSubcatnombre()
    {
        return subcatnombre;
    }

    public void setSubcatnombre(String subcatnombre)
    {
        this.subcatnombre = subcatnombre;
    }

    public String getSubcatnombresp()
    {
        return subcatnombresp;
    }

    public void setSubcatnombresp(String subcatnombresp)
    {
        this.subcatnombresp = subcatnombresp;
    }

    public String getObjectId()
    {
        return objectId;
    }

    public void setObjectId(String objectId)
    {
        this.objectId = objectId;
    }

    public int getTipoFragment() {
        return tipoFragment;
    }

    public void setTipoFragment(int tipoFragment)
    {
        this.tipoFragment = tipoFragment;
    }
}
