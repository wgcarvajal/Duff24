package duff24.com.duff24;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import duff24.com.duff24.adaptadores.AdaptadorProductoPedido;
import duff24.com.duff24.basededatos.AdminSQliteOpenHelper;
import duff24.com.duff24.modelo.Producto;
import duff24.com.duff24.typeface.CustomTypefaceSpan;
import duff24.com.duff24.util.AppUtil;

public class PedidoActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener, AdapterView.OnItemClickListener, AdaptadorProductoPedido.OnDisminuirTotal {
    private Typeface TF;
    private String font_path = "font/2-4ef58.ttf";
    private TextView titulo;
    private TextView tituloMenuHeader;
    private TextView textTotalPedido;
    private  TextView textValorTotalPedido;
    private ImageView btnMenuPrincipal;
    private DrawerLayout drawer;
    private NavigationView navView;
    private Button btnFinalizarPedido;
    private GridView gridProductosPedido;
    private AdaptadorProductoPedido adapter;
    private List<Producto> data= new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedido);

        titulo = (TextView)findViewById(R.id.txttitulo);
        tituloMenuHeader=(TextView)findViewById(R.id.titulo_header_menu);
        textTotalPedido=(TextView)findViewById(R.id.txt_total_pedido);
        textValorTotalPedido=(TextView)findViewById(R.id.txt_valor_total_pedido);

        btnMenuPrincipal=(ImageView)findViewById(R.id.btn_menu_principal);
        btnFinalizarPedido=(Button)findViewById(R.id.btn_finalizar_pedido);

        gridProductosPedido=(GridView)findViewById(R.id.grid_productos_pedido);

        drawer=(DrawerLayout)findViewById(R.id.drawer);
        navView = (NavigationView) findViewById(R.id.nav);

        btnMenuPrincipal.setOnClickListener(this);
        navView.setNavigationItemSelectedListener(this);
        Menu m = navView.getMenu();
        aplicandoTipoLetraItemMenu(m, font_path);

        TF = Typeface.createFromAsset(getAssets(), font_path);
        titulo.setTypeface(TF);
        tituloMenuHeader.setTypeface(TF);
        btnFinalizarPedido.setTypeface(TF);

        adapter= new AdaptadorProductoPedido(this,data);
        gridProductosPedido.setAdapter(adapter);
        gridProductosPedido.setOnItemClickListener(this);

        loadData();

    }

    private void loadData()
    {
        AdminSQliteOpenHelper admin = new AdminSQliteOpenHelper(this,"admin",null,1);
        SQLiteDatabase db = admin.getReadableDatabase();

        Cursor fila = db.rawQuery("select prodid, prodcantidad from pedido", null);
        if (fila != null) {

            if (fila.moveToFirst()) {

                int contador=2000;
                do {

                    for(Producto p: AppUtil.data)
                    {
                        if(p.getId().equals(fila.getString(fila.getColumnIndex("prodid"))))
                        {
                            data.add(p);
                            contador=contador+(fila.getInt(fila.getColumnIndex("prodcantidad"))*p.getPrecio());
                            adapter.notifyDataSetChanged();
                            break;
                        }
                    }

                } while (fila.moveToNext());

                textValorTotalPedido.setText("$"+contador);

            }

        }


        db.close();
    }

    private void aplicandoTipoLetraItemMenu(Menu m,String tipoLetra)
    {

        for (int i=0;i<m.size();i++) {
            MenuItem mi = m.getItem(i);
            SubMenu subMenu = mi.getSubMenu();
            if (subMenu!=null && subMenu.size() >0 ) {
                for (int j=0; j <subMenu.size();j++) {
                    MenuItem subMenuItem = subMenu.getItem(j);
                    applyFontToMenuItem(subMenuItem,tipoLetra);
                }
            }
            applyFontToMenuItem(mi,tipoLetra);
        }
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.btn_menu_principal:
                drawer.openDrawer(GravityCompat.START);
            break;
        }
    }

    private void applyFontToMenuItem(MenuItem mi,String rutaTipoLetra)
    {
        Typeface font = Typeface.createFromAsset(getAssets(), rutaTipoLetra);
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("" , font), 0 , mNewTitle.length(),  Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem)
    {
        switch (menuItem.getItemId())
        {
            case R.id.nav_la_carta:

                Intent intent = new Intent();
                setResult(RESULT_OK,intent);
                finish();
            break;
        }
        drawer.closeDrawers();
        return false;

    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        MediaPlayer m = MediaPlayer.create(getApplicationContext(),R.raw.sonido_click);
        m.start();
        String valorTotalpedido=textValorTotalPedido.getText().toString();
        String substring = valorTotalpedido.substring(1);
        int contador= Integer.parseInt(substring);
        contador= contador+ data.get(position).getPrecio();
        textValorTotalPedido.setText("$" + contador);

        TextView textconteo= (TextView) view.findViewById(R.id.txtconteo);
        int conteo = Integer.parseInt(textconteo.getText().toString());
        conteo= conteo+1;
        textconteo.setText(conteo+"");

        AdminSQliteOpenHelper admin = new AdminSQliteOpenHelper(getApplicationContext(),"admin",null,1);
        SQLiteDatabase db = admin.getWritableDatabase();

        ContentValues registroPedido= new ContentValues();
        registroPedido.put("prodcantidad",conteo);

        int cant= db.update("pedido",registroPedido,"prodid = '"+data.get(position).getId()+"'",null);

        db.close();

    }

    @Override
    public void onDisminuirTotal(int precio)
    {
        String valorTotalpedido=textValorTotalPedido.getText().toString();
        String substring = valorTotalpedido.substring(1);
        int contador= Integer.parseInt(substring)-precio;
        if(contador==2000)
        {
            contador=0;
        }
        textValorTotalPedido.setText("$"+contador);
    }
}
