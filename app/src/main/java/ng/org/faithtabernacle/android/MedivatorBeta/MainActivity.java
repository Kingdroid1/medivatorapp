package ng.org.faithtabernacle.android.MedivatorBeta;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_main);

        TextView text_view = findViewById (R.id.text_view);
        ImageView image_view = findViewById (R.id.image_view);
        Animation myanim = AnimationUtils.loadAnimation (this, R.anim.mytransition);

        text_view.startAnimation (myanim);
        image_view.startAnimation (myanim);


        final Intent i = new Intent (MainActivity.this, home_activity.class);

        final Thread timer = new Thread () {
            public void run() {
                try {
                    sleep (5000);
                } catch (InterruptedException e) {
                    e.printStackTrace ();
                } finally {
                    startActivity (i);
                    finish ();
                }
            }
        };

        timer.start ();
    }
}



