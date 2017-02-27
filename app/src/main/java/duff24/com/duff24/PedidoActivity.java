package duff24.com.duff24;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.facebook.login.LoginManager;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import duff24.com.duff24.adaptadores.AdaptadorProductoPedido;
import duff24.com.duff24.basededatos.AdminSQliteOpenHelper;
import duff24.com.duff24.modelo.Producto;
import duff24.com.duff24.modelo.ProductoPersonalizado;
import duff24.com.duff24.typeface.CustomTypefaceSpan;
import duff24.com.duff24.util.AppUtil;
import duff24.com.duff24.util.FontCache;

public class PedidoActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener, AdapterView.OnItemClickListener, AdaptadorProductoPedido.OnItemClickListener {

    public final static int MI_REQUEST_CODE = 1;
    public final static int MI_REQUEST_CODE_REGISTRADO = 2;
    public final static int MI_REQUEST_SE_LOGUIO_USUARIO=101;

    private String font_path = "font/KGTenThousandReasonsAlt.ttf";
    private String font_path_ASimple="font/VTKS_ANIMAL_2.ttf";

    private TextView titulo;
    private TextView tituloMenuHeader;
    private TextView textTotalPedido;
    private TextView textDomicilio;
    private  TextView textValorTotalPedido;
    private ImageView btnMenuPrincipal;
    private DrawerLayout drawer;
    private NavigationView navView;
    private Button btnFinalizarPedido;
    private RecyclerView gridProductosPedido;

    private RecyclerView.LayoutManager layoutManager;
    private ImageView flechaAtras;
    private AdaptadorProductoPedido adapter;
    private List<ProductoPersonalizado> data= new ArrayList<>();
    private ProgressDialog pd = null;
    private VideoView videofondo;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedido);

        videofondo = (VideoView)findViewById(R.id.videofondo);
        titulo = (TextView)findViewById(R.id.txttitulo);
        tituloMenuHeader=(TextView)findViewById(R.id.titulo_header_menu);
        textTotalPedido=(TextView)findViewById(R.id.txt_total_pedido);
        textValorTotalPedido=(TextView)findViewById(R.id.txt_valor_total_pedido);
        textDomicilio=(TextView)findViewById(R.id.txt_domicilio);

        btnMenuPrincipal=(ImageView)findViewById(R.id.btn_menu_principal);
        btnFinalizarPedido=(Button)findViewById(R.id.btn_finalizar_pedido);
        flechaAtras=(ImageView)findViewById(R.id.flecha_atras);

        gridProductosPedido=(RecyclerView) findViewById(R.id.grid_productos_pedido);

        drawer=(DrawerLayout)findViewById(R.id.drawer);
        navView = (NavigationView) findViewById(R.id.nav);

        btnMenuPrincipal.setOnClickListener(this);
        btnFinalizarPedido.setOnClickListener(this);
        navView.setNavigationItemSelectedListener(this);
        flechaAtras.setOnClickListener(this);
        Menu m = navView.getMenu();

        BackendlessUser currentUser = Backendless.UserService.CurrentUser();

        if(!hayProductos())
        {
            m.getItem(1).setVisible(false);
            btnFinalizarPedido.setVisibility(View.GONE);
            if(currentUser !=null)
            {
                flechaAtras.setVisibility(View.GONE);
            }
            else
            {
                btnMenuPrincipal.setVisibility(View.GONE);
                m.getItem(2).setVisible(false);
                MenuItem mI= m.getItem(2);
                Menu submenu= mI.getSubMenu();
                submenu.getItem(0).setVisible(false);
            }

        }
        else
        {
            flechaAtras.setVisibility(View.GONE);
            if(currentUser==null)
            {
                m.getItem(2).setVisible(false);
                MenuItem mI= m.getItem(2);
                Menu submenu= mI.getSubMenu();
                submenu.getItem(0).setVisible(false);
            }
        }

        aplicandoTipoLetraItemMenu(m, font_path);

        Typeface TF = FontCache.get(font_path_ASimple,this);
        titulo.setTypeface(TF);
        tituloMenuHeader.setTypeface(TF);
        btnFinalizarPedido.setTypeface(TF);
        TF = FontCache.get(font_path,this);
        textDomicilio.setTypeface(TF);

        layoutManager = new LinearLayoutManager(this);
        gridProductosPedido.setLayoutManager(layoutManager);
        adapter= new AdaptadorProductoPedido(this,data, this);
        SpacesItemDecoration spacesItemDecoration = new SpacesItemDecoration(getResources().getDimensionPixelSize(R.dimen.margin_item_grid_left_right),getResources().getDimensionPixelSize(R.dimen.margin_item_grid_bottom));
        gridProductosPedido.setAdapter(adapter);
        gridProductosPedido.addItemDecoration(spacesItemDecoration);


        DecimalFormat format= new DecimalFormat("###,###.##");
        textDomicilio.setText(getResources().getString(R.string.domicilio_incluido) + " $" + format.format(AppUtil.listaSubcategorias.get(0).getDomicilio()).replace(",", ".") + ")");
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
                PedidoActivity.this.videofondo.start();
            }
        });
        this.videofondo.start();
    }

    private void loadData()
    {
        AdminSQliteOpenHelper admin = AdminSQliteOpenHelper.crearSQLite(this);
        SQLiteDatabase db = admin.getReadableDatabase();

        Cursor fila = db.rawQuery("select prodid,prodprecio, prodcantidad , prodsiningredientes ,prodsiningredientesing from pedido", null);
        if (fila != null) {

            if (fila.moveToFirst())
            {

                int contador=AppUtil.listaSubcategorias.get(0).getDomicilio();
                do {

                    for(Producto p: AppUtil.data)
                    {
                        if(p.getObjectId().equals(fila.getString(fila.getColumnIndex("prodid"))))
                        {
                            ProductoPersonalizado productoPersonalizado = new ProductoPersonalizado();
                            productoPersonalizado.setProdid(fila.getString(0));
                            productoPersonalizado.setProdprecio(p.getPrecio());
                            productoPersonalizado.setNombreEsp(p.getProdnombreesp());
                            productoPersonalizado.setNombreIng(p.getProdnombre());
                            productoPersonalizado.setDescripcionEsp(p.getProddescripcionesp());
                            productoPersonalizado.setDescripcionIng(p.getProddescripcion());
                            productoPersonalizado.setProdcantidad(fila.getInt(2));
                            productoPersonalizado.setProdsiningredientes(fila.getString(3));
                            productoPersonalizado.setProdsiningredientesIng(fila.getString(4));
                            productoPersonalizado.setImgFile(p.getImgFile());
                            data.add(productoPersonalizado);
                            contador=contador+(fila.getInt(fila.getColumnIndex("prodcantidad"))*p.getPrecio());

                            break;
                        }
                    }

                } while (fila.moveToNext());
                DecimalFormat format= new DecimalFormat("###,###.##");
                adapter.notifyDataSetChanged();
                String valorTotal=format.format(contador);
                valorTotal=valorTotal.replace(",",".");
                textValorTotalPedido.setText("$"+valorTotal);

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

            case R.id.btn_finalizar_pedido:
                finalizarPedido();
            break;

            case R.id.flecha_atras:
                Intent intent = new Intent();
                setResult(RESULT_OK,intent);
                finish();
            break;
        }
    }

    private void finalizarPedido()
    {
        String substring=textValorTotalPedido.getText().toString().substring(1);
        substring =substring.replace(".","");
        int total= Integer.parseInt(substring);
        if(total<10000)
        {
            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.template_mensaje_toast,
                    (ViewGroup) findViewById(R.id.toast_layout));

            TextView text = (TextView) layout.findViewById(R.id.txt_mensaje_toast);
            text.setText(getResources().getString(R.string.mensaje_pedido_minimo));

            Toast toast = new Toast(this);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(layout);
            toast.show();
        }
        else
        {
            BackendlessUser currentUser = Backendless.UserService.CurrentUser();
            if (currentUser != null)
            {
                Intent intent = new Intent(this,RegistradoActivity.class);
                startActivityForResult(intent, MI_REQUEST_CODE);
            }
            else
            {
                Intent intent = new Intent(this,LoginActivity.class);
                startActivityForResult(intent,MI_REQUEST_CODE);
            }

        }

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
        switch (menuItem.getItemId())
        {
            case R.id.nav_la_carta:

                Intent intent = new Intent();
                setResult(RESULT_OK,intent);
                finish();
            break;
            case R.id.nav_vaciar_pedido:
                vaciarPedido();
            break;
            case R.id.nav_cerrar_sesion:
                cerrarSesion();
            break;

        }
        drawer.closeDrawers();
        return false;

    }

    private void cerrarSesion()
    {

        pd = ProgressDialog.show(this, "", getResources().getString(R.string.por_favor_espere), true, false);

        Backendless.UserService.logout(new AsyncCallback<Void>() {
            @Override
            public void handleResponse(Void response)
            {
                LoginManager.getInstance().logOut();
                Menu m=navView.getMenu();
                m.getItem(2).setVisible(false);
                Menu men=m.getItem(2).getSubMenu();
                men.getItem(0).setVisible(false);
                mostrarMensaje(R.string.txt_sesion_cerrada);
                if(!m.getItem(1).isVisible())
                {
                    btnMenuPrincipal.setVisibility(View.GONE);
                    flechaAtras.setVisibility(View.VISIBLE);
                }

                if(pd!=null)
                {
                    pd.dismiss();
                }

            }

            @Override
            public void handleFault(BackendlessFault fault) {
                pd.dismiss();
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        MediaPlayer m = MediaPlayer.create(getApplicationContext(), R.raw.sonido_click);
        m.start();
        String valorTotalpedido=textValorTotalPedido.getText().toString();
        String substring = valorTotalpedido.substring(1);
        substring =substring.replace(".","");
        int contador= Integer.parseInt(substring);
        contador= contador+ data.get(position).getProdprecio();

        DecimalFormat format= new DecimalFormat("###,###.##");
        String valorTotal=format.format(contador);
        valorTotal=valorTotal.replace(",",".");
        textValorTotalPedido.setText("$"+valorTotal);

        TextView textconteo= (TextView) view.findViewById(R.id.txtconteo);
        int conteo = Integer.parseInt(textconteo.getText().toString());
        conteo= conteo+1;
        textconteo.setText(conteo+"");

        AdminSQliteOpenHelper admin = AdminSQliteOpenHelper.crearSQLite(this);
        SQLiteDatabase db = admin.getWritableDatabase();

        ContentValues registroPedido= new ContentValues();
        registroPedido.put("prodcantidad",conteo);

        int cant= db.update("pedido",registroPedido,"prodid = '"+data.get(position).getProdid()+"'",null);

        db.close();

    }


    public void disminuirTotal(int precio)
    {
        String valorTotalpedido=textValorTotalPedido.getText().toString();
        String substring = valorTotalpedido.substring(1);
        substring =substring.replace(".","");
        int contador= Integer.parseInt(substring)-precio;
        if(contador==AppUtil.listaSubcategorias.get(0).getDomicilio())
        {
            contador=0;
            Menu m = navView.getMenu();
            m.getItem(1).setVisible(false);
            btnFinalizarPedido.setVisibility(View.GONE);
            btnMenuPrincipal.setVisibility(View.GONE);
            flechaAtras.setVisibility(View.VISIBLE);

        }
        DecimalFormat format= new DecimalFormat("###,###.##");
        String valorTotal=format.format(contador);
        valorTotal=valorTotal.replace(",",".");
        textValorTotalPedido.setText("$" + valorTotal);
    }

    public void aumentarTotal(int precio)
    {
        String valorTotalpedido=textValorTotalPedido.getText().toString();
        String substring = valorTotalpedido.substring(1);
        substring =substring.replace(".","");
        int contador= Integer.parseInt(substring)+precio;
        DecimalFormat format= new DecimalFormat("###,###.##");
        String valorTotal=format.format(contador);
        valorTotal=valorTotal.replace(",",".");
        textValorTotalPedido.setText("$" + valorTotal);
    }

    private void vaciarPedido()
    {
        final Dialog dialog= new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.template_dialog_vaciar_pedido);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.bordes_redondos_fondo_negro);


        Button btnAceptar=(Button)dialog.findViewById(R.id.btn_aceptar);
        Button btnCancelar=(Button)dialog.findViewById(R.id.btn_cancelar);
        TextView mensaje =(TextView)dialog.findViewById(R.id.txtmensaje);

        final Context context=this;
        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                AdminSQliteOpenHelper admin = AdminSQliteOpenHelper.crearSQLite(context);
                SQLiteDatabase db = admin.getWritableDatabase();

                db.delete("pedido", "", null);

                textValorTotalPedido.setText("$0");

                data.removeAll(data);
                adapter.notifyDataSetChanged();
                db.close();
                dialog.dismiss();

                LayoutInflater inflater = getLayoutInflater();
                View layout = inflater.inflate(R.layout.template_mensaje_toast,
                        (ViewGroup) findViewById(R.id.toast_layout));

                TextView text = (TextView) layout.findViewById(R.id.txt_mensaje_toast);
                text.setText(context.getResources().getString(R.string.mensaje_pedido_vacio));

                Toast toast = new Toast(context);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.setView(layout);
                toast.show();
                Menu m = navView.getMenu();
                mostrandoMenu(m);
                m.getItem(1).setVisible(false);
                btnFinalizarPedido.setVisibility(View.GONE);
                BackendlessUser currentUser = Backendless.UserService.CurrentUser();
                if(currentUser==null)
                {
                    flechaAtras.setVisibility(View.VISIBLE);
                    btnMenuPrincipal.setVisibility(View.GONE);
                    m.getItem(2).setVisible(false);
                    MenuItem mI=m.getItem(2);
                    Menu submenu=mI.getSubMenu();
                    submenu.getItem(0).setVisible(false);
                }
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private boolean hayProductos()
    {
        AdminSQliteOpenHelper admin = AdminSQliteOpenHelper.crearSQLite(this);
        SQLiteDatabase db = admin.getReadableDatabase();

        Cursor fila = db.rawQuery("select prodid from pedido ", null);
        if(fila.moveToFirst())
        {
            db.close();
            return true;
        }
        db.close();
        return false;
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("requestcode:", " " + requestCode);
        if (requestCode == MI_REQUEST_CODE) {

            if (resultCode == RESULT_OK)
            {
                vaciarPedidoDespuesDeEnviar();
                mostrarDialogConfirmarPedido();
            }
            else
            {
                if(resultCode==MI_REQUEST_SE_LOGUIO_USUARIO)
                {
                    Menu m = navView.getMenu();
                    mostrandoMenu(m);
                    Intent intent = new Intent(this,RegistradoActivity.class);
                    startActivityForResult(intent, MI_REQUEST_CODE_REGISTRADO);
                }
            }
        }
        if(requestCode==MI_REQUEST_CODE_REGISTRADO)
        {
            if(resultCode==RESULT_OK)
            {
                vaciarPedidoDespuesDeEnviar();
                mostrarDialogConfirmarPedido();
            }
        }
    }


    private void vaciarPedidoDespuesDeEnviar()
    {
        AdminSQliteOpenHelper admin = AdminSQliteOpenHelper.crearSQLite(this);
        SQLiteDatabase db = admin.getWritableDatabase();

        db.delete("pedido", "", null);

        textValorTotalPedido.setText("$0");

        data.removeAll(data);
        adapter.notifyDataSetChanged();
        db.close();

        Menu m = navView.getMenu();
        m.getItem(1).setVisible(false);
        btnFinalizarPedido.setVisibility(View.GONE);

        BackendlessUser currentUser = Backendless.UserService.CurrentUser();
        if(currentUser==null)
        {
            flechaAtras.setVisibility(View.VISIBLE);
            btnMenuPrincipal.setVisibility(View.GONE);
            m.getItem(2).setVisible(false);
            MenuItem mI=m.getItem(2);
            Menu submenu=mI.getSubMenu();
            submenu.getItem(0).setVisible(false);
        }
    }

    private void mostrarDialogConfirmarPedido()
    {
        final Dialog dialog= new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.template_dialog_confirmacion_envio);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.bordes_redondos);

        Button btnAceptar=(Button)dialog.findViewById(R.id.btn_aceptar);
        TextView mensaje =(TextView) dialog.findViewById(R.id.txtmensaje);
        Typeface TF = FontCache.get(font_path_ASimple,this);

        mensaje.setTypeface(TF);
        btnAceptar.setTypeface(TF);

        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                dialog.dismiss();
                finish();
            }
        });
        dialog.show();
    }

    @Override
    public void aumentarProducto(ProductoPersonalizado productoPersonalizado, TextView btnDisminuir, TextView txtConteo)
    {
        MediaPlayer m = MediaPlayer.create(this, R.raw.sonido_click);
        m.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                mp.release();
            }
        });
        m.start();
        int cantidad = productoPersonalizado.getProdcantidad();
        cantidad = cantidad +1;
        productoPersonalizado.setProdcantidad(cantidad);
        txtConteo.setText(cantidad+"");
        aumentarTotal(productoPersonalizado.getProdprecio());

        AdminSQliteOpenHelper admin = new AdminSQliteOpenHelper(this,"admin",null,AdminSQliteOpenHelper.v);
        SQLiteDatabase db = admin.getWritableDatabase();
        ContentValues registroPedido= new ContentValues();
        registroPedido.put("prodcantidad",cantidad);
        db.update("pedido", registroPedido, "prodid = '" + productoPersonalizado.getProdid() + "' AND prodsiningredientes = '"+productoPersonalizado.getProdsiningredientes()+"'", null);
        db.close();
    }

    @Override
    public void disminurProducto(ProductoPersonalizado productoPersonalizado, TextView btnDisminuir, TextView txtConteo)
    {
        MediaPlayer m = MediaPlayer.create(this, R.raw.sonido_click);
        m.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                mp.release();
            }
        });
        m.start();
        int cantidad = productoPersonalizado.getProdcantidad();
        cantidad = cantidad -1;

        AdminSQliteOpenHelper admin = new AdminSQliteOpenHelper(this,"admin",null,AdminSQliteOpenHelper.v);
        SQLiteDatabase db = admin.getWritableDatabase();

        if(cantidad>0)
        {
            productoPersonalizado.setProdcantidad(cantidad);
            txtConteo.setText(cantidad+"");

            ContentValues registroPedido= new ContentValues();
            registroPedido.put("prodcantidad",cantidad);
            db.update("pedido", registroPedido, "prodid = '" + productoPersonalizado.getProdid() + "' AND prodsiningredientes = '"+productoPersonalizado.getProdsiningredientes()+"'", null);
        }
        else
        {
            data.remove(productoPersonalizado);
            db.delete("pedido", "prodid ='" + productoPersonalizado.getProdid() + "' AND prodsiningredientes = '"+productoPersonalizado.getProdsiningredientes()+"'", null);
            adapter.notifyDataSetChanged();
        }
        disminuirTotal(productoPersonalizado.getProdprecio());
        db.close();

    }
}
