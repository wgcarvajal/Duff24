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
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import java.util.ArrayList;
import java.util.List;
import duff24.com.duff24.adaptadores.PagerAdapter;
import duff24.com.duff24.fragments.ProductoFragment;
import duff24.com.duff24.fragments.ProductoGridFragment;
import duff24.com.duff24.modelo.Producto;
import duff24.com.duff24.modelo.Subcategoria;
import duff24.com.duff24.typeface.CustomTypefaceSpan;
import duff24.com.duff24.util.AppUtil;
import pl.droidsonroids.gif.GifImageView;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    public final static int MI_REQUEST_CODE = 1;

    private TextView titulo;
    private ViewPager pager;
    private PagerAdapter adapter;
    private List<ProductoFragment> data;
    private ImageView btnBebidas;
    private ImageView btnMarket;
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
        btnBebidas = (ImageView) findViewById(R.id.btnbebida);
        btnMarket = (ImageView) findViewById(R.id.btnmarket);

        drawer=(DrawerLayout)findViewById(R.id.drawer);
        pager= (ViewPager)findViewById(R.id.pager);
        navView = (NavigationView) findViewById(R.id.nav);

        btnMenuPrincipal.setOnClickListener(this);
        btnBebidas.setOnClickListener(this);
        btnMarket.setOnClickListener(this);
        btnMipedido.setOnClickListener(this);
        navView.setNavigationItemSelectedListener(this);

        btnMenuPrincipal.setVisibility(View.GONE);
        btnMipedido.setVisibility(View.GONE);
        btnBebidas.setVisibility(View.GONE);
        btnMarket.setVisibility(View.GONE);

        TF = Typeface.createFromAsset(getAssets(), font_path);
        titulo.setTypeface(TF);
        tituloMenuHeader.setTypeface(TF);

        data= new ArrayList<>();
        adapter = new PagerAdapter(getSupportFragmentManager(), data);
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
            applyFontToMenuItem(mi, tipoLetra);

        }
    }
    private void loadData()
    {
        if(AppUtil.data.size()>0)
        {
            for(Subcategoria sub: AppUtil.listaSubcategorias)
            {
                if(sub.getNombreCategoria().equals("Food"))
                {
                    ProductoFragment productoFragment = new ProductoFragment();
                    productoFragment.init(sub.getNombreIngles(), sub.getNombreEspanol());
                    data.add(productoFragment);
                    adapter.notifyDataSetChanged();
                }
            }
            btnMenuPrincipal.setVisibility(View.VISIBLE);
            btnMipedido.setVisibility(View.VISIBLE);
            btnBebidas.setVisibility(View.VISIBLE);
            btnMarket.setVisibility(View.VISIBLE);

        }
        else
        {
            cargarDatosParse();
        }
    }

    public void cargarDatosParse()
    {
        ParseQuery<ParseObject> queryCategorias= new ParseQuery<>(Producto.TABLACATEGORIA);
        queryCategorias.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(final List<ParseObject> categorias, ParseException e) {
                if(e==null)
                {
                    ParseQuery<ParseObject> querySubcategoria= new ParseQuery<>(Producto.TABLASUBCATEGORIA);
                    querySubcategoria.orderByAscending(Producto.TBLPRECIO_FECHACREACION);
                    querySubcategoria.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(final List<ParseObject> subcategorias, ParseException e) {
                            if(e==null)
                            {
                                ParseQuery<ParseObject> queryPrecios= new ParseQuery<>(Producto.TABLAPRECIO);
                                queryPrecios.whereEqualTo(Producto.TBLPRECIO_PREFECHAFIN,null);
                                queryPrecios.findInBackground(new FindCallback<ParseObject>() {
                                    @Override
                                    public void done(final List<ParseObject> precios, ParseException e) {
                                        if(e==null)
                                        {
                                            ParseQuery<ParseObject> queryProductos= new ParseQuery<>(Producto.TABLA);
                                            queryProductos.findInBackground(new FindCallback<ParseObject>() {
                                                @Override
                                                public void done(List<ParseObject> productos, ParseException e)
                                                {
                                                    if(e==null)
                                                    {

                                                        for(ParseObject prod: productos)
                                                        {
                                                            Producto producto = new Producto();
                                                            producto.setId(prod.getObjectId());
                                                            producto.setNombreing(prod.getString(Producto.NOMBREING));
                                                            producto.setNombreesp(prod.getString(Producto.NOMBREESP));
                                                            producto.setDescripcionIng(prod.getString(Producto.DESCRIPCIONING));
                                                            producto.setDescripcionesp(prod.getString(Producto.DESCRIPCIONESP));
                                                            for(ParseObject pre: precios)
                                                            {
                                                                if(pre.getString(Producto.TBLPRECIO_PRODUCTO).equals(prod.getObjectId()))
                                                                {
                                                                    producto.setPrecio(pre.getInt(Producto.TBLPRECIO_VALOR));
                                                                    break;
                                                                }
                                                            }
                                                            for(ParseObject sub: subcategorias)
                                                            {
                                                                if(sub.getObjectId().equals(prod.getString(Producto.SUBCATEGORIA)))
                                                                {
                                                                    producto.setSubcategoriaing(sub.getString(Producto.TBLSUBCATEGORIA_NOMBRE));
                                                                    producto.setSubcategoriaesp(sub.getString(Producto.TBLSUBCATEGORIA_NOMBREESP));
                                                                    for(ParseObject cat: categorias)
                                                                    {
                                                                        if(cat.getObjectId().equals(sub.getString(Producto.TBLSUBCATEGORIA_CATEGORIA)))
                                                                        {
                                                                            producto.setCategoria(cat.getString(Producto.CATEGORIANOMBRE));
                                                                            AppUtil.data.add(producto);
                                                                            break;
                                                                        }
                                                                    }
                                                                    break;
                                                                }


                                                            }

                                                        }
                                                        for(ParseObject sub: subcategorias)
                                                        {
                                                            for (ParseObject cat : categorias)
                                                            {
                                                                if (sub.getString(Producto.TBLSUBCATEGORIA_CATEGORIA).equals(cat.getObjectId())) {
                                                                    Subcategoria subcategoria = new Subcategoria();
                                                                    subcategoria.setNombreCategoria(cat.getString(Producto.CATEGORIANOMBRE));
                                                                    subcategoria.setNombreEspanol(sub.getString(Producto.TBLSUBCATEGORIA_NOMBREESP));
                                                                    subcategoria.setNombreIngles(sub.getString(Producto.TBLSUBCATEGORIA_NOMBRE));
                                                                    AppUtil.listaSubcategorias.add(subcategoria);
                                                                    break;
                                                                }
                                                            }
                                                        }
                                                        for(Subcategoria sub: AppUtil.listaSubcategorias)
                                                        {
                                                            if(sub.getNombreCategoria().equals("Food"))
                                                            {
                                                                ProductoFragment productoFragment = new ProductoFragment();
                                                                productoFragment.init(sub.getNombreIngles(), sub.getNombreEspanol());
                                                                data.add(productoFragment);
                                                                adapter.notifyDataSetChanged();
                                                            }
                                                        }
                                                        btnMenuPrincipal.setVisibility(View.VISIBLE);
                                                        btnMipedido.setVisibility(View.VISIBLE);
                                                        btnBebidas.setVisibility(View.VISIBLE);
                                                        btnMarket.setVisibility(View.VISIBLE);
                                                    }

                                                }
                                            });
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });

    }

    @Override
    public void onClick(View v)
    {
        Intent intent;
        switch (v.getId())
        {
            case R.id.btnbebida:
                intent = new Intent(this,BebidasActivity.class);
                startActivity(intent);
                finish();
            break;
            case  R.id.btnmarket:
                intent = new Intent(this,MarketActivity.class);
                startActivity(intent);
                finish();
            break;
            case R.id.btn_menu_principal:
                    drawer.openDrawer(GravityCompat.START);
            break;
            case R.id.btn_mi_pedido:
                intent = new Intent(this,PedidoActivity.class);
                startActivityForResult(intent,MI_REQUEST_CODE);
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

                int posicionactual=pager.getCurrentItem();
                Log.i("posicion actual:",posicionactual+"");
                int tamano=adapter.getCount();
                ProductoFragment prodFrag=(ProductoFragment)adapter.getItem(posicionactual);
                prodFrag.actualizarData();
                if((posicionactual+1)<tamano)
                {
                    prodFrag=(ProductoFragment)adapter.getItem(posicionactual+1);
                    prodFrag.actualizarData();
                }

                if((posicionactual-1)>=0)
                {
                    prodFrag=(ProductoFragment)adapter.getItem(posicionactual-1);
                    prodFrag.actualizarData();
                }
        }
    }
}
