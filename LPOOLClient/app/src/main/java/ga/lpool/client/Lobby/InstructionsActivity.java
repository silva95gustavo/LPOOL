package ga.lpool.client.Lobby;

import android.app.ActionBar;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import ga.lpool.client.R;


public class InstructionsActivity extends Activity {

    Animation tilt_anim;
    ImageView tilt_anim_img;
    Animation press_anim;
    ImageView press_anim_img;
    Animation cursor_anim;
    ImageView cursor_anim_img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
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

        press_anim = AnimationUtils.loadAnimation(this, R.anim.instructions_press);
        press_anim.reset();

        press_anim_img = (ImageView) findViewById(R.id.imageViewPress);
        press_anim_img.startAnimation(press_anim);

        press_anim.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationEnd(Animation arg0) { press_anim_img.startAnimation(press_anim);}

            public void onAnimationRepeat(Animation arg0) {}

            public void onAnimationStart(Animation arg0) {}
        });

        cursor_anim = AnimationUtils.loadAnimation(this, R.anim.instructions_cursor);
        cursor_anim.reset();

        cursor_anim_img = (ImageView) findViewById(R.id.imageViewCursor);
        cursor_anim_img.startAnimation(cursor_anim);

        cursor_anim.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationEnd(Animation arg0) { cursor_anim_img.startAnimation(cursor_anim);}

            public void onAnimationRepeat(Animation arg0) {}

            public void onAnimationStart(Animation arg0) {}
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        super.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_instructions, menu);
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

    protected void onPause() {
        super.onPause();
        super.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    protected void onResume() {
        super.onResume();
        super.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }
}
