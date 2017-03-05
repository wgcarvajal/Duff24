package duff24.com.duff24.fragments;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.BackendlessDataQuery;
import com.backendless.persistence.QueryOptions;
import com.squareup.picasso.Callback;
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
    private ImageView anuncio;
    private ImageView placeholder;


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


        anuncio=(ImageView)v.findViewById(R.id.anuncio);
        placeholder = (ImageView) v.findViewById(R.id.placeholder);
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
                placeholder.setVisibility(View.VISIBLE);
                placeholder.setImageResource(R.drawable.foodgif);
                if(getResources().getString(R.string.idioma).equals("es"))
                {
                    Picasso.with(getContext())
                            .load(an.getImgFileEsp())
                            .into(anuncio, new Callback() {
                                @Override
                                public void onSuccess()
                                {
                                    placeholder.setImageDrawable(null);
                                    placeholder.setVisibility(View.GONE);

                                }

                                @Override
                                public void onError() {

                                }
                            });
                }
                else
                {
                    Picasso.with(getContext())
                            .load(an.getImgFile())
                            .into(anuncio, new Callback() {
                                @Override
                                public void onSuccess()
                                {
                                    placeholder.setImageDrawable(null);
                                    placeholder.setVisibility(View.GONE);
                                }

                                @Override
                                public void onError() {

                                }
                            });
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
        dataQueryanuncio.setWhereClause("idSubcategoria='"+anuncioId+"'");
        QueryOptions queryOptionsAnuncio= new QueryOptions();
        queryOptionsAnuncio.setPageSize(100);
        dataQueryanuncio.setQueryOptions(queryOptionsAnuncio);
        Backendless.Persistence.of(Anuncio.class).find(dataQueryanuncio, new AsyncCallback<BackendlessCollection<Anuncio>>() {
            @Override
            public void handleResponse(BackendlessCollection<Anuncio> response)
            {
                Anuncio anuncioResponse = response.getData().get(0);
                AppUtil.listaAnuncios.add(anuncioResponse);
                loadAnuncio();
            }

            @Override
            public void handleFault(BackendlessFault fault)
            {
                Log.i("imformacion:", fault.getMessage());
            }
        });
    }

    @Override
    public void actualizarData()
    {

    }
}
