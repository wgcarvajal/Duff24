package duff24.com.duff24;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener
{
    public final static int MI_REQUEST_CODE_REGISTRARSE = 1;
    public final static int MI_REQUEST_CODE_CONT_NO_REGISTRADO = 2;
    public final static int MI_REQUEST_SE_LOGUIO_USUARIO=101;

    private String font_path="font/A_Simple_Life.ttf";
    private Typeface TF;
    private ImageView btnAtras;
    private Button btnIniciarSesion;
    private Button btnRegistrarse;
    private Button btnContNoRegistrado;
    private TextView txtrecuperarClave;
    private TextView txtemail;
    private TextView txtpassword;
    private TextView tituloVista;
    private ProgressDialog pd = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        txtrecuperarClave=(TextView)findViewById(R.id.recuperarClave);
        txtemail=(TextView)findViewById(R.id.txt_email);
        txtpassword=(TextView)findViewById(R.id.txt_password);

        btnAtras=(ImageView)findViewById(R.id.flecha_atras);
        btnRegistrarse=(Button)findViewById(R.id.btnRegistrarse);
        btnIniciarSesion=(Button)findViewById(R.id.btninciarsesion);
        btnContNoRegistrado=(Button)findViewById(R.id.btn_continuar_no_registrado);
        btnAtras.setOnClickListener(this);
        btnRegistrarse.setOnClickListener(this);
        btnIniciarSesion.setOnClickListener(this);
        btnContNoRegistrado.setOnClickListener(this);
        txtrecuperarClave.setOnClickListener(this);



        TF = Typeface.createFromAsset(getAssets(), font_path);
        btnIniciarSesion.setTypeface(TF);
        btnRegistrarse.setTypeface(TF);
        btnContNoRegistrado.setTypeface(TF);
        txtrecuperarClave.setTypeface(TF);
        txtemail.setTypeface(TF);
        txtpassword.setTypeface(TF);
    }


    @Override
    public void onClick(View v)
    {
        Intent intent;
        switch (v.getId())
        {
            case R.id.flecha_atras:
                finish();
            break;

            case R.id.btnRegistrarse:
                intent= new Intent(this,RegistrarseActivity.class);
                startActivityForResult(intent,MI_REQUEST_CODE_REGISTRARSE);
            break;
            case R.id.btn_continuar_no_registrado:
                intent= new Intent(this,NoregistradoActivity.class);
                startActivityForResult(intent,MI_REQUEST_CODE_CONT_NO_REGISTRADO);
            break;

            case R.id.recuperarClave:

            break;

            case R.id.btninciarsesion:
                iniciarSession();
            break;
        }

    }

    private void iniciarSession()
    {
        String email=txtemail.getText().toString();
        String password=txtpassword.getText().toString();
        if(verificarCampos(email,password))
        {

            pd = ProgressDialog.show(this, getResources().getString(R.string.txt_iniciando), getResources().getString(R.string.por_favor_espere), true, false);
            ParseUser.logInInBackground(email, password, new LogInCallback() {
                public void done(ParseUser user, ParseException e) {
                    if (user != null) {
                        pd.dismiss();
                        setResult(MI_REQUEST_SE_LOGUIO_USUARIO);
                        finish();
                    } else
                    {
                         pd.dismiss();
                         if(e.getCode()==101)
                         {
                             mostrarMensaje(R.string.txt_email_clave_incorrectos);
                         }
                         else
                         {
                             mostrarMensaje(R.string.compruebe_conexion);
                         }
                    }
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode)
        {
            case MI_REQUEST_CODE_REGISTRARSE:
                if (resultCode == RESULT_OK)
                {
                    mostrarDialogConfirmarPedido();
                }
            break;

            case MI_REQUEST_CODE_CONT_NO_REGISTRADO:
                if (resultCode == RESULT_OK)
                {
                    setResult(Activity.RESULT_OK);
                    finish();
                }
            break;
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
        TF = Typeface.createFromAsset(getAssets(), font_path);
        mensaje.setText(getResources().getString(R.string.confirmar_registro));
        mensaje.setTypeface(TF);
        btnAceptar.setTypeface(TF);

        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                dialog.dismiss();

            }
        });
        dialog.show();
    }

    private boolean verificarCampos(String email,String password)
    {

        if(email.equals("") || password.equals(""))
        {
            mostrarMensaje(R.string.campos_obligatorios_inicio_session);
            return false;
        }
        else
        {
            if(email.equals(""))
            {
                mostrarMensaje(R.string.campo_obligatorio_correo_inicio_session);
                return false;
            }
            else
            {
                if(password.equals(""))
                {
                    mostrarMensaje(R.string.campo_obligatorio_password_inicio_session);
                    return false;
                }
                else
                {
                    if(!validarCorreo(email))
                    {
                        mostrarMensaje(R.string.campo_correo);
                        return false;
                    }
                    else
                    {
                        if(password.length()<4 || password.length()>20)
                        {
                            mostrarMensaje(R.string.campo_password);
                            return false;
                        }
                    }
                }
            }
        }

        return true;
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

    private boolean validarCorreo(String email)
    {
        String PATTERN_EMAIL = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"+"[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(PATTERN_EMAIL);

        // Match the given input against this pattern
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
