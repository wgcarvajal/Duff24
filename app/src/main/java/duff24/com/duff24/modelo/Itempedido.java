package duff24.com.duff24.modelo;

/**
 * Created by geovanny on 23/03/16.
 */
public class Itempedido
{
    private String objectId;
    private String pedido;
    private int itemcantidad;
    private String producto;

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getPedido() {
        return pedido;
    }

    public void setPedido(String pedido) {
        this.pedido = pedido;
    }

    public int getItemcantidad() {
        return itemcantidad;
    }

    public void setItemcantidad(int itemcantidad) {
        this.itemcantidad = itemcantidad;
    }

    public String getProducto() {
        return producto;
    }

    public void setProducto(String producto) {
        this.producto = producto;
    }
}
