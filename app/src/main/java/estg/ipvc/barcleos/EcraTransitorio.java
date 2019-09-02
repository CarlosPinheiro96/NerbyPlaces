package estg.ipvc.barcleos;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class EcraTransitorio extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 2000;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ecra_transitorio);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run(){
                Intent nextscreen = new Intent(EcraTransitorio.this, MapsActivity.class);
                startActivity(nextscreen);
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}