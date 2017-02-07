package duff24.com.duff24.adaptadores;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.util.List;

import duff24.com.duff24.R;
import duff24.com.duff24.basededatos.AdminSQliteOpenHelper;
import duff24.com.duff24.modelo.Producto;
import duff24.com.duff24.modelo.ProductoPersonalizado;
import duff24.com.duff24.util.AppUtil;
import duff24.com.duff24.util.FontCache;


/**
 * Created by geovanny on 17/01/16.
 */
public class AdaptadorProductoPedido  extends RecyclerView.Adapter<AdaptadorProductoPedido.ProductoViewHolder> implements View.OnClickListener {

    private Context context;
    private List<ProductoPersonalizado> data;
    //private LayoutInflater mInflater;
    private String sunshine="font/sunshine.ttf";
    private OnItemClickListener onItemClickListener;
    private String font_path = "font/KGTenThousandReasonsAlt.ttf";
    private String font_path_ASimple="font/VTKS_ANIMAL_2.ttf";

    @Override
    public void onClick(View view)
    {
        ProductoPersonalizado productoPersonalizado = data.get(Integer.parseInt(view.getTag().toString()));
        TextView txtconteo = (TextView) view.getTag(R.id.txtconteo);
        switch (view.getId())
        {
            case R.id.ly_producto:
                TextView btnDisminur = (TextView) view.getTag(R.id.btn_disminuir);
                aumentarProducto(productoPersonalizado,btnDisminur,txtconteo);
                break;

            case R.id.btn_disminuir:
                disminuirProducto(productoPersonalizado,(TextView) view,txtconteo);
                break;
        }

    }


    public interface OnItemClickListener
    {
        void aumentarProducto(ProductoPersonalizado productoPersonalizado,TextView btnDisminuir, TextView txtConteo);
        void disminurProducto(ProductoPersonalizado productoPersonalizado,TextView btnDisminuir, TextView txtConteo);

    }

    public AdaptadorProductoPedido(Context context, List<ProductoPersonalizado> data , OnItemClickListener onItemClickListener)
    {
        this.context = context;
        this.data = data;
        this.onItemClickListener = onItemClickListener;
    }


    @Override
    public ProductoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.template_producto_pedido,parent,false);
        ProductoViewHolder productoViewHolder = new ProductoViewHolder(v);
        productoViewHolder.fijarTiposLetras(font_path,font_path_ASimple,context);
        v.setTag(productoViewHolder);
        return productoViewHolder;
    }

    @Override
    public void onBindViewHolder(ProductoViewHolder holder, int position)
    {

        ProductoPersonalizado p = data.get(position);

        holder.sinIngredientes.setVisibility(View.VISIBLE);

        holder.btnDisminuir.setOnClickListener(this);
        holder.linearLayout.setOnClickListener(this);
        holder.linearLayout.setTag(position);
        holder.btnDisminuir.setTag(position);


        String idioma = context.getResources().getString(R.string.idioma);
        if(idioma.equals("es"))
        {
            holder.nombreproducto.setText(p.getNombreEsp());
        }
        else
        {
            holder.nombreproducto.setText(p.getNombreIng());
        }

        holder.conteo.setText(p.getProdcantidad()+"");

        if(p.getProdsiningredientes().equals(""))
        {
            if(p.getDescripcionEsp().length()< 20)
            {
                if(idioma.equals("es"))
                {
                    holder.sinIngredientes.setText(p.getDescripcionEsp());
                }
                else
                {
                    holder.sinIngredientes.setText(p.getDescripcionIng());
                }

            }
            else
            {
                holder.sinIngredientes.setVisibility(View.GONE);
            }
        }
        else
        {
            if(idioma.equals("es"))
            {
                holder.sinIngredientes.setText("Sin: "+p.getProdsiningredientes());
            }
            else
            {
                holder.sinIngredientes.setText("without: "+p.getProdsiningredientesIng());
            }

        }

        DecimalFormat format= new DecimalFormat("###,###.##");
        String pre=format.format(p.getProdprecio());
        pre=pre.replace(",",".");
        holder.precio.setText("$"+pre);
        holder.conteo.setText(p.getProdcantidad() +"");

        holder.placeHolder.setVisibility(View.VISIBLE);
        holder.placeHolder.setImageResource(R.drawable.foodgif);

        final ProductoViewHolder viewHolder = holder;

        Picasso.with(context)
                .load(p.getImgFile())
                .into(holder.imgFile, new Callback() {
                    @Override
                    public void onSuccess() {
                        viewHolder.placeHolder.setImageDrawable(null);
                        viewHolder.placeHolder.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {

                    }
                });



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
        TextView nombreproducto;
        TextView sinIngredientes;
        TextView precio;
        TextView btnDisminuir;
        TextView conteo;
        ImageView imgFile;
        ImageView placeHolder;
        LinearLayout linearLayout;

        public ProductoViewHolder(View itemView) {
            super(itemView);

            nombreproducto = (TextView) itemView.findViewById(R.id.txtnombreproducto);
            sinIngredientes =(TextView) itemView.findViewById(R.id.txt_sin_ingredientes);;
            precio = (TextView) itemView.findViewById(R.id.txtprecioproducto);;
            btnDisminuir= (TextView) itemView.findViewById(R.id.btn_disminuir);;
            conteo = (TextView) itemView.findViewById(R.id.txtconteo);;
            imgFile = (ImageView)itemView.findViewById(R.id.img_producto);
            placeHolder =(ImageView) itemView.findViewById(R.id.placeholder);
            linearLayout =(LinearLayout) itemView.findViewById(R.id.ly_producto);
        }

        public void fijarTiposLetras(String font_path,String font_path_ASimple,Context context)
        {

            Typeface TF =FontCache.get(font_path,context);
            sinIngredientes.setTypeface(TF);

            TF =FontCache.get(font_path_ASimple,context);
            nombreproducto.setTypeface(TF);
            precio.setTypeface(TF);
            btnDisminuir.setTag(R.id.txtconteo, conteo);
            linearLayout.setTag(R.id.txtconteo,conteo);
            linearLayout.setTag(R.id.btn_disminuir,btnDisminuir);
        }

    }

    public void aumentarProducto(ProductoPersonalizado productoPersonalizado,TextView btnDisminuir, TextView txtConteo)
    {
        onItemClickListener.aumentarProducto(productoPersonalizado,btnDisminuir,txtConteo);
    }

    public void disminuirProducto(ProductoPersonalizado productoPersonalizado,TextView btnDisminuir, TextView txtConteo)
    {
        onItemClickListener.disminurProducto(productoPersonalizado,btnDisminuir,txtConteo);
    }
}
