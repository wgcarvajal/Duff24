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
import duff24.com.duff24.typeface.CustomTypefaceSpan;

public class MarketActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView btnComidas;
    private ImageView btnBebidas;
    private ImageView btnMarket;
    private TextView titulo;
    private ViewPager pager;
    private PagerAdapterGrid adapter;
    private List<ProductoGridFragment> data;

    private NavigationView navView;
    private ImageView btnMenuPrincipal;
    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnMenuPrincipal=(ImageView)findViewById(R.id.btn_menu_principal);
        btnMenuPrincipal.setOnClickListener(this);

        drawer=(DrawerLayout)findViewById(R.id.drawer);

        btnComidas = (ImageView) findViewById(R.id.btncomida);
        btnComidas.setImageResource(R.mipmap.ic_foods_opaco);
        btnComidas.setOnClickListener(this);

        btnMarket=(ImageView) findViewById(R.id.btnmarket);
        btnMarket.setImageResource(R.mipmap.ic_market);

        btnBebidas= (ImageView) findViewById(R.id.btnbebida);
        btnBebidas.setImageResource(R.mipmap.ic_drinks_opaco);
        btnBebidas.setOnClickListener(this);

        titulo = (TextView)findViewById(R.id.txttitulo);

        String font_path = "font/2-4ef58.ttf";
        Typeface TF = Typeface.createFromAsset(getAssets(), font_path);
        titulo.setTypeface(TF);

        data= new ArrayList<>();
        pager= (ViewPager)findViewById(R.id.pager);
        adapter = new PagerAdapterGrid(getSupportFragmentManager(), data);
        pager.setAdapter(adapter);



        ParseQuery<ParseObject> queryCategoria = new ParseQuery<ParseObject>(Producto.TABLACATEGORIA);
        queryCategoria.whereEqualTo(Producto.CATEGORIANOMBRE, "Market");
        queryCategoria.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject categoria, ParseException e) {
                if (e == null) {
                    Log.i("entro", "entro");
                    ParseQuery<ParseObject> querySubcategoria = new ParseQuery<ParseObject>(Producto.TABLASUBCATEGORIA);
                    querySubcategoria.whereEqualTo(Producto.TBLSUBCATEGORIA_CATEGORIA, categoria.getObjectId());
                    querySubcategoria.orderByAscending(Producto.TBLSUBCATEGORIA_NOMBRE);
                    querySubcategoria.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> subcategorias, ParseException e) {
                            if (e == null) {
                                Log.i("entro", subcategorias.size() + "");
                                for (ParseObject subcategoria : subcategorias) {


                                    ProductoGridFragment productoFragment = new ProductoGridFragment();
                                    productoFragment.init(subcategoria.getString(Producto.TBLSUBCATEGORIA_NOMBRE), subcategoria.getString(Producto.TBLSUBCATEGORIA_NOMBREESP));

                                    data.add(productoFragment);
                                    adapter.notifyDataSetChanged();

                                }
                            }
                        }
                    });
                }
            }
        });
        navView = (NavigationView) findViewById(R.id.nav);
        Menu m = navView.getMenu();
        for (int i=0;i<m.size();i++) {
            MenuItem mi = m.getItem(i);

            //for aapplying a font to subMenu ...
            SubMenu subMenu = mi.getSubMenu();
            if (subMenu!=null && subMenu.size() >0 ) {
                for (int j=0; j <subMenu.size();j++) {
                    MenuItem subMenuItem = subMenu.getItem(j);
                    applyFontToMenuItem(subMenuItem);
                }
            }

            //the method we have create in activity
            applyFontToMenuItem(mi);
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
        }
    }

    private void applyFontToMenuItem(MenuItem mi) {
        Typeface font = Typeface.createFromAsset(getAssets(), "font/2-4ef58.ttf");
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("" , font), 0 , mNewTitle.length(),  Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }
}
