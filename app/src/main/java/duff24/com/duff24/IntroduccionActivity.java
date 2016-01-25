package duff24.com.duff24;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.VideoView;

public class IntroduccionActivity extends AppCompatActivity {

    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduccion);

        VideoView videoView = (VideoView) findViewById(R.id.video);

        Uri path = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.video_baja_calidad);


        videoView.setVideoURI(path);
        context=this;
        videoView.start();
        IrActiviyPrincipal ir= new IrActiviyPrincipal();
        ir.execute();


    }

    class IrActiviyPrincipal extends AsyncTask<Void, Void, Void>
    {


        @Override
        protected Void doInBackground(Void... params)
        {
            try {
                Thread.sleep(14000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Intent intent= new Intent(context,MainActivity.class);
            startActivity(intent);
            finish();
        }
    }


}