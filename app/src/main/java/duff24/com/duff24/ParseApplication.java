package duff24.com.duff24;

import android.app.Application;
import android.util.Log;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.facebook.FacebookSdk;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ParseApplication extends Application
{

    public final String YOUR_APP_ID="07007171-0E5D-D4B2-FF08-561CD4DBDE00";
    public final String YOUR_SECRET_KEY="3FB7A7F5-639D-62CE-FF17-409E620BE900";

    @Override
    public void onCreate()
    {
        FacebookSdk.sdkInitialize(this);
        String appVersion = "v1";
        Backendless.initApp(this, YOUR_APP_ID, YOUR_SECRET_KEY, appVersion);

        ArrayList registrados = new ArrayList<String>();
        registrados.add("registrados");

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date()); // Configuramos la fecha que se recibe

        calendar.add(Calendar.YEAR, 5);

        Backendless.Messaging.registerDevice("464411838818",registrados,calendar.getTime(), new AsyncCallback<Void>() {
            @Override
            public void handleResponse(Void response) {
                Log.i("device:", "registrado");
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Log.i("error:", fault.getMessage());
            }
        });
    }

}