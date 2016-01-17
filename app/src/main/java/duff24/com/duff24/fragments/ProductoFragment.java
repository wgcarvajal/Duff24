package duff24.com.duff24.fragments;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
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
public class ProductoFragment extends Fragment implements AdapterView.OnItemClickListener {

    private static final String LIST_STATE = "listState";
    private Parcelable mListState = null;


    private String subcategoria;
    private String subcategoriaesp;
    private TextView titulo;

    private ListView listaProductos;
    private List<Producto> data= new ArrayList<>();
    private AdaptadorProducto adapter;


    public ProductoFragment() {
        // Required empty public constructor
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
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_producto, container, false);
        listaProductos= (ListView) v.findViewById(R.id.lstproductos);

        String font_path = "font/2-4ef58.ttf";
        Typeface TF = Typeface.createFromAsset(inflater.getContext().getAssets(), font_path);
        titulo = (TextView) v.findViewById(R.id.textsubcategoria);
        titulo.setText(this.subcategoria + "s");
        titulo.setTypeface(TF);
        if(getResources().getString(R.string.idioma).equals("es"))
        {
            titulo.setText(this.subcategoriaesp+"s");
        }

        adapter= new AdaptadorProducto(v.getContext(),data);
        listaProductos.setAdapter(adapter);
        listaProductos.setOnItemClickListener(this);
        if(data.size()>0)
        {

            adapter.notifyDataSetChanged();
        }
        else {
            loadData();
        }

        return v;
    }



    public void init(String subcategoria,String subcategoriaesp)
    {
        this.subcategoria=subcategoria;
        this.subcategoriaesp=subcategoriaesp;
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        mListState = listaProductos.onSaveInstanceState();
        outState.putString("subcategoria", subcategoria);
        outState.putString("subcategoriaesp", subcategoriaesp);
        outState.putParcelable(LIST_STATE, mListState);
        super.onSaveInstanceState(outState);
    }

    public void loadData()
    {
        ParseQuery<ParseObject> querySubcategoria = new ParseQuery<ParseObject>(Producto.TABLASUBCATEGORIA);
        querySubcategoria.whereEqualTo(Producto.TBLSUBCATEGORIA_NOMBRE, this.subcategoria);
        querySubcategoria.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject subCategoria, ParseException e) {
                if (e == null) {

                    ParseQuery<ParseObject> queryProductos = new ParseQuery<ParseObject>(Producto.TABLA);
                    queryProductos.whereEqualTo(Producto.SUBCATEGORIA, subCategoria.getObjectId());
                    queryProductos.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> productos, ParseException e) {
                            if (e == null) {
                                for (final ParseObject prod : productos) {

                                    int bandera=0;
                                    for(Producto p:AppUtil.data)
                                    {
                                        if(p.getId().equals(prod.getObjectId()))
                                        {
                                            data.add(p);
                                            bandera=1;
                                            break;
                                        }
                                    }

                                    if(bandera==0)
                                    {
                                        Producto producto = new Producto();
                                        producto.setId(prod.getObjectId());
                                        producto.setNombre(prod.getString(Producto.NOMBRE));
                                        producto.setDescripcion(prod.getString(Producto.DESCRIPCION));
                                        producto.setPrecio(0);
                                        producto.setImagen(null);
                                        if (isAdded() && getResources().getString(R.string.idioma).equals("es")) {
                                            producto.setNombre(prod.getString(Producto.NOMBREESP));
                                            producto.setDescripcion(prod.getString(Producto.DESCRIPCIONESP));
                                        }
                                        AppUtil.data.add(producto);
                                        data.add(producto);
                                    }


                                    adapter.notifyDataSetChanged();
                                    if (mListState != null)
                                    {
                                        listaProductos.onRestoreInstanceState(mListState);
                                    }

                                    mListState = null;

                                }
                            } else {
                                Log.i("entro ", "al error");
                            }
                        }
                    });
                }
            }
        });
    }



    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
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
            registroPedido.put("prodnombre",data.get(position).getNombre());
            db.insert("pedido",null,registroPedido);
        }
        db.close();
    }


}
