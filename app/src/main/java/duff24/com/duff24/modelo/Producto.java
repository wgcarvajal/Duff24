package duff24.com.duff24.modelo;

import android.graphics.Bitmap;

/**
 * Created by geovanny on 8/01/16.
 */
public class Producto
{
    public static String TABLA="Producto";
    public static String TABLAIMAGEN="Imagen";
    public static String TABLAPRECIO="Precio";
    public static String TABLACATEGORIA="Categoria";
    public static String TABLASUBCATEGORIA="Subcategoria";
    public static String NOMBRE="prodnombre";
    public static String NOMBREESP="prodnombreesp";
    public static String DESCRIPCION="proddescripcion";
    public static String DESCRIPCIONESP="proddescripcionesp";
    public static String CATEGORIANOMBRE="catnombre";
    public static String TBLSUBCATEGORIA_CATEGORIA="categoria";
    public static String TBLSUBCATEGORIA_NOMBRE="subcatnombre";
    public static String TBLSUBCATEGORIA_NOMBREESP="subcatnombresp";
    public static String SUBCATEGORIA="subcategoria";
    public static String TBLPRECIO_PRODUCTO="producto";
    public static String TBLPRECIO_FECHACREACION="createdAt";
    public static String TBLPRECIO_VALOR="prevalor";
    public static String ID="objectId";
    public static String TBLIMAGEN_PRODUCTO="producto";
    public static String TBLIMAGEN_IMGFILE="imgFile";


    private String id;
    private String nombre;
    private int precio;
    private String descripcion;
    private String categoria;
    private String subcategoria;
    private Bitmap imagen;



    private String urlImagen;


    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getNombre()
    {
        return nombre;
    }

    public void setNombre(String nombre)
    {
        this.nombre = nombre;
    }

    public int getPrecio() {
        return precio;
    }

    public void setPrecio(int precio) {
        this.precio = precio;
    }

    public String getDescripcion()
    {
        return descripcion;
    }

    public void setDescripcion(String descripcion)
    {
        this.descripcion = descripcion;
    }

    public String getCategoria()
    {
        return categoria;
    }

    public void setCategoria(String categoria)
    {
        this.categoria = categoria;
    }

    public String getSubcategoria()
    {
        return subcategoria;
    }

    public void setSubcategoria(String subcategoria)
    {
        this.subcategoria = subcategoria;
    }

    public Bitmap getImagen() {
        return imagen;
    }

    public void setImagen(Bitmap imagen)
    {
        this.imagen = imagen;
    }

    public String getUrlImagen() {
        return urlImagen;
    }

    public void setUrlImagen(String urlImagen) {
        this.urlImagen = urlImagen;
    }



}
