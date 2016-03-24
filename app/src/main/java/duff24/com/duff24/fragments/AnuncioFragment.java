package duff24.com.duff24.fragments;

import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.BackendlessDataQuery;
import com.backendless.persistence.QueryOptions;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import duff24.com.duff24.R;
import duff24.com.duff24.modelo.Anuncio;
import duff24.com.duff24.util.AppUtil;
import duff24.com.duff24.util.FontCache;

public class AnuncioFragment extends FragmentGeneric {


    private String subcategoriaing;
    private String subcategoriaesp;
    private String anuncioId;
    private TextView titulo;
    private ImageView anuncio;
    private String font_path = "font/2-4ef58.ttf";


    public AnuncioFragment()
    {
        // Required empty public constructor
    }

    public void init(String subcategoriaing,String subcategoriaesp,String id)
    {
        this.subcategoriaing=subcategoriaing;
        this.subcategoriaesp=subcategoriaesp;
        anuncioId=id;
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        outState.putString("subcategoriaing", subcategoriaing);
        outState.putString("subcategoriaesp", subcategoriaesp);
        outState.putString("anuncioId",anuncioId);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if(savedInstanceState!=null)
        {
            subcategoriaing=savedInstanceState.getString("subcategoriaing");
            subcategoriaesp=savedInstanceState.getString("subcategoriaesp");
            anuncioId=savedInstanceState.getString("anuncioId");
        }
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_anuncio, container, false);

        titulo=(TextView)v.findViewById(R.id.textsubcategoria);
        anuncio=(ImageView)v.findViewById(R.id.anuncio);
        Typeface TF= FontCache.get(font_path,inflater.getContext());
        titulo.setTypeface(TF);
        if(getResources().getString(R.string.idioma).equals("es"))
        {
            titulo.setText(this.subcategoriaesp);
        }
        else
        {
            titulo.setText(this.subcategoriaing);
        }
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        loadAnuncio();
    }

    private void loadAnuncio()
    {
        int bandera=0;
        Anuncio an;
        for(Anuncio a : AppUtil.listaAnuncios)
        {
            if(a.getIdSubcategoria().equals(anuncioId))
            {
                Log.i("ya habia","un anuncio guardado");
                an=a;
                bandera=1;
                if(getResources().getString(R.string.idioma).equals("es"))
                {
                    Picasso.with(getContext())
                            .load(an.getImgFileEsp())
                            .into(anuncio);
                }
                else
                {
                    Picasso.with(getContext())
                            .load(an.getImgFile())
                            .into(anuncio);
                }
            }

        }
        if(bandera==0)
        {
            cargarDatoRemoto();
        }
    }

    private void cargarDatoRemoto()
    {
        final Context context=getContext();

        BackendlessDataQuery dataQueryanuncio= new BackendlessDataQuery();
        List<String> anuncioSelect=new ArrayList<>();
        anuncioSelect.add("objectId");
        anuncioSelect.add("imgFile");
        anuncioSelect.add("imgFileEsp");
        anuncioSelect.add("idSubcategoria");

        dataQueryanuncio.setProperties(anuncioSelect);
        dataQueryanuncio.setWhereClause("subcategoria='"+anuncioId+"'");
        QueryOptions queryOptionsAnuncio= new QueryOptions();
        queryOptionsAnuncio.setPageSize(100);
        dataQueryanuncio.setQueryOptions(queryOptionsAnuncio);
        Backendless.Persistence.of(Anuncio.class).findFirst(new AsyncCallback<Anuncio>() {
            @Override
            public void handleResponse(Anuncio response)
            {
                AppUtil.listaAnuncios.add(response);

                if(context.getResources().getString(R.string.idioma).equals("es"))
                {
                    Picasso.with(getContext())
                            .load(response.getImgFileEsp())
                            .into(anuncio);
                }
                else
                {
                    Picasso.with(getContext())
                            .load(response.getImgFile())
                            .into(anuncio);
                }
            }

            @Override
            public void handleFault(BackendlessFault fault)
            {

            }
        });
    }
    @Override
    public void actualizarData()
    {

    }
}
