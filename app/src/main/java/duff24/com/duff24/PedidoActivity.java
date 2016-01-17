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
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import duff24.com.duff24.typeface.CustomTypefaceSpan;

public class PedidoActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {
    private Typeface TF;
    private String font_path = "font/2-4ef58.ttf";
    private TextView titulo;
    private TextView tituloMenuHeader;
    private ImageView btnMenuPrincipal;
    private DrawerLayout drawer;
    private NavigationView navView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedido);

        titulo = (TextView)findViewById(R.id.txttitulo);
        tituloMenuHeader=(TextView)findViewById(R.id.titulo_header_menu);

        btnMenuPrincipal=(ImageView)findViewById(R.id.btn_menu_principal);

        drawer=(DrawerLayout)findViewById(R.id.drawer);
        navView = (NavigationView) findViewById(R.id.nav);

        btnMenuPrincipal.setOnClickListener(this);
        navView.setNavigationItemSelectedListener(this);

        TF = Typeface.createFromAsset(getAssets(), font_path);
        titulo.setTypeface(TF);
        tituloMenuHeader.setTypeface(TF);

        Menu m = navView.getMenu();
        aplicandoTipoLetraItemMenu(m, font_path);

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
                finish();
            break;
        }
        drawer.closeDrawers();
        return false;
    }
}
