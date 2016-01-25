package duff24.com.duff24.adaptadores;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.media.MediaPlayer;
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

import java.text.DecimalFormat;
import java.util.List;

import duff24.com.duff24.R;
import duff24.com.duff24.basededatos.AdminSQliteOpenHelper;
import duff24.com.duff24.modelo.Producto;

/**
 * Created by geovanny on 17/01/16.
 */
public class AdaptadorProductoPedido extends BaseAdapter implements View.OnClickListener {
    private Context context;
    private List<Producto> data;
    private String font_path = "font/2-4ef58.ttf";
    private String font_pathOds="font/odstemplik.otf";
    private Typeface TF;


    public interface OnDisminuirTotal
    {
        void onDisminuirTotal(int precio);
    }

    OnDisminuirTotal onDisminuirTotal;

    public AdaptadorProductoPedido(Context context, List<Producto> data)
    {
        this.context = context;
        this.data = data;
        onDisminuirTotal = (OnDisminuirTotal) context;
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
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View v = null;
        boolean entro=false;
        final Producto producto= data.get(position);
        if(convertView == null)
        {
            v = View.inflate(context, R.layout.template_producto_pedido, null);
            entro=true;
        }
        else
        {
            v=convertView;
        }
        TextView txtnombreProducto = (TextView) v.findViewById(R.id.txtnombreproducto);
        TextView txtprecioProducto=(TextView) v.findViewById(R.id.txtprecioproducto);
        ImageView btnDisminuir= (ImageView) v.findViewById(R.id.btn_disminuir);
        TextView txtconteo=(TextView)v.findViewById(R.id.txtconteo);
        ImageView imagenProducto =(ImageView)v.findViewById(R.id.img_producto);

        btnDisminuir.setTag(position);
        btnDisminuir.setOnClickListener(this);

        if(entro)
        {
            TF = Typeface.createFromAsset(context.getAssets(), font_path);
            txtnombreProducto.setTypeface(TF);
            TF = Typeface.createFromAsset(context.getAssets(), font_pathOds);
            txtconteo.setTypeface(TF);

        }
        txtnombreProducto.setText(producto.getNombreing());

        DecimalFormat format= new DecimalFormat("###,###.##");
        String valorTotal=format.format(producto.getPrecio());
        valorTotal=valorTotal.replace(",",".");
        txtprecioProducto.setText("$"+valorTotal);


        if(context.getResources().getString(R.string.idioma).equals("es"))
        {
            txtnombreProducto.setText(producto.getNombreesp());
        }


        final View fv=v;

        if(producto.getImagen()!=null)
        {
            imagenProducto.setImageBitmap(producto.getImagen());
        }
        else
        {
            ParseQuery<ParseObject> queryImagen = new ParseQuery<ParseObject>(Producto.TABLAIMAGEN);
            queryImagen.whereEqualTo(Producto.TBLIMAGEN_PRODUCTO, producto.getId());
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
                                    ImageView img=(ImageView) fv.findViewById(R.id.img_producto);
                                    Bitmap imagenmodificada = decodeSampledBitmapFromResource(data, 0, data.length, 80, 80);
                                    producto.setImagen(imagenmodificada);
                                    img.setImageBitmap(imagenmodificada);
                                }
                            }
                        });
                    }

                }
            });

        }

        AdminSQliteOpenHelper admin = new AdminSQliteOpenHelper(context,"admin",null,1);
        SQLiteDatabase db = admin.getReadableDatabase();

        Cursor fila = db.rawQuery("select prodcantidad from pedido where prodid = '"+producto.getId()+"'",null);

        if(fila.moveToFirst())
        {
            txtconteo.setText(fila.getInt(0)+"");
        }
        db.close();
        return v;
    }


//----------------------------------------------------------------------------------------------------

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

}
