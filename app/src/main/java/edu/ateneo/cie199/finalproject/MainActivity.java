package edu.ateneo.cie199.finalproject;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {
    MusicHandler music;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final PokemonGoApp app = (PokemonGoApp) getApplication();
        app.setFontForContainer((RelativeLayout)findViewById(R.id.main_group), "generation6.ttf");
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Plays the music during oncreate
        music = new MusicHandler();
        music.initMusic(this, R.raw.main_song);
        music.playMusic(app.getMusicSwitch());
        app.getMusicHandler().initButtonSfx(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        final PokemonGoApp app = (PokemonGoApp) getApplication();
        app.setMap(googleMap);
        app.getMap().getUiSettings().setMapToolbarEnabled(false);

        //INITIAL VALUES FOR TESTING
        final double initLatitude = 14.640528;
        final double initLongitude = 121.074899;
        LatLng initialPosition = new LatLng(initLatitude,initLongitude);

        //INITIALIZING POKEMON & MOVES & TYPES
        //TODO MAKE THIS FROM FILE
        app.loadAllItems();
        app.loadAllPokemonTypes();
        app.loadAllPokemon();
        app.loadAllPokemonMoves();
        app.loadPlayer(initialPosition);

        //INITIALIZING PLAYER
        //TODO LOAD PLAYER SAVE DATA FROM FILE INSTEAD OF HARD CODE


        app.setSpawnCount(app.getSpawnCount() + 1);

        //INITIALIZING CAMERA
        final float initZoom = 14.5f;
        app.moveMapCamera(app.getPlayer().getMarker().getPosition(), initZoom);
        app.getMap().setMaxZoomPreference(15.3f);

        //INITIALIZING SELECTED MARKER
        final ImageButton imgButtonMain = (ImageButton) findViewById(R.id.imgbtn_main_image);
        final TextView txvMain = (TextView) findViewById(R.id.txv_main_title);
        app.setSelectedMarker(app.getPlayer().getMarker());
        txvMain.setText(app.getPlayer().getName());
        imgButtonMain.setImageResource(R.drawable.player_main);
        imgButtonMain.setBackgroundColor(Color.argb(0, 0, 0, 0));

        //SPAWNER
        final Handler spawnHandler = new Handler();
        final Timer spawnTimer = new Timer();
        final int spawnRate = 6000;
        TimerTask spawnTask = new TimerTask() {
            @Override
            public void run() {
                spawnHandler.post(new Runnable() {
                    public void run() {

                        //SPAWNER SETTINGS
                        int maxSpawn = 30;
                        double rangeMax = 0.01;
                        double rangeMin = -0.01;

                        //GENERATING OFFSET
                        double offsetLat = rangeMin + (rangeMax - rangeMin) * app.getDoubleRNG();
                        double offsetLng = rangeMin + (rangeMax - rangeMin) * app.getDoubleRNG();

                        //GENERATING POKEMON
                        Pokemon spawnPokemon = app.getAllPokemons().get(app.getIntegerRNG(app.getAllPokemons().size()));
                        Item spawnItem = app.getPlayer().getBag()[app.getIntegerRNG(Player.MAX_BAG_SLOTS)];

                        //GENERATING SPAWN POINT
                        LatLng originPosition = app.getPlayer().getMarker().getPosition();
                        LatLng spawnPosition = new LatLng(originPosition.latitude + offsetLat,
                                originPosition.longitude + offsetLng);

                        Marker marker;
                        if(app.getIntegerRNG(5) > 0){
                            marker = app.getMap().addMarker(
                                    new MarkerOptions().position(spawnPosition).title(
                                            spawnPokemon.getName()).icon(
                                            BitmapDescriptorFactory.fromResource(
                                                    spawnPokemon.getIcon())));
                        }
                        else{
                            marker = app.getMap().addMarker(
                                    new MarkerOptions().position(spawnPosition).title(
                                            spawnItem.getName()).icon(
                                            BitmapDescriptorFactory.fromResource(
                                                    spawnItem.getImageIcon())));
                        }
                        app.addMarkers(marker);

                        //DESPAWN
                        if(app.getSpawnCount() >= maxSpawn){
                            if(app.getMarkers().get(app.getSpawnCount() - maxSpawn).equals(
                                    app.getSelectedMarker())){
                                app.setSelectedMarker(app.getPlayer().getMarker());
                                txvMain.setText(app.getSelectedMarker().getTitle());
                                imgButtonMain.setImageResource(R.drawable.player_main);
                            }
                            if(!(app.getMarkers().get(app.getSpawnCount() - maxSpawn).equals(
                                    app.getCurrentGoal()))) {
                                app.getMarkers().get(app.getSpawnCount() - maxSpawn).remove();
                                app.deleteMarker(app.getMarkers().get(
                                        app.getSpawnCount() - maxSpawn));
                            }
                        }
                        app.setSpawnCount(app.getSpawnCount() + 1);
                    }
                });
            }
        };
        spawnTimer.schedule(spawnTask, 0, spawnRate); //execute in every X minutes

        final Button btnSettings = (Button) findViewById(R.id.btn_main_settings);
        btnSettings.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        app.getMusicHandler().playButtonSfx(app.getSFXSwitch());
                        Intent settingsActivityIntent = new Intent(MainActivity.this, SettingsActivity.class);
                        startActivity(settingsActivityIntent);
                    }
                }
        );

        final Button btnPokemon = (Button) findViewById(R.id.btn_main_team);
        btnPokemon.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        app.getMusicHandler().playButtonSfx(app.getSFXSwitch());
                        Intent manageActivityIntent = new Intent(MainActivity.this, ManageActivity.class);
                        startActivity(manageActivityIntent);
                    }
                }
        );

        final Button btnAction = (Button) findViewById(R.id.btn_main_action);
        final Runnable engageMarker = new Runnable()
        {
            @Override
            public void run()
            {
                app.deleteMarker(app.getCurrentGoal());
                if(app.getSelectedMarker().equals(app.getCurrentGoal())){
                    app.setSelectedMarker(app.getPlayer().getMarker());
                    txvMain.setText(app.getPlayer().getName());
                    imgButtonMain.setImageResource(R.drawable.player_main);
                }

                app.getCurrentGoal().remove();
                btnAction.setClickable(true);
                app.getMap().getUiSettings().setAllGesturesEnabled(true);

                app.getPlayer().getMarker().setIcon(BitmapDescriptorFactory.fromResource(
                                R.drawable.player_stand));
                if(app.getPokemon(app.getCurrentGoal().getTitle()).isEmpty()){
                    app.getPlayer().giveItem(new Item(app.getCurrentGoal().getTitle(), 1));
                }
                else{
                    Intent battleActivityIntent = new Intent(MainActivity.this, BattleActivity.class);
                    startActivity(battleActivityIntent);
                }

                return;
            }
        };
        btnAction.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        app.getMusicHandler().playButtonSfx(app.getSFXSwitch());

                        //IF DESTINATION HAS BEEN SELECTED
                        if(!app.getSelectedMarker().equals(app.getPlayer().getMarker()) && (!app.getPlayer().isPlayerDefeated() || app.getPokemon(app.getSelectedMarker().getTitle()).isEmpty())) {

                            app.setCurrentGoal(app.getSelectedMarker());
                            txvMain.setText(app.getCurrentGoal().getTitle());

                            btnAction.setClickable(false);
                            app.getMap().getUiSettings().setAllGesturesEnabled(false);
                            Projection projection = app.getMap().getProjection();

                            //PULL CAMERA BACK TO PLAYER
                            final Handler moveHandler = new Handler();
                            Point startPoint = projection.toScreenLocation(
                                    app.getMap().getCameraPosition().target);

                            final LatLng pullPosition = projection.fromScreenLocation(
                                    startPoint);
                            final long initDuration = 500;
                            long pullDuration = initDuration;

                            if (!(app.getMap().getCameraPosition().target.equals(
                                    app.getPlayer().getMarker().getPosition()))){

                                final long pullStart = SystemClock.uptimeMillis();
                                final Interpolator pullInterpolator = new LinearInterpolator();
                                moveHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {

                                        long elapsed = SystemClock.uptimeMillis() - pullStart;
                                        float t = pullInterpolator.getInterpolation((float) elapsed
                                                / initDuration);
                                        double lng = t *
                                                app.getPlayer().getMarker().getPosition().longitude
                                                + (1 - t) * pullPosition.longitude;
                                        double lat = t *
                                                app.getPlayer().getMarker().getPosition().latitude
                                                + (1 - t) * pullPosition.latitude;

                                        app.moveMapCamera(new LatLng(lat, lng),
                                                app.getMap().getCameraPosition().zoom);

                                        if (t < 1.0) {
                                            // Post again 16ms later.
                                            moveHandler.postDelayed(this, 0);
                                        } else {
                                            app.getPlayer().getMarker().setVisible(true);
                                        }
                                    }
                                }, 0);
                            }
                            else{
                                pullDuration = 0;
                            }
                            //MOVE PLAYER TO GOAL
                            startPoint = projection.toScreenLocation(
                                    app.getPlayer().getMarker().getPosition());

                            final LatLng startLatLng = projection.fromScreenLocation(startPoint);
                            final long moveStart = SystemClock.uptimeMillis();
                            final long moveDuration = 10000;
                            final Interpolator moveInterpolator = new LinearInterpolator();

                            moveHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    long elapsed = SystemClock.uptimeMillis() - moveStart;
                                    float t = moveInterpolator.getInterpolation((float) elapsed
                                            / moveDuration);
                                    double lng = t * app.getCurrentGoal().getPosition().longitude
                                            + (1 - t) * startLatLng.longitude;
                                    double lat = t * app.getCurrentGoal().getPosition().latitude
                                            + (1 - t) * startLatLng.latitude;

                                    app.getPlayer().getMarker().setPosition(new LatLng(lat, lng));
                                    app.moveMapCamera(new LatLng(lat, lng),
                                            app.getMap().getCameraPosition().zoom);

                                    if(elapsed%500 > 0 && elapsed%500 <= 250) {
                                        app.getPlayer().getMarker().setIcon(
                                                BitmapDescriptorFactory.fromResource(
                                                        R.drawable.player_walk1));
                                    }
                                    else {
                                        app.getPlayer().getMarker().setIcon(
                                                BitmapDescriptorFactory.fromResource(
                                                        R.drawable.player_walk2));
                                    }
                                    if (t < 1.0) {
                                        // Post again 16ms later.
                                        moveHandler.postDelayed(this, 0);
                                    }
                                    else {
                                        app.getPlayer().getMarker().setVisible(true);
                                    }
                                }
                            }, pullDuration);
                            moveHandler.postDelayed(engageMarker, pullDuration + moveDuration);
                        }
                        else{
                            app.setCurrentGoal(app.getPlayer().getMarker());
                        }
                    }
                }
        );

        app.getMap().setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                app.setSelectedMarker(marker);

                if(app.getSelectedMarker().getTitle().isEmpty()){
                    txvMain.setText(app.getPlayer().getName());
                    imgButtonMain.setImageResource(R.drawable.player_main);
                }
                else {
                    txvMain.setText(app.getSelectedMarker().getTitle());
                    if(app.getPokemon(app.getSelectedMarker().getTitle()).isEmpty()){
                        imgButtonMain.setImageResource(app.getItem(marker.getTitle()).getImageBig());
                    }
                    else{
                        app.getMusicHandler().playSfx(MainActivity.this, app.getPokemon(app.getSelectedMarker().getTitle()).getSound(), app.getSFXSwitch());
                        imgButtonMain.setImageResource(app.getPokemon(marker.getTitle()).getMainImage());
                    }
                }
                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        PokemonGoApp app = (PokemonGoApp) getApplication();
        if(music == null){
            music.initMusic(this, MusicHandler.MUSIC_MAIN);
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
