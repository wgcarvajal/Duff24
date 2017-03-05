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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
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
import duff24.com.duff24.util.FontCache;

/**
 * Created by geovanny on 8/01/16.
 */


public class AdaptadorProducto extends RecyclerView.Adapter<AdaptadorProducto.ProductoViewHolder> implements View.OnClickListener {
    private Context context;
    private List<Producto> data;
    //private LayoutInflater mInflater;
    private String sunshine="font/sunshine.ttf";
    private OnItemClickListener onItemClickListener;



    public interface OnItemClickListener
    {

        void itemClick(Producto producto,TextView btnDisminuir , TextView txtconteo);
        void itemClickDisminuir(Producto producto,TextView btnDisminuir , TextView txtconteo);

    }

    public AdaptadorProducto(Context context, List<Producto> data , OnItemClickListener onItemClickListener)
    {
        this.context = context;
        this.data = data;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public ProductoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.template_producto,parent,false);
        ProductoViewHolder productoViewHolder = new ProductoViewHolder(v);
        productoViewHolder.fijarTiposLetras(sunshine,context);
        v.setTag(productoViewHolder);
        return productoViewHolder;
    }

    @Override
    public void onBindViewHolder(ProductoViewHolder holder, int position)
    {
        Producto p = data.get(position);
        holder.txtconteo.setText("0");
        fijarDatos(p,holder,position);

        holder.placeholder.setVisibility(View.VISIBLE);
        holder.placeholder.setImageResource(R.drawable.foodgif);
        final ProductoViewHolder viewHolder = holder;

        String imgFile;

        if(p.getImgFileNew() == null)
        {
            imgFile = p.getImgFile();
        }
        else
        {
            imgFile = p.getImgFileNew();
        }

        final float tam = context.getResources().getDimension(R.dimen.alto_template_producto);

        int height = (int)tam;

        int width = (int)(tam * 2 / 3);

        Picasso.with(context)
                .load(imgFile)
                .resize(width,height)
                .into(viewHolder.imagenProducto, new Callback() {

                    @Override
                    public void onSuccess() {

                        viewHolder.placeholder.setImageDrawable(null);
                        viewHolder.placeholder.setVisibility(View.GONE);
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


        ImageView imagenProducto;
        ImageView placeholder;
        TextView txtconteo;
        TextView btnDisminuir;

        public ProductoViewHolder(View itemView) {
            super(itemView);
            imagenProducto=(ImageView) itemView.findViewById(R.id.img_producto);
            placeholder=(ImageView) itemView.findViewById(R.id.placeholder);
            txtconteo=(TextView) itemView.findViewById(R.id.txtconteo);
            btnDisminuir=(TextView) itemView.findViewById(R.id.btn_disminuir);

        }
        public void  fijarTiposLetras(String tipo, Context context)
        {
            Typeface TF = FontCache.get(tipo,context);
            txtconteo.setTypeface(TF);
            txtconteo.setText("0");
            btnDisminuir.setTag(R.id.txtconteo, txtconteo);
            imagenProducto.setTag(R.id.txtconteo,txtconteo);
            imagenProducto.setTag(R.id.btn_disminuir,btnDisminuir);
        }

    }


    private void fijarDatos(Producto producto,ProductoViewHolder viewHolder,int position)
    {

        viewHolder.txtconteo.setVisibility(View.GONE);
        viewHolder.btnDisminuir.setVisibility(View.GONE);
        viewHolder.btnDisminuir.setTag(position);
        viewHolder.imagenProducto.setTag(position);
        viewHolder.btnDisminuir.setOnClickListener(this);
        viewHolder.imagenProducto.setOnClickListener(this);
        FijarCantidadTask fijarCantidadTask=new FijarCantidadTask(context,viewHolder);
        fijarCantidadTask.execute(producto.getObjectId());

    }

    public class FijarCantidadTask extends AsyncTask<String,Void,Void>
    {
        ProductoViewHolder viewHolder;
        Context context;
        int cantidad=0;

        public FijarCantidadTask(Context context, ProductoViewHolder viewHolder)
        {
            this.viewHolder=viewHolder;
            this.context=context;
        }

        @Override
        protected Void doInBackground(String... params)
        {
            AdminSQliteOpenHelper admin = new AdminSQliteOpenHelper(context,"admin",null,AdminSQliteOpenHelper.v);

            SQLiteDatabase db = admin.getReadableDatabase();
            Cursor fila = db.rawQuery("select prodcantidad from pedido where prodid = '"+params[0]+"'",null);
            if(fila.moveToFirst())
            {
                cantidad=fila.getInt(0);

                while (fila.moveToNext())
                {
                    cantidad = cantidad + fila.getInt(0);
                }
            }
            db.close();
            return  null;
        }

        @Override
        protected void onPostExecute(Void avoid) {
            super.onPostExecute(avoid);
            if(cantidad!=0)
            {
                viewHolder.txtconteo.setVisibility(View.VISIBLE);
                viewHolder.btnDisminuir.setVisibility(View.VISIBLE);
                viewHolder.txtconteo.setText(cantidad + "");
            }
        }
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {

            case R.id.btn_disminuir:


                TextView txtconteo=(TextView)v.getTag(R.id.txtconteo);
                Producto p = data.get(Integer.parseInt(v.getTag().toString()));
                onItemClickListener.itemClickDisminuir(p,(TextView)v,txtconteo);
                break;

            case R.id.img_producto:
                Log.i("entro ", "entroooooooooooooooooo");
                int posicion = Integer.parseInt(v.getTag().toString());
                TextView disminuir = (TextView) v.getTag(R.id.btn_disminuir);
                TextView conteo = (TextView) v.getTag(R.id.txtconteo);
                onItemClickListener.itemClick(data.get(posicion),disminuir, conteo);
                break;
        }

    }


}



