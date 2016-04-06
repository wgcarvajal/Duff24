package duff24.com.duff24;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import duff24.com.duff24.util.FontCache;

public class PedidoencaminoActivity extends AppCompatActivity {

    private String font_path="font/A_Simple_Life.ttf";
    private String fontStackyard="font/Stackyard.ttf";
    private TextView titulo;
    private TextView mensaje;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedidoencamino);

        titulo=(TextView)findViewById(R.id.tituloNotificacion);
        mensaje=(TextView)findViewById(R.id.mensaje);


        Typeface TF= FontCache.get(fontStackyard, this);
        titulo.setTypeface(TF);
        TF = FontCache.get(font_path, this);
        mensaje.setTypeface(TF);

    }


}
