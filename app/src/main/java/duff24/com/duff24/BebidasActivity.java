package duff24.com.duff24;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
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


import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;
import duff24.com.duff24.adaptadores.PagerAdapterGrid;
import duff24.com.duff24.fragments.ProductoGridFragment;
import duff24.com.duff24.modelo.Subcategoria;
import duff24.com.duff24.typeface.CustomTypefaceSpan;
import duff24.com.duff24.util.AppUtil;
import pl.droidsonroids.gif.GifImageView;

public class BebidasActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {


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
    private TextView text_compruebe_conexion;
    private Button btnRecargarVista;
    private Typeface TF;
    private String font_path = "font/2-4ef58.ttf";
    private String font_path_ASimple="font/A_Simple_Life.ttf";
    private ProgressDialog pd = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        titulo = (TextView)findViewById(R.id.txttitulo);
        tituloMenuHeader=(TextView)findViewById(R.id.titulo_header_menu);
        text_compruebe_conexion=(TextView)findViewById(R.id.txt_sin_conexion);

        btnMenuPrincipal=(ImageView)findViewById(R.id.btn_menu_principal);
        btnMipedido = (GifImageView)findViewById(R.id.btn_mi_pedido);
        btnComidas = (ImageView) findViewById(R.id.btncomida);
        btnMarket=(ImageView) findViewById(R.id.btnmarket);
        btnBebidas= (ImageView) findViewById(R.id.btnbebida);
        btnRecargarVista=(Button)findViewById(R.id.volver_cargar);

        text_compruebe_conexion.setVisibility(View.GONE);
        btnRecargarVista.setVisibility(View.GONE);

        drawer=(DrawerLayout)findViewById(R.id.drawer);
        pager= (ViewPager)findViewById(R.id.pager);
        navView = (NavigationView) findViewById(R.id.nav);

        btnMenuPrincipal.setOnClickListener(this);
        btnComidas.setOnClickListener(this);
        btnMarket.setOnClickListener(this);
        btnMipedido.setOnClickListener(this);
        navView.setNavigationItemSelectedListener(this);

        btnComidas.setImageResource(R.mipmap.ic_foods_opaco);
        btnBebidas.setImageResource(R.mipmap.ic_driks);

        TF = Typeface.createFromAsset(getAssets(), font_path);
        titulo.setTypeface(TF);
        tituloMenuHeader.setTypeface(TF);

        data= new ArrayList<>();
        adapter = new PagerAdapterGrid(getSupportFragmentManager(), data);
        pager.setAdapter(adapter);

        Menu m = navView.getMenu();
        aplicandoTipoLetraItemMenu(m,font_path_ASimple);

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
            if(sub.getNombreCategoria().equals("Drink"))
            {
                ProductoGridFragment productoFragment = new ProductoGridFragment();
                productoFragment.init(sub.getNombreIngles(), sub.getNombreEspanol());
                data.add(productoFragment);
                adapter.notifyDataSetChanged();
            }
        }

        Menu m=navView.getMenu();
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            m.getItem(1).setTitle(currentUser.getUsername());
            m.getItem(1).setVisible(true);
            Menu men=m.getItem(1).getSubMenu();
            men.getItem(0).setVisible(true);
        }
        else
        {
            m.getItem(1).setVisible(false);
            Menu men=m.getItem(1).getSubMenu();
            men.getItem(0).setVisible(false);
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
            case R.id.btnmarket:
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

        CerrarSesionTask cst= new CerrarSesionTask();
        cst.execute();

    }

    public class CerrarSesionTask extends AsyncTask<Void,Void,Void>
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MI_REQUEST_CODE)
        {
                int posicionactual=pager.getCurrentItem();
                int tamano=adapter.getCount();
                ProductoGridFragment prodFrag=(ProductoGridFragment)getSupportFragmentManager().getFragments().get(posicionactual);
                prodFrag.actualizarData();
                if((posicionactual+1)<tamano)
                {
                    prodFrag=(ProductoGridFragment)getSupportFragmentManager().getFragments().get(posicionactual+1);;
                    prodFrag.actualizarData();
                }
                if((posicionactual-1)>=0)
                {
                    prodFrag=(ProductoGridFragment)getSupportFragmentManager().getFragments().get(posicionactual-1);
                    prodFrag.actualizarData();
                }
            Menu m=navView.getMenu();
            mostrandoMenu(m);
            ParseUser currentUser = ParseUser.getCurrentUser();
            if (currentUser != null) {
                m.getItem(1).setTitle(currentUser.getUsername());
            }
            else
            {
                m.getItem(1).setVisible(false);
                Menu men=m.getItem(1).getSubMenu();
                men.getItem(0).setVisible(false);
            }
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
}
