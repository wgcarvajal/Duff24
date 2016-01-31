package duff24.com.duff24.adaptadores;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.util.List;

import duff24.com.duff24.R;
import duff24.com.duff24.basededatos.AdminSQliteOpenHelper;
import duff24.com.duff24.modelo.Producto;

/**
 * Created by geovanny on 17/01/16.
 */
public class AdaptadorProductoPedido extends BaseAdapter implements View.OnClickListener
{
    private Context context;
    private List<Producto> data;
    private String font_path = "font/2-4ef58.ttf";
    private String font_pathOds="font/odstemplik.otf";
    private Typeface TF;
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
        public TextView txtconteo;
        public ImageView btnDisminuir;
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
        ViewHolder viewHolder;

        if(convertView == null)
        {
            v = mInflater.inflate(R.layout.template_producto_pedido,parent,false);
            viewHolder=new ViewHolder();
            viewHolder.txtnombreProducto=(TextView) v.findViewById(R.id.txtnombreproducto);
            viewHolder.imagenProducto=(ImageView) v.findViewById(R.id.img_producto);
            viewHolder.txtconteo=(TextView) v.findViewById(R.id.txtconteo);
            viewHolder.btnDisminuir=(ImageView) v.findViewById(R.id.btn_disminuir);
            viewHolder.txtPrecioProducto=(TextView) v.findViewById(R.id.txtprecioproducto);
            TF = Typeface.createFromAsset(context.getAssets(), font_path);
            viewHolder.txtnombreProducto.setTypeface(TF);
            TF = Typeface.createFromAsset(context.getAssets(),font_pathOds);
            viewHolder.txtconteo.setTypeface(TF);
            viewHolder.txtconteo.setText("0");
            viewHolder.btnDisminuir.setTag(R.id.txtconteo,viewHolder.txtconteo);
            v.setTag(viewHolder);
        }
        else
        {
            viewHolder=(ViewHolder)v.getTag();
        }

        final Producto p = (Producto) getItem(position);
        fijarDatos(p, viewHolder, context.getResources().getString(R.string.idioma), position);
        viewHolder.imagenProducto.setImageBitmap(null);

        final View fv=v;

        if(p.getImagen()!=null)
        {
            loadBitmap(p.getImagen(), viewHolder.imagenProducto, p.getId(), context);
        }
        else
        {
            ParseQuery<ParseObject> queryImagen = new ParseQuery<ParseObject>(Producto.TABLAIMAGEN);
            queryImagen.whereEqualTo(Producto.TBLIMAGEN_PRODUCTO, p.getId());
            queryImagen.getFirstInBackground(new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject object, ParseException e) {
                    if (e == null) {
                        ParseFile fileObject = (ParseFile) object.get(Producto.TBLIMAGEN_IMGFILE);
                        fileObject.getDataInBackground(new GetDataCallback() {
                            @Override
                            public void done(byte[] data, ParseException e)
                            {
                                if(e==null)
                                {
                                    p.setImagen(data);
                                    ImageView imagen = (ImageView) fv.findViewById(R.id.img_producto);
                                    loadBitmap(data, imagen, p.getId(), context);
                                }
                            }
                        });
                    }
                }
            });
        }
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
            viewHolder.txtnombreProducto.setText(producto.getNombreesp());
        }
        else
        {
            viewHolder.txtnombreProducto.setText(producto.getNombreing());
        }
        viewHolder.btnDisminuir.setTag(position);
        viewHolder.btnDisminuir.setOnClickListener(this);
        FijarCantidadTask fijarCantidadTask=new FijarCantidadTask(context,viewHolder);
        fijarCantidadTask.execute(producto.getId());
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
            AdminSQliteOpenHelper admin = new AdminSQliteOpenHelper(context,"admin",null,1);

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
        MediaPlayer m = MediaPlayer.create(context,R.raw.sonido_click);
        m.start();

        AdminSQliteOpenHelper admin = new AdminSQliteOpenHelper(context,"admin",null,1);
        SQLiteDatabase db = admin.getWritableDatabase();

        String prodid = data.get(Integer.parseInt(v.getTag().toString())).getId();

        Cursor fila = db.rawQuery("select prodcantidad from pedido where prodid = '" + prodid + "'", null);
        if(fila.moveToFirst())
        {
            int contador=fila.getInt(0);
            onDisminuirTotal.onDisminuirTotal(data.get(Integer.parseInt(v.getTag().toString())).getPrecio());
            if(contador==1)
            {
                db.delete("pedido","prodid ='"+prodid+"'",null);
                data.remove(Integer.parseInt(v.getTag().toString()));
            }
            else
            {
                ContentValues registroPedido= new ContentValues();
                registroPedido.put("prodcantidad",contador-1);
                int cant= db.update("pedido",registroPedido,"prodid = '"+prodid+"'",null);
            }
            this.notifyDataSetChanged();
        }
        db.close();
    }




    //---------------------------------------------------------------------------

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(byte [] data, int offset,int length,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, offset, length, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeByteArray(data, offset, length, options);
    }

    class BitmapWorkerTask extends AsyncTask<Integer, Void, Bitmap> {
        private final WeakReference<ImageView> imageViewReference;
        private byte [] data = null;
        private String prodid="";

        public BitmapWorkerTask(ImageView imageView,byte [] data,String prodid)
        {
            imageViewReference = new WeakReference<ImageView>(imageView);
            this.data=data;
            this.prodid=prodid;
        }

        @Override
        protected Bitmap doInBackground(Integer... params) {
            Bitmap bitmap= decodeSampledBitmapFromResource(data, 0,data.length,80,80);
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap)
        {
            if (isCancelled())
            {
                bitmap = null;
            }
            if (imageViewReference != null && bitmap != null)
            {
                final ImageView imageView = imageViewReference.get();
                final BitmapWorkerTask bitmapWorkerTask =
                        getBitmapWorkerTask(imageView);
                if (this == bitmapWorkerTask && imageView != null)
                {
                    imageView.setImageBitmap(bitmap);
                }
            }
        }
    }
    public void loadBitmap(byte[] data , ImageView imageView,String prodid,Context context)
    {

        if (cancelPotentialWork(prodid, imageView)) {
            final BitmapWorkerTask task = new BitmapWorkerTask(imageView,data,prodid);
            final AsyncDrawable asyncDrawable =
                    new AsyncDrawable(context.getResources(), null, task);
            imageView.setImageDrawable(asyncDrawable);
            task.execute();
        }
    }

    static class AsyncDrawable extends BitmapDrawable {
        private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;

        public AsyncDrawable(Resources res, Bitmap bitmap,
                             BitmapWorkerTask bitmapWorkerTask) {
            super(res, bitmap);
            bitmapWorkerTaskReference =
                    new WeakReference<BitmapWorkerTask>(bitmapWorkerTask);
        }

        public BitmapWorkerTask getBitmapWorkerTask() {
            return bitmapWorkerTaskReference.get();
        }

    }

    public static boolean cancelPotentialWork(String prodid, ImageView imageView) {
        final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

        if (bitmapWorkerTask != null) {
            final String bitmapProdid = bitmapWorkerTask.prodid;
            // If bitmapData is not yet set or it differs from the new data
            if (bitmapProdid.equals("") || !bitmapProdid.equals(prodid)) {
                // Cancel previous task
                bitmapWorkerTask.cancel(true);
            } else {
                // The same work is already in progress
                return false;
            }
        }
        // No task associated with the ImageView, or an existing task was cancelled
        return true;
    }

    private static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getBitmapWorkerTask();
            }
        }
        return null;
    }

}
