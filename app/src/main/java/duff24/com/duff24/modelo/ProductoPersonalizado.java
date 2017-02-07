package duff24.com.duff24.modelo;

/**
 * Created by aranda on 5/02/17.
 */
public class ProductoPersonalizado
{
    private String prodid;
    private int prodcantidad;
    private int prodprecio;
    private String prodsiningredientes;
    private String prodsiningredientesIng;
    private String nombreIng;
    private String nombreEsp;
    private String descripcionIng;
    private String descripcionEsp;
    private String imgFile;


    public String getProdid() {
        return prodid;
    }

    public void setProdid(String prodid) {
        this.prodid = prodid;
    }

    public int getProdcantidad() {
        return prodcantidad;
    }

    public void setProdcantidad(int prodcantidad) {
        this.prodcantidad = prodcantidad;
    }

    public int getProdprecio() {
        return prodprecio;
    }

    public void setProdprecio(int prodprecio) {
        this.prodprecio = prodprecio;
    }

    public String getProdsiningredientes() {
        return prodsiningredientes;
    }

    public void setProdsiningredientes(String prodsiningredientes) {
        this.prodsiningredientes = prodsiningredientes;
    }


    public String getNombreIng() {
        return nombreIng;
    }

    public void setNombreIng(String nombreIng) {
        this.nombreIng = nombreIng;
    }

    public String getNombreEsp() {
        return nombreEsp;
    }

    public void setNombreEsp(String nombreEsp) {
        this.nombreEsp = nombreEsp;
    }

    public String getDescripcionIng() {
        return descripcionIng;
    }

    public void setDescripcionIng(String descripcionIng) {
        this.descripcionIng = descripcionIng;
    }

    public String getDescripcionEsp() {
        return descripcionEsp;
    }

    public void setDescripcionEsp(String getDescripcionEsp) {
        this.descripcionEsp = getDescripcionEsp;
    }


    public String getImgFile() {
        return imgFile;
    }

    public void setImgFile(String imgFile) {
        this.imgFile = imgFile;
    }


    public String getProdsiningredientesIng() {
        return prodsiningredientesIng;
    }

    public void setProdsiningredientesIng(String prodsiningredientesIng) {
        this.prodsiningredientesIng = prodsiningredientesIng;
    }
}
