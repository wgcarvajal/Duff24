package duff24.com.duff24.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import duff24.com.duff24.R;
import duff24.com.duff24.util.FontCache;

public class AnuncioFragment extends FragmentGeneric {


    private String subcategoriaing;
    private String subcategoriaesp;
    private TextView titulo;
    private ImageView anuncio;
    private String font_path = "font/2-4ef58.ttf";


    public AnuncioFragment()
    {
        // Required empty public constructor
    }

    public void init(String subcategoriaing,String subcategoriaesp)
    {
        this.subcategoriaing=subcategoriaing;
        this.subcategoriaesp=subcategoriaesp;
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        outState.putString("subcategoriaing", subcategoriaing);
        outState.putString("subcategoriaesp", subcategoriaesp);
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
    }

    @Override
    public void actualizarData()
    {

    }
}
