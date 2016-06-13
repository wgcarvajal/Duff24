package duff24.com.duff24.fragments;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import duff24.com.duff24.R;
import duff24.com.duff24.adaptadores.AdaptadorProducto;
import duff24.com.duff24.basededatos.AdminSQliteOpenHelper;
import duff24.com.duff24.modelo.Producto;
import duff24.com.duff24.util.AppUtil;
import duff24.com.duff24.util.FontCache;
import pl.droidsonroids.gif.GifImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProductoFragment extends FragmentGeneric implements AdapterView.OnItemClickListener, View.OnClickListener {
    private static final String LIST_STATE = "listState";

    private Parcelable mListState = null;
    private String subcategoriaing;
    private String subcategoriaesp;
    private TextView titulo;
    private ImageView btnMenuPrincipal;
    private GifImageView btnMipedido;
    private ListView listaProductos;
    private List<Producto> data= new ArrayList<>();
    private AdaptadorProducto adapter;
    private String font_path = "font/2-4ef58.ttf";


    public ProductoFragment() {
        // Required empty public constructor
    }

    public interface OnComunicationFragment
    {
        void onIrAlPedidoFragment();
        void onAbrirMenuPrincipalFragment();
    }
    OnComunicationFragment onComunicationFragment;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onComunicationFragment=(OnComunicationFragment)context;
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

        Typeface TF = FontCache.get(font_path,inflater.getContext());
        titulo.setText(this.subcategoriaing);
        titulo.setTypeface(TF);
        if(getResources().getString(R.string.idioma).equals("es"))
        {
            titulo.setText(this.subcategoriaesp);
        }
        btnMenuPrincipal=(ImageView)v.findViewById(R.id.btn_menu_principal);
        btnMipedido=(GifImageView)v.findViewById(R.id.btn_mi_pedido);

        btnMenuPrincipal.setOnClickListener(this);
        btnMipedido.setOnClickListener(this);
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
            for(Producto prod: AppUtil.data)
            {
                if(prod.getSubcategoriaing().equals(subcategoriaing))
                {
                    data.add(prod);

                }
            }


            if (mListState != null)
            {
                listaProductos.onRestoreInstanceState(mListState);
            }
            adapter.notifyDataSetChanged();
            mListState = null;

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        TextView textconteo= (TextView) view.findViewById(R.id.txtconteo);
        TextView btnDisminuir= (TextView) view.findViewById(R.id.btn_disminuir);
        AgregarProductoPedidoTask agregarProductoPedidoTask= new AgregarProductoPedidoTask(textconteo,btnDisminuir);
        agregarProductoPedidoTask.execute(data.get(position));
    }

    public class AgregarProductoPedidoTask extends AsyncTask<Producto,Void,Integer>
    {
        private WeakReference<TextView> txtcontedo;
        private WeakReference<TextView> btndisminuir;

        public AgregarProductoPedidoTask(TextView conteo,TextView disminuir)
        {
            txtcontedo= new WeakReference<TextView>(conteo);
            btndisminuir= new WeakReference<TextView>(disminuir);
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
            AdminSQliteOpenHelper admin = AdminSQliteOpenHelper.crearSQLite(getContext());
            SQLiteDatabase db = admin.getWritableDatabase();
            Cursor fila = db.rawQuery("select prodcantidad from pedido where prodid = '" + params[0].getObjectId() + "'", null);
            ContentValues registroPedido= new ContentValues();
            int conteo=1;
            if(fila.moveToFirst())
            {
                conteo=fila.getInt(0)+1;
                registroPedido.put("prodcantidad",conteo);
                db.update("pedido",registroPedido,"prodid = '"+params[0].getObjectId()+"'",null);
            }
            else
            {
                registroPedido.put("prodid",params[0].getObjectId());
                registroPedido.put("prodprecio",params[0].getPrecio());
                registroPedido.put("prodnombreesp",params[0].getProdnombreesp());
                registroPedido.put("prodnombreing",params[0].getProdnombre());
                registroPedido.put("proddescripcioning",params[0].getProddescripcion());
                registroPedido.put("proddescripcionesp",params[0].getProddescripcionesp());
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

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {

            case R.id.btn_menu_principal:
                onComunicationFragment.onAbrirMenuPrincipalFragment();
            break;
            case R.id.btn_mi_pedido:
                onComunicationFragment.onIrAlPedidoFragment();
            break;

        }
    }


}
