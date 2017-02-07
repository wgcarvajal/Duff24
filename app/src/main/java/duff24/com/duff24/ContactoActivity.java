package duff24.com.duff24;

import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import duff24.com.duff24.modelo.Contacto;
import duff24.com.duff24.util.FontCache;

public class ContactoActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView btnFlechaAtras;
    private EditText textEmail;
    private EditText textNombre;
    private EditText textMensaje;
    private TextView textOpinion;
    private Button btnEnviar;
    private String font_path = "font/KGTenThousandReasonsAlt.ttf";
    private ProgressDialog pd = null;
    private VideoView videofondo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacto);

        videofondo = (VideoView)findViewById(R.id.videofondo);

        btnFlechaAtras=(ImageView)findViewById(R.id.flecha_atras);
        btnEnviar=(Button)findViewById(R.id.btn_enviar_pedido);

        textEmail=(EditText)findViewById(R.id.txt_email);
        textNombre=(EditText)findViewById(R.id.txt_nombre);
        textMensaje=(EditText)findViewById(R.id.txt_observaciones);
        textOpinion=(TextView)findViewById(R.id.txtOpinion);

        btnFlechaAtras.setOnClickListener(this);
        btnEnviar.setOnClickListener(this);

        Typeface TF = FontCache.get(font_path,this);
        textOpinion.setTypeface(TF);
        textMensaje.setTypeface(TF);
        textNombre.setTypeface(TF);
        textEmail.setTypeface(TF);
        btnEnviar.setTypeface(TF);
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.videofondo.setVideoPath("android.resource://"+getPackageName()+"/"+R.raw.fondo_duff);
        this.videofondo.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
        {
            public void onCompletion(MediaPlayer paramAnonymousMediaPlayer)
            {
                ContactoActivity.this.videofondo.start();
            }
        });
        this.videofondo.start();
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.flecha_atras:
                finish();
            break;
            case R.id.btn_enviar_pedido:
                enviar();
            break;
        }

    }

    private void enviar()
    {
        String email=textEmail.getText().toString();
        String nombre= textNombre.getText().toString();
        String mensaje=textMensaje.getText().toString();
        if(email.equals("")||nombre.equals("")||mensaje.equals(""))
        {
            mostrarMensaje(R.string.todos_campos_obligatorios);
        }
        else
        {
            if(!validarCorreo(email))
            {
                mostrarMensaje(R.string.campo_correo);
            }
            else
            {


                pd = ProgressDialog.show(this, getResources().getString(R.string.enviando_datos), getResources().getString(R.string.por_favor_espere), true, false);

                Contacto contacto = new Contacto();
                contacto.setEmail(email);
                contacto.setMensaje(mensaje);
                contacto.setNombre(nombre);

                Backendless.Persistence.save(contacto, new AsyncCallback<Contacto>() {
                    @Override
                    public void handleResponse(Contacto response)
                    {
                        pd.dismiss();
                        mostrarMensaje(R.string.txt_mensaje_enviado);
                        textEmail.setText("");
                        textNombre.setText("");
                        textMensaje.setText("");
                    }

                    @Override
                    public void handleFault(BackendlessFault fault)
                    {
                        pd.dismiss();
                        mostrarMensaje(R.string.compruebe_conexion);
                    }
                });

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

    private boolean validarCorreo(String email)
    {
        String PATTERN_EMAIL = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"+"[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(PATTERN_EMAIL);

        // Match the given input against this pattern
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
