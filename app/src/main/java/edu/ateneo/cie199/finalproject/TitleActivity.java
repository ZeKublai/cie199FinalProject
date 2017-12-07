package edu.ateneo.cie199.finalproject;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.json.JSONException;

public class TitleActivity extends AppCompatActivity {
    MusicHandler music;
    int REQUEST_DATA_LOADED = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_title);

        final PokemonGoApp app = (PokemonGoApp) getApplication();
        app.setFontForContainer((RelativeLayout)findViewById(R.id.title_group), "generation6.ttf");
        //Plays music
        music = new MusicHandler();
        music.initMusic(this, MusicHandler.MUSIC_TITLE);
        music.playMusic(app.getMusicSwitch());

        final ImageView backgroundOne = (ImageView) findViewById(R.id.background_one);
        final ImageView backgroundTwo = (ImageView) findViewById(R.id.background_two);

        final ValueAnimator animator = ValueAnimator.ofFloat(0.0f, 1.0f);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(10000L);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                final float progress = (float) animation.getAnimatedValue();
                final float width = backgroundOne.getWidth();
                final float translationX = width * progress;
                backgroundOne.setTranslationX(translationX);
                backgroundTwo.setTranslationX(translationX - width);
            }
        });
        animator.start();

        Button btnNewGame = (Button) findViewById(R.id.btn_load_new_game);
        btnNewGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                app.setLoadData(false);
                //sound effect
                app.getMusicHandler().playSfx(TitleActivity.this, MusicHandler.SFX_SELECT, app.getSFXSwitch());
                Intent beginMainActivityIntent = new Intent(TitleActivity.this, LoadingScreenActivity.class);
                beginMainActivityIntent.putExtra("Continue?", app.getLoadData());
                startActivityForResult(beginMainActivityIntent, REQUEST_DATA_LOADED);
                return;
            }
        });

        Button btnContinueGame = (Button) findViewById(R.id.btn_load_continue_game);
        btnContinueGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                app.setLoadData(true);
                app.getMusicHandler().playSfx(TitleActivity.this, MusicHandler.SFX_SELECT, app.getSFXSwitch());
                Intent beginMainActivityIntent = new Intent(TitleActivity.this, LoadingScreenActivity.class);
                beginMainActivityIntent.putExtra("Continue?", app.getLoadData());
                startActivityForResult(beginMainActivityIntent, REQUEST_DATA_LOADED);
                return;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        PokemonGoApp app = (PokemonGoApp) getApplication();
        if(music == null){
            music.initMusic(this, MusicHandler.MUSIC_TITLE);
        }
        if(!music.getMusicPlayer().isPlaying()) {
            music.playMusic(app.getMusicSwitch());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        music.getMusicPlayer().pause();
    }

    @Override
    public void onBackPressed(){

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("INTENT FINISH", Integer.toString(requestCode));
        Log.e("INTENT result", Integer.toString(resultCode));
        String msg = "";
        if(requestCode == REQUEST_DATA_LOADED && resultCode == RESULT_CANCELED ) {
             msg = data.getStringExtra("MSG");
        }
        Toast.makeText(TitleActivity.this, msg, Toast.LENGTH_LONG).show();

    }

}
