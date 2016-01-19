package duff24.com.duff24;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import java.util.ArrayList;
import java.util.List;
import duff24.com.duff24.adaptadores.PagerAdapterGrid;
import duff24.com.duff24.fragments.ProductoGridFragment;
import duff24.com.duff24.modelo.Producto;
import duff24.com.duff24.modelo.Subcategoria;
import duff24.com.duff24.typeface.CustomTypefaceSpan;
import duff24.com.duff24.util.AppUtil;
import pl.droidsonroids.gif.GifImageView;

public class MarketActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    public final static int MI_REQUEST_CODE = 1;

    private ImageView btnComidas;
    private ImageView btnBebidas;
    private ImageView btnMarket;
    private TextView titulo;
    private ViewPager pager;
    private PagerAdapterGrid adapter;
    private List<ProductoGridFragment> data;
    private NavigationView navView;
    private ImageView btnMenuPrincipal;
    private GifImageView btnMipedido;
    private DrawerLayout drawer;
    private TextView tituloMenuHeader;
    private Typeface TF;
    private String font_path = "font/2-4ef58.ttf";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        titulo = (TextView)findViewById(R.id.txttitulo);
        tituloMenuHeader=(TextView)findViewById(R.id.titulo_header_menu);

        btnMenuPrincipal=(ImageView)findViewById(R.id.btn_menu_principal);
        btnMipedido = (GifImageView)findViewById(R.id.btn_mi_pedido);
        btnComidas = (ImageView) findViewById(R.id.btncomida);
        btnMarket=(ImageView) findViewById(R.id.btnmarket);
        btnBebidas= (ImageView) findViewById(R.id.btnbebida);

        drawer=(DrawerLayout)findViewById(R.id.drawer);
        pager= (ViewPager)findViewById(R.id.pager);
        navView = (NavigationView) findViewById(R.id.nav);

        btnMenuPrincipal.setOnClickListener(this);
        btnComidas.setOnClickListener(this);
        btnBebidas.setOnClickListener(this);
        btnMipedido.setOnClickListener(this);
        navView.setNavigationItemSelectedListener(this);

        btnComidas.setImageResource(R.mipmap.ic_foods_opaco);
        btnBebidas.setImageResource(R.mipmap.ic_drinks_opaco);
        btnMarket.setImageResource(R.mipmap.ic_market);

        TF = Typeface.createFromAsset(getAssets(), font_path);
        titulo.setTypeface(TF);
        tituloMenuHeader.setTypeface(TF);

        data= new ArrayList<>();
        adapter = new PagerAdapterGrid(getSupportFragmentManager(), data);
        pager.setAdapter(adapter);

        Menu m = navView.getMenu();
        aplicandoTipoLetraItemMenu(m,font_path);

        loadData();
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

    private void loadData()
    {
        for(Subcategoria sub: AppUtil.listaSubcategorias)
        {
            if(sub.getNombreCategoria().equals("Market"))
            {
                ProductoGridFragment productoFragment = new ProductoGridFragment();
                productoFragment.init(sub.getNombreIngles(), sub.getNombreEspanol());
                data.add(productoFragment);
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onClick(View v)
    {
        Intent intent;
        switch (v.getId())
        {
            case R.id.btncomida:
                intent = new Intent(this,MainActivity.class);
                startActivity(intent);
                finish();
            break;
            case R.id.btnbebida:
                intent = new Intent(this,BebidasActivity.class);
                startActivity(intent);
                finish();
            break;
            case R.id.btn_menu_principal:
                drawer.openDrawer(GravityCompat.START);
            break;
            case R.id.btn_mi_pedido:
                intent = new Intent(this,PedidoActivity.class);
                startActivityForResult(intent, MI_REQUEST_CODE);
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
        Intent intent;
        switch (menuItem.getItemId())
        {
            case R.id.nav_mi_pedido:
                intent = new Intent(this,PedidoActivity.class);
                startActivityForResult(intent, MI_REQUEST_CODE);
                break;
        }
        drawer.closeDrawers();
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MI_REQUEST_CODE) {

            if (requestCode == MI_REQUEST_CODE)
            {
                int posicionactual=pager.getCurrentItem();
                int tamano=adapter.getCount();
                ProductoGridFragment prodFrag=(ProductoGridFragment)adapter.getItem(posicionactual);
                prodFrag.actualizarData();
                if((posicionactual+1)<tamano)
                {
                    prodFrag=(ProductoGridFragment)adapter.getItem(posicionactual+1);
                    prodFrag.actualizarData();
                }
                if((posicionactual-1)>=0)
                {
                    prodFrag=(ProductoGridFragment)adapter.getItem(posicionactual-1);
                    prodFrag.actualizarData();
                }
            }

        }
    }
}
