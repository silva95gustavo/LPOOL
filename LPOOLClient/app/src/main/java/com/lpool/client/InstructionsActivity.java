package com.lpool.client;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;


public class InstructionsActivity extends ActionBarActivity {

    Animation tilt_anim;
    ImageView tilt_anim_img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructions);

        tilt_anim = AnimationUtils.loadAnimation(this, R.anim.instructions_anim_tilt);
        tilt_anim.reset();

        tilt_anim_img = (ImageView) findViewById(R.id.imageViewGif);
        tilt_anim_img.startAnimation(tilt_anim);

        tilt_anim.setAnimationListener(new Animation.AnimationListener() {

            public void onAnimationEnd(Animation arg0) {
                tilt_anim_img.startAnimation(tilt_anim);
            }

            public void onAnimationRepeat(Animation arg0) {}

            public void onAnimationStart(Animation arg0) {}

        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_instructions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
