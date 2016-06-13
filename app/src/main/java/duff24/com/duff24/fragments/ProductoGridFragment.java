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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import duff24.com.duff24.R;
import duff24.com.duff24.adaptadores.AdaptadorProductoGrid;
import duff24.com.duff24.basededatos.AdminSQliteOpenHelper;
import duff24.com.duff24.modelo.Producto;
import duff24.com.duff24.util.AppUtil;
import duff24.com.duff24.util.FontCache;
import pl.droidsonroids.gif.GifImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProductoGridFragment extends FragmentGeneric implements AdapterView.OnItemClickListener, View.OnClickListener {
    private static final String LIST_STATE = "listState";

    private Parcelable mListState = null;
    private String subcategoria;
    private String subcategoriaesp;
    private TextView titulo;
    private ImageView btnMenuPrincipal;
    private GifImageView btnMipedido;
    private GridView gridProductos;
    private List<Producto> data = new ArrayList<>();
    private AdaptadorProductoGrid adapter;
    private String font_path = "font/2-4ef58.ttf";

    public ProductoGridFragment()
    {

    }

    public interface OnComunicationFragmentGrid
    {
        void onIrAlPedidoFragmenGrid();
        void onAbrirMenuPrincipalFragmentGrid();
    }

    OnComunicationFragmentGrid onComunicationFragmentGrid;


    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        onComunicationFragmentGrid=(OnComunicationFragmentGrid)context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if(savedInstanceState!=null)
        {
            subcategoria=savedInstanceState.getString("subcategoria");
            subcategoriaesp=savedInstanceState.getString("subcategoriaesp");
            mListState=savedInstanceState.getParcelable(LIST_STATE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_producto_grid, container, false);

        gridProductos= (GridView) v.findViewById(R.id.gridProductos);
        titulo = (TextView) v.findViewById(R.id.textsubcategoria);

        Typeface TF = FontCache.get(font_path,inflater.getContext());
        titulo.setText(this.subcategoria);
        titulo.setTypeface(TF);
        if(getResources().getString(R.string.idioma).equals("es"))
        {
            titulo.setText(this.subcategoriaesp);
        }
        btnMenuPrincipal=(ImageView)v.findViewById(R.id.btn_menu_principal);
        btnMipedido=(GifImageView)v.findViewById(R.id.btn_mi_pedido);

        btnMenuPrincipal.setOnClickListener(this);
        btnMipedido.setOnClickListener(this);
        adapter= new AdaptadorProductoGrid(v.getContext(),data);
        gridProductos.setAdapter(adapter);
        gridProductos.setOnItemClickListener(this);

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(data.size()>0)
        {

            adapter.notifyDataSetChanged();
        }
        else
        {
            loadData();
        }
    }

    public void init(String subcategoria,String subcategoriaesp)
    {
        this.subcategoria=subcategoria;
        this.subcategoriaesp=subcategoriaesp;
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        mListState = gridProductos.onSaveInstanceState();
        outState.putString("subcategoria", subcategoria);
        outState.putString("subcategoriaesp",subcategoriaesp);
        outState.putParcelable(LIST_STATE, mListState);
        super.onSaveInstanceState(outState);
    }

    public void loadData()
    {
        for(Producto prod: AppUtil.data)
        {
            if(prod.getSubcategoriaing().equals(subcategoria))
            {
                data.add(prod);
            }
        }

        if (mListState != null)
        {
            gridProductos.onRestoreInstanceState(mListState);
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
                onComunicationFragmentGrid.onAbrirMenuPrincipalFragmentGrid();
                break;
            case R.id.btn_mi_pedido:
                onComunicationFragmentGrid.onIrAlPedidoFragmenGrid();
                break;

        }
    }
}
