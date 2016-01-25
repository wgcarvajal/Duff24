package duff24.com.duff24.fragments;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
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
public class ProductoFragment extends Fragment implements AdapterView.OnItemClickListener
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


        if(data.size()>0)
        {
            adapter.notifyDataSetChanged();
        }
        else
        {
            loadData();
        }
        return v;
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
                adapter.notifyDataSetChanged();
                if (mListState != null)
                {
                    listaProductos.onRestoreInstanceState(mListState);
                }
                mListState = null;
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        final MediaPlayer m = MediaPlayer.create(getContext(),R.raw.sonido_click);
        m.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                if (mp == m) {
                    m.start();
                }
            }
        });

        TextView textconteo= (TextView) view.findViewById(R.id.txtconteo);
        textconteo.setVisibility(View.VISIBLE);

        ImageView btnDisminuir= (ImageView) view.findViewById(R.id.btn_disminuir);
        btnDisminuir.setVisibility(View.VISIBLE);
        int conteo = Integer.parseInt(textconteo.getText().toString());
        conteo= conteo+1;
        textconteo.setText(conteo + "");


        AdminSQliteOpenHelper admin = new AdminSQliteOpenHelper(getContext(),"admin",null,1);
        SQLiteDatabase db = admin.getWritableDatabase();
        String prodid = data.get(position).getId();

        Cursor fila = db.rawQuery("select prodcantidad from pedido where prodid = '" + prodid + "'", null);
        ContentValues registroPedido= new ContentValues();
        registroPedido.put("prodcantidad",conteo);

        if(fila.moveToFirst())
        {

            int cant= db.update("pedido",registroPedido,"prodid = '"+prodid+"'",null);
        }
        else
        {
            registroPedido.put("prodid",prodid);
            registroPedido.put("prodprecio",data.get(position).getPrecio());
            registroPedido.put("prodnombreesp",data.get(position).getNombreesp());
            registroPedido.put("prodnombreing",data.get(position).getNombreing());
            registroPedido.put("proddescripcioning",data.get(position).getDescripcionIng());
            registroPedido.put("proddescripcionesp",data.get(position).getDescripcionesp());

            db.insert("pedido",null,registroPedido);
        }
        db.close();
    }

    public void actualizarData()
    {
        adapter.notifyDataSetChanged();
    }


}
