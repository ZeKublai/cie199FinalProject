package edu.ateneo.cie199.finalproject;

import android.animation.ValueAnimator;
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

import org.json.JSONException;

import java.security.spec.PKCS8EncodedKeySpec;

public class TitleActivity extends AppCompatActivity {
    MusicHandler music;

    private class APIData extends AsyncTask<String, Void, Void>{

        @Override
        protected Void doInBackground(String... strings) {
            final PokemonGoApp app = (PokemonGoApp) getApplication();
            app.loadAllPokemonTypes();
            try {
                Log.d("Load Moves", "LOADING MOVES..");
                app.loadAllMovesApi();
            } catch (JSONException e) {
                Log.e("PARSE ERROR", "FAILED TO PARSE JSON MOVE DATA");
                e.printStackTrace();
            }
            return null;
        }
    }

    private class PokemonAPI extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            final PokemonGoApp app = (PokemonGoApp) getApplication();
            try {
                Log.d("Load Pokemons", "LOADING PKMN...");
                app.loadAllPokemonApi();
            }
            catch (JSONException e){
                Log.e("Parsing err", "Failed to save json Pokemon data");
                e.printStackTrace();
            }
            return null;
        }
    }

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

        Button btnNewGame = (Button) findViewById(R.id.btn_title_new_game);
        try {
            APIData data = new APIData();
            data.execute();
        }
        catch(Error e){
            Log.e("Erorr", "Something went wrong");
        }

        try{
            PokemonAPI data = new PokemonAPI();
            data.execute();
        }
        catch(Error e){
            Log.e("Error", "Unable to request Pokemon API");
        }
        btnNewGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                app.setLoadData(false);
                //sound effect
                app.getMusicHandler().playSfx(TitleActivity.this, MusicHandler.SFX_SELECT, app.getSFXSwitch());
                Intent beginMainActivityIntent = new Intent(TitleActivity.this, MainActivity.class);
                startActivity(beginMainActivityIntent);
                return;
            }
        });

        Button btnContinueGame = (Button) findViewById(R.id.btn_title_continue_game);
        btnContinueGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                app.setLoadData(true);
                app.getMusicHandler().playSfx(TitleActivity.this, MusicHandler.SFX_SELECT, app.getSFXSwitch());
                Intent beginMainActivityIntent = new Intent(TitleActivity.this, MainActivity.class);
                startActivity(beginMainActivityIntent);
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
}
