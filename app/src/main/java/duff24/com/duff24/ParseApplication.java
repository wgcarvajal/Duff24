package duff24.com.duff24;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;


public class ParseApplication extends Application
{
    public final String APPID="NQxh04eIW6dtbOmNfqPLRhKsx9S4X7KKedCUkTeW";
    public final String CLIENTKEY="vuCK5Vn8X4vpP0XsHDN3WUyIgAkOX5UhFuEIwSpY";

    @Override
    public void onCreate()
    {
        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, APPID, CLIENTKEY);
    }
}