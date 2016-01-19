package duff24.com.duff24.adaptadores;

import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.util.Log;
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
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;

import duff24.com.duff24.R;
import duff24.com.duff24.basededatos.AdminSQliteOpenHelper;
import duff24.com.duff24.modelo.Producto;

/**
 * Created by geovanny on 8/01/16.
 */
public class AdaptadorProducto extends BaseAdapter implements View.OnClickListener
{
    private Context context;
    private List<Producto> data;
    private String font_path = "font/2-4ef58.ttf";
    private String font_pathOds="font/odstemplik.otf";
    private Typeface TF;

    public AdaptadorProducto(Context context, List<Producto> data)
    {
        this.context = context;
        this.data = data;
    }
    @Override
    public int getCount()
    {
        return data.size();
    }

    @Override
    public Object getItem(int position)
    {
        return data.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View v = null;
        boolean entro=false;
        final Producto p = (Producto) getItem(position);

        if(convertView == null)
        {
            v = View.inflate(context,R.layout.template_producto ,null);
            entro=true;
        }else
        {
            v = convertView;
        }

        TextView txtnombreProducto = (TextView) v.findViewById(R.id.txtnombreproducto);
        ImageView imagenProducto=(ImageView) v.findViewById(R.id.img_producto);
        TextView txtconteo= (TextView) v.findViewById(R.id.txtconteo);
        ImageView btnDisminuir= (ImageView) v.findViewById(R.id.btn_disminuir);
        TextView txtPrecioProducto = (TextView) v.findViewById(R.id.txtprecioproducto);
        TextView txtDescripcionProducto = (TextView) v.findViewById(R.id.txtdescripcionproducto);

        if(entro)
        {
            TF = Typeface.createFromAsset(context.getAssets(), font_path);
            txtnombreProducto.setTypeface(TF);
            TF = Typeface.createFromAsset(context.getAssets(),font_pathOds);
            txtconteo.setTypeface(TF);
        }

        txtconteo.setText("0");
        txtnombreProducto.setText(p.getNombreing());
        txtDescripcionProducto.setText(p.getDescripcionIng());
        txtPrecioProducto.setText("$" + p.getPrecio());

        if(context.getResources().getString(R.string.idioma).equals("es"))
        {
            txtnombreProducto.setText(p.getNombreesp());
            txtDescripcionProducto.setText(p.getDescripcionesp());
        }

        txtconteo.setVisibility(View.GONE);
        btnDisminuir.setVisibility(View.GONE);

        btnDisminuir.setTag(position);
        btnDisminuir.setOnClickListener(this);

        final View fv=v;

        if(p.getImagen()!=null)
        {
            imagenProducto.setImageBitmap(p.getImagen());
        }
        else
        {
            ParseQuery<ParseObject> queryImagen = new ParseQuery<ParseObject>(Producto.TABLAIMAGEN);
            queryImagen.whereEqualTo(Producto.TBLIMAGEN_PRODUCTO, p.getId());
            queryImagen.getFirstInBackground(new GetCallback<ParseObject>()
            {
                @Override
                public void done(ParseObject object, ParseException e)
                {
                    if (e == null)
                    {
                        ParseFile fileObject = (ParseFile) object.get(Producto.TBLIMAGEN_IMGFILE);
                        fileObject.getDataInBackground(new GetDataCallback() {
                            @Override
                            public void done(byte[] data, ParseException e) {
                            if (e == null)
                            {
                                ImageView imagen = (ImageView) fv.findViewById(R.id.img_producto);
                                Bitmap imagenmodificada = decodeSampledBitmapFromResource(data, 0, data.length, 100, 100);
                                p.setImagen(imagenmodificada);
                                imagen.setImageBitmap(imagenmodificada);
                            }
                            }
                        });
                    }
                }
            });
        }

        AdminSQliteOpenHelper admin = new AdminSQliteOpenHelper(context,"admin",null,1);
        SQLiteDatabase db = admin.getReadableDatabase();

        Cursor fila = db.rawQuery("select prodcantidad from pedido where prodid = '"+p.getId()+"'",null);

        if(fila.moveToFirst())
        {
            txtconteo.setVisibility(View.VISIBLE);
            btnDisminuir.setVisibility(View.VISIBLE);
            txtconteo.setText(fila.getInt(0)+"");
        }
        db.close();

        return v;
    }

    @Override
    public void onClick(View v)
    {
        MediaPlayer m = MediaPlayer.create(context,R.raw.sonido_click);
        m.start();

        AdminSQliteOpenHelper admin = new AdminSQliteOpenHelper(context,"admin",null,1);
        SQLiteDatabase db = admin.getWritableDatabase();

        String prodid = data.get(Integer.parseInt(v.getTag().toString())).getId();

        Cursor fila = db.rawQuery("select prodcantidad from pedido where prodid = '"+prodid+"'",null);

        if(fila.moveToFirst())
        {
            int contador=fila.getInt(0);
            if(contador==1)
            {
                db.delete("pedido","prodid ='"+prodid+"'",null);
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




    //--------------------------------------------------------------------

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

        public BitmapWorkerTask(ImageView imageView,byte [] data,String prodid) {
            // Use a WeakReference to ensure the ImageView can be garbage collected
            imageViewReference = new WeakReference<ImageView>(imageView);
            this.data=data;
            this.prodid=prodid;
        }

        // Decode image in background.
        @Override
        protected Bitmap doInBackground(Integer... params) {
            Bitmap bitmap= decodeSampledBitmapFromResource(data, 0,data.length,10,10);
            return bitmap;
        }

        // Once complete, see if ImageView is still around and set bitmap.
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (imageViewReference != null && bitmap != null) {
                final ImageView imageView = imageViewReference.get();
                if (imageView != null) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        }

    }

    public void loadBitmap(byte[] data , ImageView imageView,String prodid)
    {
            BitmapWorkerTask task = new BitmapWorkerTask(imageView,data,prodid);
            task.execute();
    }


    private String guardarImagen (Context context, String nombre, Bitmap imagen,String extension){
        ContextWrapper cw = new ContextWrapper(context);
        File dirImages = cw.getDir("Imagenes", Context.MODE_PRIVATE);
        File myPath = new File(dirImages, nombre +"."+ extension);


        FileOutputStream fos = null;
        try{
            fos = new FileOutputStream(myPath);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            if(extension.equals("jpg"))
            {
                imagen.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            }
            else
            {
                imagen.compress(Bitmap.CompressFormat.PNG, 100, stream);
            }

            byte [] bytearray= stream.toByteArray();
            fos.write(bytearray);
            fos.close();
        }catch (FileNotFoundException ex){
            ex.printStackTrace();
        }catch (IOException ex){
            ex.printStackTrace();
        }
        return myPath.getAbsolutePath();
    }












}
