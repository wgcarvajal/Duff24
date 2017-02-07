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
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import duff24.com.duff24.SpacesItemDecoration;
import duff24.com.duff24.adaptadores.AdaptadorProducto;
import duff24.com.duff24.basededatos.AdminSQliteOpenHelper;
import duff24.com.duff24.modelo.Producto;
import duff24.com.duff24.util.AppUtil;
import duff24.com.duff24.util.FontCache;
import pl.droidsonroids.gif.GifImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProductoFragment extends FragmentGeneric implements  View.OnClickListener ,AdaptadorProducto.OnItemClickListener{
    private static final String LIST_STATE = "listState";

    private Parcelable mListState = null;
    private String subcategoria;
    private String subcategoriaNombre;
    private TextView titulo;
    private RecyclerView gridProductos;
    private List<Producto> data= new ArrayList<>();
    private AdaptadorProducto adapter;
    private ImageView carritoCompras;
    private ImageView menuPrincipal;
    private String rotulafont="font/VTKS_ANIMAL_2.ttf";
    private GridLayoutManager layoutManager;


    public ProductoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onComunicationFragment=(OnComunicationFragment)context;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btn_mi_pedido:
                onComunicationFragment.onIrAlPedidoFragment();
                break;

            case R.id.btn_menu_principal:
                onComunicationFragment.onAbrirMenuPrincipalFragment();
                break;
        }
    }

   @Override
    public void itemClick(Producto producto,TextView btnDisminuir , TextView txtconteo)
    {
        onComunicationFragment.onAbrirDescripcionProducto(producto,btnDisminuir,txtconteo);
    }

    @Override
    public void itemClickDisminuir(Producto producto, TextView btnDisminuir, TextView txtconteo) {

        onComunicationFragment.onAbrirDisminuirProducto(producto,btnDisminuir,txtconteo);
    }

    public interface OnComunicationFragment
    {
        void onIrAlPedidoFragment();
        void onAbrirMenuPrincipalFragment();
        void onAbrirDescripcionProducto(Producto producto,TextView btnDisminuir , TextView txtconteo);
        void onAbrirDisminuirProducto(Producto producto,TextView btnDisminuir , TextView txtconteo);
    }
    OnComunicationFragment onComunicationFragment;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if(savedInstanceState!=null)
        {
            subcategoria=savedInstanceState.getString("subcategoria");
            subcategoriaNombre=savedInstanceState.getString("subcategoriaNombre");
            mListState=savedInstanceState.getParcelable(LIST_STATE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View v= inflater.inflate(R.layout.fragment_producto, container, false);
        gridProductos= (RecyclerView) v.findViewById(R.id.gridProductos);
        titulo=(TextView)v.findViewById(R.id.textsubcategoria);

        layoutManager = new GridLayoutManager(v.getContext(),3);
        gridProductos.setLayoutManager(layoutManager);
        adapter= new AdaptadorProducto(v.getContext(),data,this);
        SpacesItemDecoration spacesItemDecoration = new SpacesItemDecoration(getResources().getDimensionPixelSize(R.dimen.margin_item_grid_left_right),getResources().getDimensionPixelSize(R.dimen.margin_item_grid_bottom));
        gridProductos.setAdapter(adapter);
        gridProductos.addItemDecoration(spacesItemDecoration);

        //gridProductos.setOnItemClickListener(this);

        carritoCompras=(ImageView)v.findViewById(R.id.btn_mi_pedido);
        menuPrincipal=(ImageView)v.findViewById(R.id.btn_menu_principal);
        carritoCompras.setOnClickListener(this);
        menuPrincipal.setOnClickListener(this);

        titulo.setText(subcategoriaNombre);
        Typeface TF= FontCache.get(rotulafont,v.getContext());
        titulo.setTypeface(TF,Typeface.BOLD);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
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

    public void init(String subcategoria,String subcategoriaNombre)
    {

        this.subcategoria=subcategoria;
        this.subcategoriaNombre=subcategoriaNombre;
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        mListState = layoutManager.onSaveInstanceState();
        outState.putString("subcategoria", subcategoria);
        outState.putString("subcategoriaNombre", subcategoriaNombre);
        outState.putParcelable(LIST_STATE, mListState);
        super.onSaveInstanceState(outState);
    }

    private void loadData()
    {
        LoadDataTask loadDataTask= new LoadDataTask();
        loadDataTask.execute();
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
                if(prod.getSubcategoria().equals(subcategoria))
                {
                    data.add(prod);

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
                layoutManager.onRestoreInstanceState(mListState);
            }
            adapter.notifyDataSetChanged();
            mListState = null;
        }
    }




}