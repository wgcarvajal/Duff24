package duff24.com.duff24.adaptadores;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import duff24.com.duff24.R;
import duff24.com.duff24.modelo.Producto;
import duff24.com.duff24.modelo.ProductoPersonalizado;
import duff24.com.duff24.util.FontCache;

/**
 * Created by aranda on 5/02/17.
 */
public class AdaptadorProductoDisminuir extends RecyclerView.Adapter<AdaptadorProductoDisminuir.ProductoViewHolder> implements View.OnClickListener {
    private Context context;
    private List<ProductoPersonalizado> data;
    private String sunshine="font/sunshine.ttf";
    private OnItemClickListener onItemClickListener;
    private String font_path = "font/KGTenThousandReasonsAlt.ttf";
    private String font_path_ASimple="font/VTKS_ANIMAL_2.ttf";

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.btn_disminuir:
                int posicion = Integer.parseInt(view.getTag().toString());
                ProductoPersonalizado p = data.get(posicion);
                TextView conteo =(TextView) view.getTag(R.id.txtconteo);
                disminuirProducto(p,conteo);
                break;
        }
    }

    public interface OnItemClickListener
    {

        void itemClickDisminuirDialogDisminuir(String siningredientes);

    }

    public AdaptadorProductoDisminuir(Context context, List<ProductoPersonalizado> data , OnItemClickListener onItemClickListener)
    {
        this.context = context;
        this.data = data;
        this.onItemClickListener = onItemClickListener;

    }

    @Override
    public ProductoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.template_producto_personalizado_dialog_disminuir,parent,false);
        ProductoViewHolder productoViewHolder = new ProductoViewHolder(v);

        Typeface TF = FontCache.get(font_path_ASimple,context);
        productoViewHolder.productoPersonalizado.setTypeface(TF);
        TF = FontCache.get(font_path,context);
        productoViewHolder.sinIngredientes.setTypeface(TF);


        v.setTag(productoViewHolder);
        return productoViewHolder;
    }

    @Override
    public void onBindViewHolder(ProductoViewHolder holder, int position)
    {
        ProductoPersonalizado p = data.get(position);

        holder.conteo.setText(p.getProdcantidad()+"");
        holder.btnDisminuir.setOnClickListener(this);

        holder.btnDisminuir.setTag(R.id.txtconteo, holder.conteo);
        holder.btnDisminuir.setTag(position);

        String idioma = context.getResources().getString(R.string.idioma);

        if(p.getProdsiningredientes().equals(""))
        {
            if(idioma.equals("es"))
            {
                holder.productoPersonalizado.setText("Hamburguesa");
                holder.sinIngredientes.setText("Completa");
            }
            else
            {
                holder.productoPersonalizado.setText("Burger");
                holder.sinIngredientes.setText("Full");
            }

        }
        else
        {
            if(idioma.equals("es"))
            {
                holder.productoPersonalizado.setText("Hamburguesa sin:");
                holder.sinIngredientes.setText(p.getProdsiningredientes());
            }
            else
            {
                holder.productoPersonalizado.setText("Burger without:");
                holder.sinIngredientes.setText(p.getProdsiningredientesIng());
            }

        }

    }

    @Override
    public int getItemCount() {
        if(data!=null)
        {
            return data.size();
        }
        return 0;
    }

    public static class ProductoViewHolder extends RecyclerView.ViewHolder
    {
        TextView productoPersonalizado;
        TextView btnDisminuir;
        TextView conteo;
        TextView sinIngredientes;

        public ProductoViewHolder(View itemView) {
            super(itemView);
            productoPersonalizado =(TextView) itemView.findViewById(R.id.txt_producto_personalizado_dialgo_disminuir);
            btnDisminuir =(TextView) itemView.findViewById(R.id.btn_disminuir);
            conteo =(TextView) itemView.findViewById(R.id.txtconteo);
            sinIngredientes = (TextView)itemView.findViewById(R.id.txt_sin_ingredientes_personalizado_dialgo_disminuir);
        }

    }

    public void disminuirProducto(ProductoPersonalizado productoPersonalizado,TextView txtconteo)
    {
        int cantidad = productoPersonalizado.getProdcantidad();

        cantidad = cantidad -1;

        if(cantidad> 0)
        {
            productoPersonalizado.setProdcantidad(cantidad);
            txtconteo.setText(cantidad+"");
        }
        else
        {
            data.remove(productoPersonalizado);
            notifyDataSetChanged();
        }

        onItemClickListener.itemClickDisminuirDialogDisminuir(productoPersonalizado.getProdsiningredientes());


    }

}
