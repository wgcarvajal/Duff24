package duff24.com.duff24.adaptadores;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
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
 * Created by geovanny on 17/01/16.
 */
public class AdaptadorProductoPedido extends BaseAdapter implements View.OnClickListener
{
    private Context context;
    private List<Producto> data;
    private String font_path = "font/2-4ef58.ttf";
    private String font_pathOds="font/odstemplik.otf";
    private String font_path_ASimple="font/A_Simple_Life.ttf";
    private LayoutInflater mInflater;


    public interface OnDisminuirTotal
    {
        void onDisminuirTotal(int precio);
    }

    OnDisminuirTotal onDisminuirTotal;

    public class ViewHolder
    {
        public TextView txtnombreProducto;
        public ImageView imagenProducto;
        public ImageView placeholder;
        public TextView txtconteo;
        public TextView btnDisminuir;
        public TextView txtPrecioProducto;
    }

    public AdaptadorProductoPedido(Context context, List<Producto> data)
    {
        mInflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
        this.data = data;
        onDisminuirTotal = (OnDisminuirTotal) context;
    }

    @Override
    public int getCount()
    {
        if(data!=null)
        {
            return data.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position)
    {
        if(data != null && position >= 0 && position < getCount() )
        {
            return data.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View v = convertView;
        final ViewHolder viewHolder;

        if(convertView == null)
        {
            v = mInflater.inflate(R.layout.template_producto_pedido,parent,false);
            viewHolder=new ViewHolder();
            viewHolder.txtnombreProducto=(TextView) v.findViewById(R.id.txtnombreproducto);
            viewHolder.imagenProducto=(ImageView) v.findViewById(R.id.img_producto);
            viewHolder.placeholder=(ImageView)v.findViewById(R.id.placeholder);
            viewHolder.txtconteo=(TextView) v.findViewById(R.id.txtconteo);
            viewHolder.btnDisminuir=(TextView) v.findViewById(R.id.btn_disminuir);
            viewHolder.txtPrecioProducto=(TextView) v.findViewById(R.id.txtprecioproducto);
            Typeface TF = FontCache.get(font_path,context);
            viewHolder.txtnombreProducto.setTypeface(TF);
            TF = FontCache.get(font_pathOds,context);
            viewHolder.txtconteo.setTypeface(TF);
            viewHolder.txtconteo.setText("0");
            TF = FontCache.get(font_path_ASimple,context);
            viewHolder.txtPrecioProducto.setTypeface(TF);
            viewHolder.btnDisminuir.setTag(R.id.txtconteo,viewHolder.txtconteo);
            v.setTag(viewHolder);
        }
        else
        {
            viewHolder=(ViewHolder)v.getTag();
        }

        final Producto p = (Producto) getItem(position);
        fijarDatos(p, viewHolder, context.getResources().getString(R.string.idioma), position);


        viewHolder.placeholder.setVisibility(View.VISIBLE);
        viewHolder.placeholder.setImageResource(R.drawable.carga);
        viewHolder.txtPrecioProducto.setVisibility(View.INVISIBLE);
        viewHolder.txtnombreProducto.setVisibility(View.INVISIBLE);

        Picasso.with(context)
                .load(p.getImgFile())
                .into(viewHolder.imagenProducto, new Callback() {

                    @Override
                    public void onSuccess() {
                        viewHolder.placeholder.setImageDrawable(null);
                        viewHolder.placeholder.setVisibility(View.GONE);
                        viewHolder.txtPrecioProducto.setVisibility(View.VISIBLE);
                        viewHolder.txtnombreProducto.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onError() {

                    }
                });

        return v;
    }

    private void fijarDatos(Producto producto,ViewHolder viewHolder,String idioma,int position)
    {
        DecimalFormat format =new DecimalFormat("###,###.##");
        String valorProducto=format.format(producto.getPrecio());
        valorProducto=valorProducto.replace(",",".");
        viewHolder.txtPrecioProducto.setText("$" + valorProducto);
        if(idioma.equals("es"))
        {
            viewHolder.txtnombreProducto.setText(producto.getProdnombreesp());
        }
        else
        {
            viewHolder.txtnombreProducto.setText(producto.getProdnombre());
        }
        viewHolder.btnDisminuir.setTag(position);
        viewHolder.btnDisminuir.setOnClickListener(this);
        FijarCantidadTask fijarCantidadTask=new FijarCantidadTask(context,viewHolder);
        fijarCantidadTask.execute(producto.getObjectId());
    }

    public class FijarCantidadTask extends AsyncTask<String,Void,Void>
    {
        ViewHolder viewHolder;
        Context context;
        int cantidad=0;

        public FijarCantidadTask(Context context,ViewHolder viewHolder)
        {
            this.viewHolder=viewHolder;
            this.context=context;
        }

        @Override
        protected Void doInBackground(String... params)
        {
            AdminSQliteOpenHelper admin = AdminSQliteOpenHelper.crearSQLite(context);

            SQLiteDatabase db = admin.getReadableDatabase();
            Cursor fila = db.rawQuery("select prodcantidad from pedido where prodid = '"+params[0]+"'",null);
            if(fila.moveToFirst())
            {
                cantidad=fila.getInt(0);
            }
            db.close();
            return  null;
        }

        @Override
        protected void onPostExecute(Void avoid)
        {
            super.onPostExecute(avoid);
            viewHolder.txtconteo.setText(cantidad+"");
        }
    }

    @Override
    public void onClick(View v)
    {
        TextView txtconteo=(TextView)v.getTag(R.id.txtconteo);
        int precio= data.get(Integer.parseInt(v.getTag().toString())).getPrecio();
        String prodid = data.get(Integer.parseInt(v.getTag().toString())).getObjectId();
        DisminuirCantidadTask disminuirCantidadTask= new DisminuirCantidadTask(txtconteo,(TextView)v,context,Integer.parseInt(v.getTag().toString()),precio);
        disminuirCantidadTask.execute(data.get(Integer.parseInt(v.getTag().toString())).getObjectId());
    }



    public class DisminuirCantidadTask extends AsyncTask<String,Void,Void>
    {
        private WeakReference<TextView> textViewWeakReference;
        private WeakReference<TextView> imageViewWeakReference;
        private Context context;
        private int posicion;
        private int cantidad=0;
        private int precio=0;

        public DisminuirCantidadTask(TextView textView,TextView btn,Context context,int posicion,int precio)
        {
            this.textViewWeakReference= new WeakReference<TextView>(textView);
            this.imageViewWeakReference= new WeakReference<TextView>(btn);
            this.posicion=posicion;
            this.context=context;
            this.precio=precio;
        }
        @Override
        protected Void doInBackground(String... params)
        {
            MediaPlayer m = MediaPlayer.create(context, R.raw.sonido_click);
            m.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer mp) {
                    mp.release();
                }
            });
            m.start();

            AdminSQliteOpenHelper admin = AdminSQliteOpenHelper.crearSQLite(context);
            SQLiteDatabase db = admin.getWritableDatabase();
            Cursor fila = db.rawQuery("select prodcantidad from pedido where prodid = '" + params[0] + "'", null);
            if(fila.moveToFirst())
            {
                this.cantidad=fila.getInt(0)-1;
                if(cantidad==0)
                {
                    db.delete("pedido", "prodid ='" + params[0] + "'", null);
                }
                else
                {
                    ContentValues registroPedido= new ContentValues();
                    registroPedido.put("prodcantidad",cantidad);
                    db.update("pedido", registroPedido, "prodid = '" + params[0] + "'", null);
                }
            }
            db.close();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(cantidad==0)
            {
                data.remove(posicion);
                notifyDataSetChanged();
            }
            else
            {
                textViewWeakReference.get().setText(cantidad + "");
            }
            onDisminuirTotal.onDisminuirTotal(precio);
        }
    }





}
