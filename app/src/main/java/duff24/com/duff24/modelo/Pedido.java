package duff24.com.duff24.modelo;

import java.util.Date;

/**
 * Created by geovanny on 21/01/16.
 */
public class Pedido
{
    private String objectId;
    private String estado;
    private String pedpersonanombre;
    private String peddireccion;
    private String pedtelefono;
    private String pedformapago;
    private String pedobservaciones;
    private String ciudad;
    private int peddomicilio;
    private Date created;

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getPedpersonanombre() {
        return pedpersonanombre;
    }

    public void setPedpersonanombre(String pedpersonanombre) {
        this.pedpersonanombre = pedpersonanombre;
    }

    public String getPeddireccion() {
        return peddireccion;
    }

    public void setPeddireccion(String peddireccion) {
        this.peddireccion = peddireccion;
    }

    public String getPedtelefono() {
        return pedtelefono;
    }

    public void setPedtelefono(String pedtelefono) {
        this.pedtelefono = pedtelefono;
    }

    public String getPedformapago() {
        return pedformapago;
    }

    public void setPedformapago(String pedformapago) {
        this.pedformapago = pedformapago;
    }

    public String getPedobservaciones() {
        return pedobservaciones;
    }

    public void setPedobservaciones(String pedobservaciones) {
        this.pedobservaciones = pedobservaciones;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getCiudad()
    {
        return ciudad;
    }

    public void setCiudad(String ciudad)
    {
        this.ciudad = ciudad;
    }

    public int getPeddomicilio() {
        return peddomicilio;
    }

    public void setPeddomicilio(int peddomicilio) {
        this.peddomicilio = peddomicilio;
    }
}
