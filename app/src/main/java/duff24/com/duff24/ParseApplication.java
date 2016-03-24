package duff24.com.duff24;

import android.app.Application;

import com.backendless.Backendless;
import com.facebook.FacebookSdk;

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
    }

}