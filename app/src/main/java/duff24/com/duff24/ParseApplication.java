package duff24.com.duff24;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;
import android.util.Log;

import com.facebook.FacebookSdk;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ParseApplication extends Application
{
    public final String APPID="NQxh04eIW6dtbOmNfqPLRhKsx9S4X7KKedCUkTeW";
    public final String CLIENTKEY="vuCK5Vn8X4vpP0XsHDN3WUyIgAkOX5UhFuEIwSpY";

    @Override
    public void onCreate()
    {
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, APPID, CLIENTKEY);
        ParseFacebookUtils.initialize(this);
        FacebookSdk.sdkInitialize(this);
        //generarHash();

    }


    /*private void generarHash()
    {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "duff24.com.duff24",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
        } catch (NoSuchAlgorithmException e) {
        }
    }*/
}