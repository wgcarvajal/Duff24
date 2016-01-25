package duff24.com.duff24;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import bolts.Task;
import duff24.com.duff24.adaptadores.AdaptadorProducto;
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
    private List<ProductoFragment> data= new ArrayList<>();
    private ImageView btnBebidas;
    private ImageView btnMarket;
    private ImageView btnComidas;
    private NavigationView navView;
    private ImageView btnMenuPrincipal;
    private GifImageView btnMipedido;
    private DrawerLayout drawer;
    private TextView tituloMenuHeader;
    private TextView text_compruebe_conexion;
    private Button btnRecargarVista;
    private Typeface TF;
    private String font_path = "font/2-4ef58.ttf";
    private String font_path_ASimple="font/A_Simple_Life.ttf";
    private ProgressDialog pd = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        titulo = (TextView)findViewById(R.id.txttitulo);
        tituloMenuHeader=(TextView)findViewById(R.id.titulo_header_menu);
        text_compruebe_conexion=(TextView)findViewById(R.id.txt_sin_conexion);

        btnMenuPrincipal=(ImageView)findViewById(R.id.btn_menu_principal);
        btnMipedido = (GifImageView)findViewById(R.id.btn_mi_pedido);
        btnBebidas = (ImageView) findViewById(R.id.btnbebida);
        btnMarket = (ImageView) findViewById(R.id.btnmarket);
        btnComidas=(ImageView) findViewById(R.id.btncomida);
        btnRecargarVista=(Button)findViewById(R.id.volver_cargar);

        drawer=(DrawerLayout)findViewById(R.id.drawer);
        pager= (ViewPager)findViewById(R.id.pager);
        navView = (NavigationView) findViewById(R.id.nav);

        btnMenuPrincipal.setOnClickListener(this);
        btnBebidas.setOnClickListener(this);
        btnMarket.setOnClickListener(this);
        btnMipedido.setOnClickListener(this);
        navView.setNavigationItemSelectedListener(this);
        btnRecargarVista.setOnClickListener(this);

        btnMenuPrincipal.setVisibility(View.GONE);
        btnMipedido.setVisibility(View.GONE);
        btnBebidas.setVisibility(View.GONE);
        btnMarket.setVisibility(View.GONE);
        btnComidas.setVisibility(View.GONE);
        text_compruebe_conexion.setVisibility(View.GONE);
        btnRecargarVista.setVisibility(View.GONE);

        TF = Typeface.createFromAsset(getAssets(), font_path);
        titulo.setTypeface(TF);
        tituloMenuHeader.setTypeface(TF);
        TF = Typeface.createFromAsset(getAssets(), font_path_ASimple);
        text_compruebe_conexion.setTypeface(TF);
        btnRecargarVista.setTypeface(TF);

        adapter = new PagerAdapter(getSupportFragmentManager(), data);
        pager.setAdapter(adapter);
        Menu m = navView.getMenu();
        aplicandoTipoLetraItemMenu(m, font_path_ASimple);
        ocultandoMenu(m);
        loadData();


    }
    private void ocultandoMenu(Menu m)
    {
        for (int i=0;i<m.size();i++) {
            MenuItem mi = m.getItem(i);
            SubMenu subMenu = mi.getSubMenu();
            if (subMenu!=null && subMenu.size() >0 ) {
                for (int j=0; j <subMenu.size();j++) {
                    MenuItem subMenuItem = subMenu.getItem(j);
                    subMenuItem.setVisible(false);
                }
            }
            mi.setVisible(false);

        }
    }

    private void mostrandoMenu(Menu m)
    {
        for (int i=0;i<m.size();i++) {
            MenuItem mi = m.getItem(i);
            SubMenu subMenu = mi.getSubMenu();
            if (subMenu!=null && subMenu.size() >0 ) {
                for (int j=0; j <subMenu.size();j++) {
                    MenuItem subMenuItem = subMenu.getItem(j);
                    subMenuItem.setVisible(true);
                }
            }
            mi.setVisible(true);

        }
    }

    private void aplicandoTipoLetraItemMenu(Menu m,String tipoLetra)
    {

        for (int i=0;i<m.size();i++) {
            MenuItem mi = m.getItem(i);
            SubMenu subMenu = mi.getSubMenu();
            if (subMenu!=null && subMenu.size() >0 ) {
                for (int j=0; j <subMenu.size();j++) {
                    MenuItem subMenuItem = subMenu.getItem(j);
                    applyFontToMenuItem(subMenuItem, tipoLetra);
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
            btnComidas.setVisibility(View.VISIBLE);
            Menu m=navView.getMenu();
            mostrandoMenu(m);

            ParseUser currentUser = ParseUser.getCurrentUser();
            if (currentUser != null) {
                m.getItem(1).setTitle(currentUser.getUsername());
            } else
            {
                m.getItem(1).setVisible(false);
                Menu men=m.getItem(1).getSubMenu();
                men.getItem(0).setVisible(false);
            }


        }
        else
        {
            CargarDatosRemotosTask cargarDatosRemotosTask=new CargarDatosRemotosTask();
            cargarDatosRemotosTask.execute();
        }
    }

    public void cargarDatosParse()
    {
        ParseQuery<ParseObject> queryCategorias= new ParseQuery<>(Producto.TABLACATEGORIA);
        queryCategorias.selectKeys(Arrays.asList(Producto.ID,Producto.CATEGORIANOMBRE));
        queryCategorias.findInBackground(new FindCallback<ParseObject>() {

            @Override
            public void done(final List<ParseObject> categorias, ParseException e) {
                if(e==null)
                {
                    ParseQuery<ParseObject> querySubcategoria= new ParseQuery<>(Producto.TABLASUBCATEGORIA);
                    querySubcategoria.orderByAscending(Producto.TBLPRECIO_FECHACREACION);
                    querySubcategoria.selectKeys(Arrays.asList(Producto.ID,Producto.TBLSUBCATEGORIA_NOMBRE,Producto.TBLSUBCATEGORIA_NOMBREESP,Producto.TBLSUBCATEGORIA_CATEGORIA));
                    querySubcategoria.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(final List<ParseObject> subcategorias, ParseException e) {
                            if(e==null)
                            {
                                ParseQuery<ParseObject> queryPrecios= new ParseQuery<>(Producto.TABLAPRECIO);
                                queryPrecios.whereEqualTo(Producto.TBLPRECIO_PREFECHAFIN,null);
                                queryPrecios.selectKeys(Arrays.asList(Producto.TBLPRECIO_PRODUCTO,Producto.TBLPRECIO_VALOR));
                                queryPrecios.findInBackground(new FindCallback<ParseObject>() {
                                    @Override
                                    public void done(final List<ParseObject> precios, ParseException e) {
                                        if(e==null)
                                        {
                                            ParseQuery<ParseObject> queryProductos= new ParseQuery<>(Producto.TABLA);
                                            queryProductos.selectKeys(Arrays.asList(Producto.ID,Producto.NOMBREESP,Producto.NOMBREING,Producto.DESCRIPCIONING,Producto.DESCRIPCIONESP,Producto.SUBCATEGORIA));
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
                                                        btnComidas.setVisibility(View.VISIBLE);
                                                        Menu m= navView.getMenu();
                                                        mostrandoMenu(m);
                                                        ParseUser currentUser = ParseUser.getCurrentUser();
                                                        if (currentUser != null) {
                                                            m.getItem(1).setTitle(currentUser.getUsername());
                                                        } else
                                                        {
                                                            m.getItem(1).setVisible(false);
                                                            Menu men=m.getItem(1).getSubMenu();
                                                            men.getItem(0).setVisible(false);
                                                        }
                                                    }
                                                    else
                                                    {
                                                        mostrarMensajeComprobarConexion();
                                                    }

                                                }
                                            });
                                        }
                                        else
                                        {
                                            mostrarMensajeComprobarConexion();
                                        }
                                    }
                                });
                            }
                            else
                            {
                                mostrarMensajeComprobarConexion();
                            }
                        }
                    });
                }
                else
                {
                    mostrarMensajeComprobarConexion();
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

            case R.id.volver_cargar:

                volverAcargar();

            break;

        }
    }

    private void volverAcargar()
    {
        text_compruebe_conexion.setVisibility(View.GONE);
        btnRecargarVista.setVisibility(View.GONE);
        CargarDatosRemotosTask cargarDatosRemotosTask=new CargarDatosRemotosTask();
        cargarDatosRemotosTask.execute();
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
            case R.id.nav_cerrar_sesion:
                cerrarSesion();
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

                int tamano=pager.getAdapter().getCount();

                ProductoFragment prodFrag=(ProductoFragment)getSupportFragmentManager().getFragments().get(posicionactual);
                prodFrag.actualizarData();
                if((posicionactual+1)<tamano)
                {
                    prodFrag=(ProductoFragment)getSupportFragmentManager().getFragments().get(posicionactual+1);
                    prodFrag.actualizarData();
                }

                if((posicionactual-1)>=0)
                {
                    prodFrag=(ProductoFragment)getSupportFragmentManager().getFragments().get(posicionactual-1);
                    prodFrag.actualizarData();
                }

            Menu m=navView.getMenu();
            mostrandoMenu(m);
            ParseUser currentUser = ParseUser.getCurrentUser();
            if (currentUser != null) {
                m.getItem(1).setTitle(currentUser.getUsername());
            } else
            {
                m.getItem(1).setVisible(false);
                Menu men=m.getItem(1).getSubMenu();
                men.getItem(0).setVisible(false);
            }
        }
    }

    class CargarDatosRemotosTask extends AsyncTask<Void, Void, Boolean>
    {


        @Override
        protected Boolean doInBackground(Void... params)
        {
            return hayConexionInternet();
        }

        @Override
        protected void onPostExecute(Boolean resultado) {
            super.onPostExecute(resultado);

            if(resultado)
            {
                cargarDatosParse();
            }
            else
            {
                mostrarMensajeComprobarConexion();
            }

        }
    }

    private void  mostrarMensajeComprobarConexion()
    {
        text_compruebe_conexion.setVisibility(View.VISIBLE);
        btnRecargarVista.setVisibility(View.VISIBLE);
    }

    private boolean hayConexionInternet()
    {
        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(this.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if(activeNetwork!=null)
        {

            return activeNetwork.isConnectedOrConnecting();
        }
        return false;

    }
    private void cerrarSesion()
    {
        pd = ProgressDialog.show(this,"", getResources().getString(R.string.por_favor_espere), true, false);

        CerrarSesionTask cst= new CerrarSesionTask();
        cst.execute();

    }

    private void mostrarMensaje(int idmensaje)
    {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.template_mensaje_toast,
                (ViewGroup) findViewById(R.id.toast_layout));

        TextView text = (TextView) layout.findViewById(R.id.txt_mensaje_toast);
        text.setText(getResources().getString(idmensaje));

        Toast toast = new Toast(this);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

    public class CerrarSesionTask extends  AsyncTask<Void,Void,Void>
    {

        @Override
        protected Void doInBackground(Void... params) {
            ParseUser.logOut();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            super.onPostExecute(aVoid);
            Menu m=navView.getMenu();
            m.getItem(1).setVisible(false);
            Menu men=m.getItem(1).getSubMenu();
            men.getItem(0).setVisible(false);
            mostrarMensaje(R.string.txt_sesion_cerrada);
            if(pd!=null)
            {
                pd.dismiss();
            }
        }
    }


}
