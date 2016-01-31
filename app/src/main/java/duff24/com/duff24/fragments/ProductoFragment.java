package duff24.com.duff24.fragments;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import duff24.com.duff24.R;
import duff24.com.duff24.adaptadores.AdaptadorProducto;
import duff24.com.duff24.basededatos.AdminSQliteOpenHelper;
import duff24.com.duff24.modelo.Producto;
import duff24.com.duff24.util.AppUtil;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProductoFragment extends FragmentGeneric implements AdapterView.OnItemClickListener
{
    private static final String LIST_STATE = "listState";

    private Parcelable mListState = null;
    private String subcategoriaing;
    private String subcategoriaesp;
    private TextView titulo;
    private ListView listaProductos;
    private List<Producto> data= new ArrayList<>();
    private AdaptadorProducto adapter;
    private String font_path = "font/2-4ef58.ttf";
    private Typeface TF;


    public ProductoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if(savedInstanceState!=null)
        {
            subcategoriaing=savedInstanceState.getString("subcategoriaing");
            subcategoriaesp=savedInstanceState.getString("subcategoriaesp");
            mListState=savedInstanceState.getParcelable(LIST_STATE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View v= inflater.inflate(R.layout.fragment_producto, container, false);
        listaProductos= (ListView) v.findViewById(R.id.lstproductos);
        titulo = (TextView) v.findViewById(R.id.textsubcategoria);

        TF = Typeface.createFromAsset(inflater.getContext().getAssets(), font_path);
        titulo.setText(this.subcategoriaing);
        titulo.setTypeface(TF);
        if(getResources().getString(R.string.idioma).equals("es"))
        {
            titulo.setText(this.subcategoriaesp);
        }

        adapter= new AdaptadorProducto(v.getContext(),data);
        listaProductos.setAdapter(adapter);
        listaProductos.setOnItemClickListener(this);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        if(data.size()>0)
        {
            Log.i("entro:","data size >0");
            adapter.notifyDataSetChanged();
        }
        else
        {
            loadData();
        }
    }

    public void init(String subcategoriaing,String subcategoriaesp)
    {
        this.subcategoriaing=subcategoriaing;
        this.subcategoriaesp=subcategoriaesp;
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        mListState = listaProductos.onSaveInstanceState();
        outState.putString("subcategoriaing", subcategoriaing);
        outState.putString("subcategoriaesp", subcategoriaesp);
        outState.putParcelable(LIST_STATE, mListState);
        super.onSaveInstanceState(outState);
    }

    private void loadData()
    {
        LoadDataTask loadDataTask= new LoadDataTask();
        loadDataTask.execute();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        TextView textconteo= (TextView) view.findViewById(R.id.txtconteo);
        ImageView btnDisminuir= (ImageView) view.findViewById(R.id.btn_disminuir);
        AgregarProductoPedidoTask agregarProductoPedidoTask= new AgregarProductoPedidoTask(textconteo,btnDisminuir);
        agregarProductoPedidoTask.execute(data.get(position));
    }

    public class AgregarProductoPedidoTask extends AsyncTask<Producto,Void,Integer>
    {
        private WeakReference<TextView> txtcontedo;
        private WeakReference<ImageView> btndisminuir;

        public AgregarProductoPedidoTask(TextView conteo,ImageView disminuir)
        {
            txtcontedo= new WeakReference<TextView>(conteo);
            btndisminuir= new WeakReference<ImageView>(disminuir);
        }
        @Override
        protected Integer doInBackground(Producto... params)
        {
            MediaPlayer m = MediaPlayer.create(getContext(),R.raw.sonido_click);
            m.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer mp) {
                    mp.release();
                }
            });
            m.start();
            AdminSQliteOpenHelper admin = new AdminSQliteOpenHelper(getContext(),"admin",null,1);
            SQLiteDatabase db = admin.getWritableDatabase();
            Cursor fila = db.rawQuery("select prodcantidad from pedido where prodid = '" + params[0].getId() + "'", null);
            ContentValues registroPedido= new ContentValues();
            int conteo=1;
            if(fila.moveToFirst())
            {
                conteo=fila.getInt(0)+1;
                registroPedido.put("prodcantidad",conteo);
                db.update("pedido",registroPedido,"prodid = '"+params[0].getId()+"'",null);
            }
            else
            {
                registroPedido.put("prodid",params[0].getId());
                registroPedido.put("prodprecio",params[0].getPrecio());
                registroPedido.put("prodnombreesp",params[0].getNombreesp());
                registroPedido.put("prodnombreing",params[0].getNombreing());
                registroPedido.put("proddescripcioning",params[0].getDescripcionIng());
                registroPedido.put("proddescripcionesp",params[0].getDescripcionesp());
                registroPedido.put("prodcantidad",conteo);
                db.insert("pedido",null,registroPedido);
            }
            db.close();
            return conteo;
        }

        @Override
        protected void onPostExecute(Integer resultado) {
            super.onPostExecute(resultado);
            txtcontedo.get().setText("" + resultado);
            txtcontedo.get().setVisibility(View.VISIBLE);
            btndisminuir.get().setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void actualizarData()
    {
        if(adapter!=null)
        {
            adapter.notifyDataSetChanged();
        }

    }

    public class LoadDataTask extends AsyncTask<Void,Void,Void>
    {
        @Override
        protected Void doInBackground(Void... params)
        {
            for(Producto prod: AppUtil.data)
            {
                if(prod.getSubcategoriaing().equals(subcategoriaing))
                {
                    data.add(prod);
                    adapter.notifyDataSetChanged();
                }
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid)
        {
            super.onPostExecute(aVoid);
            if (mListState != null)
            {
                listaProductos.onRestoreInstanceState(mListState);
            }
            mListState = null;
        }
    }




}
