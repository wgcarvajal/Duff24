package duff24.com.duff24;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.BackendlessDataQuery;
import com.backendless.persistence.QueryOptions;
import com.backendless.persistence.local.UserIdStorageFactory;
import com.facebook.login.LoginManager;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.viewpagerindicator.PageIndicator;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import duff24.com.duff24.adaptadores.AdaptadorProductoDisminuir;
import duff24.com.duff24.adaptadores.PagerAdapter;
import duff24.com.duff24.basededatos.AdminSQliteOpenHelper;
import duff24.com.duff24.fragments.AnuncioFragment;
import duff24.com.duff24.fragments.FragmentGeneric;
import duff24.com.duff24.fragments.ProductoFragment;
import duff24.com.duff24.modelo.Producto;
import duff24.com.duff24.modelo.ProductoPersonalizado;
import duff24.com.duff24.modelo.Subcategoria;
import duff24.com.duff24.typeface.CustomTypefaceSpan;
import duff24.com.duff24.util.AppUtil;
import duff24.com.duff24.util.FontCache;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener,
        ProductoFragment.OnComunicationFragment, AdaptadorProductoDisminuir.OnItemClickListener {

    public final static int MI_REQUEST_CODE = 1;

    private ViewPager pager;
    private PageIndicator pagerIndicator;
    private PagerAdapter adapter;
    private List<FragmentGeneric> data= new ArrayList<>();
    private NavigationView navView;
    private DrawerLayout drawer;
    private TextView tituloMenuHeader;
    private TextView text_compruebe_conexion;
    private Button btnRecargarVista;
    private String font_path = "font/KGTenThousandReasonsAlt.ttf";
    private String font_path_ASimple="font/VTKS_ANIMAL_2.ttf";
    private ProgressDialog pd = null;
    private VideoView videofondo;

    private Producto productSelected;
    private TextView btnDisminuirProductSelected;
    private TextView txtConteoProductoSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        videofondo = (VideoView)findViewById(R.id.videofondo);

        text_compruebe_conexion=(TextView)findViewById(R.id.txt_sin_conexion);
        tituloMenuHeader=(TextView)findViewById(R.id.titulo_header_menu);


        btnRecargarVista=(Button)findViewById(R.id.volver_cargar);

        drawer=(DrawerLayout)findViewById(R.id.drawer);
        pager= (ViewPager)findViewById(R.id.pager);
        pager.setPageTransformer(true, new com.ToxicBakery.viewpager.transforms.CubeOutTransformer());
        pagerIndicator=(PageIndicator)findViewById(R.id.pagerIndicator);
        navView = (NavigationView) findViewById(R.id.nav);

        navView.setNavigationItemSelectedListener(this);
        btnRecargarVista.setOnClickListener(this);


        text_compruebe_conexion.setVisibility(View.GONE);
        btnRecargarVista.setVisibility(View.GONE);


        Typeface TF = FontCache.get(font_path_ASimple,this);

        tituloMenuHeader.setTypeface(TF);
        text_compruebe_conexion.setTypeface(TF);
        btnRecargarVista.setTypeface(TF);
        adapter = new PagerAdapter(getSupportFragmentManager(), data);
        pager.setAdapter(adapter);
        pagerIndicator.setViewPager(pager);
        Menu m = navView.getMenu();
        aplicandoTipoLetraItemMenu(m, font_path);
        ocultandoMenu(m);
        loadData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.videofondo.setVideoPath("android.resource://"+getPackageName()+"/"+R.raw.fondo_duff);
        this.videofondo.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
        {
            public void onCompletion(MediaPlayer paramAnonymousMediaPlayer)
            {
                MainActivity.this.videofondo.start();
            }
        });
        this.videofondo.start();
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
                switch (sub.getTipoFragment())
                {
                    case Subcategoria.SINDESCRIPCION:
                    case Subcategoria.CONDESCRIPCION:
                        ProductoFragment productoFragment = new ProductoFragment();
                        productoFragment.init(sub.getObjectId(), sub.getSubcatnombresp());
                        data.add(productoFragment);
                        break;

                    case Subcategoria.ANUNCIO:
                        AnuncioFragment anuncioFragment= new AnuncioFragment();
                        anuncioFragment.init(sub.getSubcatnombre(), sub.getSubcatnombresp(),sub.getObjectId());
                        data.add(anuncioFragment);
                    break;
                }

            }
            adapter.notifyDataSetChanged();
            Menu m=navView.getMenu();
            mostrandoMenu(m);

            BackendlessUser currentUser = Backendless.UserService.CurrentUser();
            if (currentUser == null)
            {
                m.getItem(2).setVisible(false);
                Menu men=m.getItem(2).getSubMenu();
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

        BackendlessDataQuery dataQuerysubcategoria= new BackendlessDataQuery();
        List<String>subcategoriaSelect=new ArrayList<>();
        subcategoriaSelect.add("objectId");
        subcategoriaSelect.add("tipoFragment");
        subcategoriaSelect.add("subcatnombre");
        subcategoriaSelect.add("subcatnombresp");
        subcategoriaSelect.add("domicilio");

        dataQuerysubcategoria.setProperties(subcategoriaSelect);
        QueryOptions queryOptionsSubcategoria= new QueryOptions();
        queryOptionsSubcategoria.setPageSize(100);
        queryOptionsSubcategoria.addSortByOption("posicion ASC");
        dataQuerysubcategoria.setQueryOptions(queryOptionsSubcategoria);
        Backendless.Persistence.of(Subcategoria.class).find(dataQuerysubcategoria, new AsyncCallback<BackendlessCollection<Subcategoria>>() {
            @Override
            public void handleResponse(BackendlessCollection<Subcategoria> response)
            {
                AppUtil.listaSubcategorias= response.getData();
                BackendlessDataQuery dataQueryProductos= new BackendlessDataQuery();
                List<String> productoSelect=new ArrayList<>();
                productoSelect.add("objectId");
                productoSelect.add("precio");
                productoSelect.add("proddescripcion");
                productoSelect.add("proddescripcionesp");
                productoSelect.add("prodnombre");
                productoSelect.add("prodnombreesp");
                productoSelect.add("subcategoria");
                productoSelect.add("imgFile");
                productoSelect.add("imgFileNew");
                productoSelect.add("condescripcion");
                productoSelect.add("personalizable");
                dataQueryProductos.setProperties(productoSelect);
                QueryOptions queryOptionsProductos= new QueryOptions();
                queryOptionsProductos.setPageSize(100);
                queryOptionsProductos.addSortByOption("posicion ASC");
                dataQueryProductos.setQueryOptions(queryOptionsProductos);
                dataQueryProductos.setWhereClause("activo = TRUE");
                Backendless.Persistence.of(Producto.class).find(dataQueryProductos, new AsyncCallback<BackendlessCollection<Producto>>() {
                    @Override
                    public void handleResponse(BackendlessCollection<Producto> prods)
                    {

                        AppUtil.data=prods.getData();
                        if(prods.getTotalObjects()>100)
                        {
                            prods.nextPage(new AsyncCallback<BackendlessCollection<Producto>>() {
                                @Override
                                public void handleResponse(BackendlessCollection<Producto> prodspagdos)
                                {
                                    List<Producto> pds=prodspagdos.getData();
                                    for(Producto p :pds)
                                    {
                                        AppUtil.data.add(p);
                                    }
                                    crearFragments();

                                }

                                @Override
                                public void handleFault(BackendlessFault fault)
                                {
                                    AppUtil.data= new ArrayList<>();
                                    mostrarMensajeComprobarConexion();

                                }
                            });


                        }
                        else
                        {
                            crearFragments();
                        }
                    }

                    @Override
                    public void handleFault(BackendlessFault fault)
                    {
                        mostrarMensajeComprobarConexion();
                    }
                });
            }

            @Override
            public void handleFault(BackendlessFault fault)
            {
                mostrarMensajeComprobarConexion();
            }
        });
    }


    private void crearFragments()
    {
        final Menu m = navView.getMenu();
        mostrandoMenu(m);

        Backendless.UserService.isValidLogin(new AsyncCallback<Boolean>() {
            @Override
            public void handleResponse(Boolean response) {

                if (response) {
                    String currentUserObjectId = UserIdStorageFactory.instance().getStorage().get();
                    Backendless.Data.of(BackendlessUser.class).findById(currentUserObjectId, new AsyncCallback<BackendlessUser>() {
                        @Override
                        public void handleResponse(BackendlessUser response)
                        {
                            Backendless.UserService.setCurrentUser(response);
                        }

                        @Override
                        public void handleFault(BackendlessFault fault)
                        {
                            m.getItem(2).setVisible(false);
                            Menu men = m.getItem(2).getSubMenu();
                            men.getItem(0).setVisible(false);


                        }
                    });
                } else {
                    m.getItem(2).setVisible(false);
                    Menu men = m.getItem(2).getSubMenu();
                    men.getItem(0).setVisible(false);

                }
            }
            @Override
            public void handleFault(BackendlessFault fault)
            {
                m.getItem(2).setVisible(false);
                Menu men = m.getItem(2).getSubMenu();
                men.getItem(0).setVisible(false);

            }
        });


        for (Subcategoria sub : AppUtil.listaSubcategorias)
        {
            switch (sub.getTipoFragment())
            {
                case Subcategoria.SINDESCRIPCION:
                case Subcategoria.CONDESCRIPCION:
                    ProductoFragment productoFragment = new ProductoFragment();
                    productoFragment.init(sub.getObjectId(), sub.getSubcatnombresp());
                    data.add(productoFragment);
                    break;


                case Subcategoria.ANUNCIO:
                    AnuncioFragment anuncioFragment= new AnuncioFragment();
                    anuncioFragment.init(sub.getSubcatnombre(), sub.getSubcatnombresp(),sub.getObjectId());
                    data.add(anuncioFragment);
                    break;
            }

        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
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
        Typeface font = FontCache.get(rutaTipoLetra,this);
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
            case R.id.nav_contacto_sugerencias:
                intent = new Intent(this,ContactoActivity.class);
                startActivity(intent);
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

        /*VideoView videoView = (VideoView) findViewById(R.id.videofondo);
        videoView.start();*/

        if (requestCode == MI_REQUEST_CODE)
        {
            int  tamano= pager.getAdapter().getCount();
            int posicionActual=pager.getCurrentItem();
            FragmentGeneric frag=(FragmentGeneric)((PagerAdapter)pager.getAdapter()).getItem(posicionActual);

            if(frag!=null)
            {

                frag.actualizarData();
                if((posicionActual+1)<tamano)
                {
                    frag=(FragmentGeneric)((PagerAdapter)pager.getAdapter()).getItem(posicionActual+1);
                    frag.actualizarData();
                }
                if((posicionActual-1)>=0)
                {
                    frag=(FragmentGeneric)((PagerAdapter)pager.getAdapter()).getItem(posicionActual - 1);
                    frag.actualizarData();
                }

            }

            Menu m=navView.getMenu();
            mostrandoMenu(m);
            BackendlessUser currentUser = Backendless.UserService.CurrentUser();
            if (currentUser == null)
            {
                m.getItem(2).setVisible(false);
                Menu men=m.getItem(2).getSubMenu();
                men.getItem(0).setVisible(false);
            }
        }
    }

    @Override
    public void itemClickDisminuirDialogDisminuir(String sinIngredientes)
    {

        disminuirProducto(productSelected,btnDisminuirProductSelected,txtConteoProductoSelected,sinIngredientes);
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

            Log.i("hay conexion:",""+resultado);
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

        Backendless.UserService.logout(new AsyncCallback<Void>() {
            @Override
            public void handleResponse(Void response) {

                LoginManager.getInstance().logOut();
                Menu m = navView.getMenu();
                m.getItem(2).setVisible(false);
                Menu men = m.getItem(2).getSubMenu();
                men.getItem(0).setVisible(false);
                mostrarMensaje(R.string.txt_sesion_cerrada);
                if (pd != null) {
                    pd.dismiss();
                }


            }

            @Override
            public void handleFault(BackendlessFault fault) {
                if (pd != null) {
                    pd.dismiss();
                }
                mostrarMensaje(R.string.compruebe_conexion);
            }
        });

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

    @Override
    public void onIrAlPedidoFragment()
    {
        Intent intent = new Intent(this,PedidoActivity.class);
        startActivityForResult(intent, MI_REQUEST_CODE);
    }

    @Override
    public void onAbrirMenuPrincipalFragment()
    {
        drawer.openDrawer(GravityCompat.START);
    }


    @Override
    public void onAbrirDescripcionProducto(Producto producto,TextView btnDisminuir , TextView txtconteo)
    {
        if(producto.isCondescripcion())
        {
            productSelected = producto;
            btnDisminuirProductSelected = btnDisminuir;
            txtConteoProductoSelected = txtconteo;

            abrirDialogDescripcionProducto();
        }
        else
        {
            aumentarProducto(producto,btnDisminuir,txtconteo,"","");
        }

    }

    @Override
    public void onAbrirDisminuirProducto(Producto producto, TextView btnDisminuir, TextView txtconteo)
    {
        if(producto.isCondescripcion() && producto.isPersonalizable())
        {
            productSelected = producto;
            btnDisminuirProductSelected = btnDisminuir;
            txtConteoProductoSelected = txtconteo;
            abrirDialogDisminuirProducto();
        }
        else
        {
            disminuirProducto(producto,btnDisminuir,txtconteo,"");
        }

    }

    private void abrirDialogDisminuirProducto()
    {
        AdminSQliteOpenHelper admin = new AdminSQliteOpenHelper(this,"admin",null,AdminSQliteOpenHelper.v);
        SQLiteDatabase db = admin.getWritableDatabase();
        Cursor fila = db.rawQuery("select prodid,prodprecio, prodcantidad , prodsiningredientes, prodsiningredientesing from pedido where prodid = '" + productSelected.getObjectId() + "'", null);
        if(fila.getCount()>1)
        {
            final Dialog dialog= new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.template_dialog_disminuir_producto);
            dialog.getWindow().setBackgroundDrawableResource(R.drawable.bordes_redondos_dialog_descripcion_producto);

            Button btn_cerrar_dialog_disminuir = (Button)dialog.findViewById(R.id.btn_cerrar_dialog_disminuir);
            TextView nombre_producto_dialog_disminuir = (TextView)dialog.findViewById(R.id.txt_nombre_producto_dialog_disminuir);
            RecyclerView listProductos = (RecyclerView) dialog.findViewById(R.id.list_producto_personalizado);

            List<ProductoPersonalizado> listaProductosPersonalizados = new ArrayList<>();

            if(fila.moveToFirst())
            {
                ProductoPersonalizado productoPersonalizado = new ProductoPersonalizado();
                productoPersonalizado.setProdid(fila.getString(0));
                productoPersonalizado.setProdprecio(fila.getInt(1));
                productoPersonalizado.setProdcantidad(fila.getInt(2));
                productoPersonalizado.setProdsiningredientes(fila.getString(3));
                productoPersonalizado.setProdsiningredientesIng(fila.getString(4));
                listaProductosPersonalizados.add(productoPersonalizado);

                while (fila.moveToNext())
                {
                    productoPersonalizado = new ProductoPersonalizado();
                    productoPersonalizado.setProdid(fila.getString(0));
                    productoPersonalizado.setProdprecio(fila.getInt(1));
                    productoPersonalizado.setProdcantidad(fila.getInt(2));
                    productoPersonalizado.setProdsiningredientes(fila.getString(3));
                    productoPersonalizado.setProdsiningredientesIng(fila.getString(4));
                    listaProductosPersonalizados.add(productoPersonalizado);
                }
            }

            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
            listProductos.setLayoutManager(layoutManager);
            AdaptadorProductoDisminuir adaptadorProductoDisminuir= new AdaptadorProductoDisminuir(this,listaProductosPersonalizados,this);
            SpacesItemDecoration spacesItemDecoration = new SpacesItemDecoration(getResources().getDimensionPixelSize(R.dimen.margin_item_grid_left_right),getResources().getDimensionPixelSize(R.dimen.margin_item_grid_bottom));
            listProductos.setAdapter(adaptadorProductoDisminuir);
            listProductos.addItemDecoration(spacesItemDecoration);


            String idioma = getResources().getString(R.string.idioma);

            if(idioma.equals("es"))
            {
                nombre_producto_dialog_disminuir.setText(productSelected.getProdnombreesp());

            }
            else
            {
                nombre_producto_dialog_disminuir.setText(productSelected.getProdnombre());

            }

            Typeface TF = FontCache.get(font_path_ASimple,this);
            nombre_producto_dialog_disminuir.setTypeface(TF);
            btn_cerrar_dialog_disminuir.setTypeface(TF);

            btn_cerrar_dialog_disminuir.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.hide();
                }
            });

            dialog.show();
        }
        else
        {
            fila.moveToFirst();
            disminuirProducto(productSelected,btnDisminuirProductSelected,txtConteoProductoSelected,fila.getString(3));
        }
        db.close();
    }

    private void abrirDialogDescripcionProducto()
    {
        final Dialog dialog= new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        if(productSelected.isPersonalizable())
        {
            dialog.setContentView(R.layout.template_dialog_descripcion_producto);
        }
        else
        {
            dialog.setContentView(R.layout.template_dialog_descripcion_producto_2);
        }

        dialog.getWindow().setBackgroundDrawableResource(R.drawable.bordes_redondos_dialog_descripcion_producto);

        TextView nombre_producto_dialog_descripcion = (TextView)dialog.findViewById(R.id.txt_nombre_producto_dialog_descripcion);
        TextView nombre_descripcion_dialog_descripcion = (TextView)dialog.findViewById(R.id.txt_descripcion_producto_dialog_descripcion);
        TextView precio_dialog_descripcion = (TextView)dialog.findViewById(R.id.txt_precio_dialog_descripcion);
        Button btnagregarProducto_dialog_descripcion = (Button)dialog.findViewById(R.id.btn_agregar_producto_dialog_descripcion);
        ImageView imagen_producto_dialog_descripcion = (ImageView) dialog.findViewById(R.id.img_producto_dialog_descripcion);
        final CheckBox sincebolla;
        final CheckBox sintomate;
        final CheckBox sinsalsas;
        final ImageView placeholder = (ImageView) dialog.findViewById(R.id.placeholder_dialog_descripcion);

        placeholder.setVisibility(View.VISIBLE);
        placeholder.setImageResource(R.drawable.foodgif);

        String idioma = getResources().getString(R.string.idioma);

        if(idioma.equals("es"))
        {
            nombre_producto_dialog_descripcion.setText(productSelected.getProdnombreesp());
            nombre_descripcion_dialog_descripcion.setText(productSelected.getProddescripcionesp());
        }
        else
        {
            nombre_producto_dialog_descripcion.setText(productSelected.getProdnombre());
            nombre_descripcion_dialog_descripcion.setText(productSelected.getProddescripcion());
        }
        DecimalFormat format= new DecimalFormat("###,###.##");
        String precio=format.format(productSelected.getPrecio());
        precio = precio.replace(",",".");
        precio_dialog_descripcion.setText("$"+precio);
        Typeface TF = FontCache.get(font_path_ASimple,this);
        nombre_producto_dialog_descripcion.setTypeface(TF);
        precio_dialog_descripcion.setTypeface(TF);
        btnagregarProducto_dialog_descripcion.setTypeface(TF);
        TF = FontCache.get(font_path,this);
        nombre_descripcion_dialog_descripcion.setTypeface(TF);
        if(productSelected.isPersonalizable())
        {
            TF = FontCache.get(font_path_ASimple,this);
            TextView sin_dialog_descripcion = (TextView)dialog.findViewById(R.id.txt_sin_dialog_descripcion);
            ScrollView scrollpersonalizable = (ScrollView) dialog.findViewById(R.id.scroll_personalizable);
            sin_dialog_descripcion.setTypeface(TF);
            sincebolla = (CheckBox)dialog.findViewById(R.id.check_cebolla);
            sintomate = (CheckBox)dialog.findViewById(R.id.check_tomate);
            sinsalsas = (CheckBox)dialog.findViewById(R.id.check_salsas);
            sincebolla.setTypeface(TF);
            sintomate.setTypeface(TF);
            sinsalsas.setTypeface(TF);
            btnagregarProducto_dialog_descripcion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    String sinIngredientes ="";
                    String sinIngredientesIng ="";
                    if(sincebolla.isChecked())
                    {
                        sinIngredientes = "cebolla";
                        sinIngredientesIng = "onion";
                    }
                    if(sintomate.isChecked())
                    {
                        if(sinIngredientes.equals(""))
                        {
                            sinIngredientes = "tomate";
                            sinIngredientesIng = "tomato";
                        }
                        else
                        {
                            sinIngredientes = sinIngredientes +", tomate";
                            sinIngredientesIng = sinIngredientesIng +", tomato";
                        }
                    }

                    if(sinsalsas.isChecked())
                    {
                        if(sinIngredientes.equals(""))
                        {
                            sinIngredientes = "salsas";
                            sinIngredientesIng = "sauces";
                        }
                        else
                        {
                            sinIngredientes = sinIngredientes +", salsas";
                            sinIngredientesIng = sinIngredientesIng +", sauces";
                        }
                    }
                    aumentarProducto(productSelected,btnDisminuirProductSelected,txtConteoProductoSelected,sinIngredientes,sinIngredientesIng);
                    dialog.hide();
                }
            });
        }
        else
        {
            btnagregarProducto_dialog_descripcion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    String sinIngredientes ="";
                    String sinIngredientesIng ="";
                    aumentarProducto(productSelected,btnDisminuirProductSelected,txtConteoProductoSelected,sinIngredientes,sinIngredientesIng);
                    dialog.hide();
                }
            });
        }

        Picasso.with(this)
                .load(productSelected.getImgFile())
                .into(imagen_producto_dialog_descripcion, new Callback() {
                    @Override
                    public void onSuccess() {
                        placeholder.setImageDrawable(null);
                        placeholder.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {

                    }
                });

        dialog.show();
    }
    private void aumentarProducto(Producto producto, TextView btndisminuir, TextView txtconteo, String sinIngredientes , String sinIngredientesIng)
    {
        MediaPlayer m = MediaPlayer.create(this,R.raw.sonido_click);
        m.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                mp.release();
            }
        });
        m.start();
        AdminSQliteOpenHelper admin = new AdminSQliteOpenHelper(this,"admin",null,AdminSQliteOpenHelper.v);
        SQLiteDatabase db = admin.getWritableDatabase();
        Cursor fila = db.rawQuery("select prodcantidad , prodsiningredientes from pedido where prodid = '" + producto.getObjectId() + "'", null);
        ContentValues registroPedido= new ContentValues();
        int conteoTotal=1;
        if(fila.moveToFirst())
        {
            int conteo = 0;
            conteoTotal=fila.getInt(0)+1;

            if(fila.getString(1).equals(sinIngredientes))
            {
                conteo = fila.getInt(0) +1;
            }

            while(fila.moveToNext())
            {
                if(fila.getString(1).equals(sinIngredientes))
                {
                    conteo = fila.getInt(0) +1;
                }
                conteoTotal = conteoTotal + fila.getInt(0);
            }


            if(conteo > 0)
            {
                registroPedido.put("prodcantidad",conteo);
                db.update("pedido",registroPedido,"prodid = '"+producto.getObjectId()+"' AND prodsiningredientes = '"+sinIngredientes+"'",null);
            }
            else
            {
                conteo = 1;
                registroPedido.put("prodid",producto.getObjectId());
                registroPedido.put("prodprecio",producto.getPrecio());
                registroPedido.put("prodnombreing",producto.getProdnombre());
                registroPedido.put("prodnombreesp",producto.getProdnombreesp());
                registroPedido.put("proddescripcioning",producto.getProddescripcion());
                registroPedido.put("proddescripcionesp",producto.getProddescripcionesp());
                registroPedido.put("prodcantidad",conteo);
                registroPedido.put("prodsiningredientes",sinIngredientes);
                registroPedido.put("prodsiningredientesing",sinIngredientesIng);
                db.insert("pedido",null,registroPedido);
            }
        }
        else
        {
            registroPedido.put("prodid",producto.getObjectId());
            registroPedido.put("prodprecio",producto.getPrecio());
            registroPedido.put("prodnombreing",producto.getProdnombre());
            registroPedido.put("prodnombreesp",producto.getProdnombreesp());
            registroPedido.put("proddescripcioning",producto.getProddescripcion());
            registroPedido.put("proddescripcionesp",producto.getProddescripcionesp());
            registroPedido.put("prodcantidad",conteoTotal);
            registroPedido.put("prodsiningredientes",sinIngredientes);
            registroPedido.put("prodsiningredientesing",sinIngredientesIng);
            db.insert("pedido",null,registroPedido);
        }
        db.close();
        txtconteo.setText("" + conteoTotal);
        txtconteo.setVisibility(View.VISIBLE);
        btndisminuir.setVisibility(View.VISIBLE);
    }
    private void disminuirProducto(Producto producto,TextView txtdisminuir, TextView txtconteo, String sinIngredientes)
    {
        int cantidad=0;
        int cantidadProductoPersonalizado =0;
        MediaPlayer m = MediaPlayer.create(this, R.raw.sonido_click);
        m.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                mp.release();
            }
        });
        m.start();

        AdminSQliteOpenHelper admin = new AdminSQliteOpenHelper(this,"admin",null,AdminSQliteOpenHelper.v);
        SQLiteDatabase db = admin.getWritableDatabase();
        Cursor fila = db.rawQuery("select prodcantidad , prodsiningredientes from pedido where prodid = '" + producto.getObjectId() + "'", null);
        if(fila.moveToFirst())
        {
            cantidad=fila.getInt(0);
            if(fila.getString(1).equals(sinIngredientes))
            {
                cantidadProductoPersonalizado = fila.getInt(0) -1;
            }

            while (fila.moveToNext())
            {
                cantidad = cantidad +fila.getInt(0);

                if(fila.getString(1).equals(sinIngredientes))
                {
                    cantidadProductoPersonalizado = fila.getInt(0) -1;
                }
            }

            cantidad = cantidad -1;

            if(cantidadProductoPersonalizado==0)
            {
                db.delete("pedido", "prodid ='" + producto.getObjectId() + "' AND prodsiningredientes = '"+sinIngredientes+"'", null);
            }
            else
            {
                ContentValues registroPedido= new ContentValues();
                registroPedido.put("prodcantidad",cantidadProductoPersonalizado);
                db.update("pedido", registroPedido, "prodid = '" + producto.getObjectId() + "' AND prodsiningredientes = '"+sinIngredientes+"'", null);
            }
        }
        db.close();

        if(cantidad==0)
        {
            txtconteo.setVisibility(View.GONE);
            txtconteo.setText(0 + "");
            txtdisminuir.setVisibility(View.GONE);
        }
        else
        {
            txtconteo.setText(cantidad + "");
        }
    }
}
