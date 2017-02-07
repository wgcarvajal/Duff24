package duff24.com.duff24.modelo;


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
    public static String NOMBREING="prodnombre";
    public static String NOMBREESP="prodnombreesp";
    public static String DESCRIPCIONING="proddescripcion";
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
    public static String TBLPRECIO_PREFECHAFIN="prefechafin";
    public static String TBLSUBCATEGORIA_FECHACREACION="createdAt";


    private String objectId;
    private String prodnombreesp;
    private String prodnombre;
    private int precio;
    private String proddescripcion;
    private String proddescripcionesp;
    private String subcategoriaing;
    private String subcategoriaesp;
    private String imgFile;
    private String subcategoria;
    private String imgFileNew;
    private boolean condescripcion;
    private boolean personalizable;


    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getProdnombreesp() {
        return prodnombreesp;
    }

    public void setProdnombreesp(String prodnombreesp) {
        this.prodnombreesp = prodnombreesp;
    }

    public String getProdnombre() {
        return prodnombre;
    }

    public void setProdnombre(String prodnombre) {
        this.prodnombre = prodnombre;
    }

    public int getPrecio() {
        return precio;
    }

    public void setPrecio(int precio) {
        this.precio = precio;
    }

    public String getProddescripcion() {
        return proddescripcion;
    }

    public void setProddescripcion(String proddescripcion) {
        this.proddescripcion = proddescripcion;
    }

    public String getProddescripcionesp() {
        return proddescripcionesp;
    }

    public void setProddescripcionesp(String proddescripcionesp) {
        this.proddescripcionesp = proddescripcionesp;
    }

    public String getSubcategoriaing() {
        return subcategoriaing;
    }

    public void setSubcategoriaing(String subcategoriaing) {
        this.subcategoriaing = subcategoriaing;
    }

    public String getSubcategoriaesp() {
        return subcategoriaesp;
    }

    public void setSubcategoriaesp(String subcategoriaesp) {
        this.subcategoriaesp = subcategoriaesp;
    }

    public String getImgFile() {
        return imgFile;
    }

    public void setImgFile(String imgFile) {
        this.imgFile = imgFile;
    }

    public String getImgFileNew() {
        return imgFileNew;
    }

    public void setImgFileNew(String imgFileNew) {
        this.imgFileNew = imgFileNew;
    }

    public String getSubcategoria() {
        return subcategoria;
    }

    public void setSubcategoria(String subcategoria) {
        this.subcategoria = subcategoria;
    }


    public boolean isCondescripcion() {
        return condescripcion;
    }

    public void setCondescripcion(boolean condescripcion) {
        this.condescripcion = condescripcion;
    }


    public boolean isPersonalizable() {
        return personalizable;
    }

    public void setPersonalizable(boolean personalizable) {
        this.personalizable = personalizable;
    }
}
