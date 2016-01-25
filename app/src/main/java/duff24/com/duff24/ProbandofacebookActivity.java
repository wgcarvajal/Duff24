package duff24.com.duff24;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

import java.util.Arrays;
import java.util.List;

public class ProbandofacebookActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnFacebook;
    Button btnCerrarFacebook;
    private Dialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_probandofacebook);

        btnFacebook=(Button)findViewById(R.id.btn_facebook);
        btnCerrarFacebook=(Button)findViewById(R.id.cerrar_session_facebook);
        btnFacebook.setOnClickListener(this);
        btnCerrarFacebook.setOnClickListener(this);
    }



    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.btn_facebook:


                List<String> permissions = Arrays.asList("public_profile", "user_about_me",
                        "user_birthday", "user_location", "email");



                ParseFacebookUtils.logInWithReadPermissionsInBackground(this, permissions, new LogInCallback() {



                    @Override
                    public void done(ParseUser user, ParseException err) {



                        if (user == null) {
                            Log.d("MyApp", "El usuario cancelo el Loggin");
                        } else if (user.isNew()) {

                            Log.d("MyApp", "Primer loggin del Usuario");


                        } else {
                            Log.d("MyApp", "El usuario ya estaba logueado");

                        }
                    }
                });
            break;
            case R.id.cerrar_session_facebook:
                ParseUser.logOut();
            break;
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }
}
