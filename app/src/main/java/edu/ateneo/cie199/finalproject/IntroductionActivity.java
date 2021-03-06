package edu.ateneo.cie199.finalproject;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by John, Duke and JV on 11/26/2017.
 * This class contains the scripted sequence of messages to act like a tutorial and crash course.
 */

public class IntroductionActivity extends AppCompatActivity {

    private Gender mChosenGender = new Gender();
    private String mChosenName = "";
    private PokéDexData mChosenStarter = new PokéDexData();
    private int mCurrentMessage = 1;
    private String[] mScript = new String[29];

    MusicHandler music;

    /**
     * Initializes the activity.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduction);
        final PokemonApp app = (PokemonApp) getApplication();

        //Plays music
        music = new MusicHandler();
        music.initMusic(this, MusicHandler.MUSIC_INTRO);
        music.playMusic(app.getMusicSwitch());
        app.getMusicHandler().initButtonSfx(this);

        app.setFontForContainer((RelativeLayout)findViewById(R.id.intro_group), "generation6.ttf");
        final Button btnAction = (Button) findViewById(R.id.btn_intro_action);
        ImageView imgJerome = (ImageView) findViewById(R.id.img_intro_jerome);
        final ImageView imgOmastar = (ImageView) findViewById(R.id.img_intro_pokemon);
        final TextView txvMessage = (TextView) findViewById(R.id.txv_intro_message);
        final ImageButton imgbtnPokeBall = (ImageButton) findViewById(R.id.imgbtn_intro_pokeball);

        updateScript();

        imgJerome.setImageResource(R.drawable.jerome_intro);
        imgOmastar.setImageResource(R.drawable.jerome_pokemon);
        imgOmastar.setVisibility(View.INVISIBLE);
        txvMessage.setText(mScript[0] + "∇");
        btnAction.setBackgroundColor(PokemonApp.TRANSPARENT_COLOR);
        imgbtnPokeBall.setBackgroundResource(R.drawable.intro_pokeball);
        imgbtnPokeBall.setVisibility(View.INVISIBLE);
        imgbtnPokeBall.setClickable(false);

        btnAction.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        app.getMusicHandler().playButtonSfx(app.getSFXSwitch());
                        txvMessage.setText(mScript[mCurrentMessage] + "∇");
                        if(mCurrentMessage == 5){
                            mCurrentMessage++;
                            imgbtnPokeBall.setVisibility(View.VISIBLE);
                            imgbtnPokeBall.setClickable(true);
                        }
                        else if(mCurrentMessage == 6){
                            imgbtnPokeBall.setClickable(true);
                            btnAction.setClickable(false);
                        }
                        else if(mCurrentMessage == 14){
                            btnAction.setClickable(false);

                            final Dialog genderDialog = new Dialog(IntroductionActivity.this);
                            genderDialog.setContentView(R.layout.choose_gender_dialog);
                            app.setFontForContainer(
                                    (RelativeLayout) genderDialog.findViewById(R.id.gender_group),
                                    "generation6.ttf"
                            );
                            genderDialog.setTitle("");
                            genderDialog.setCancelable(false);
                            genderDialog.setCanceledOnTouchOutside(false);
                            ImageButton imgbtnBoy = (ImageButton) genderDialog.findViewById(
                                    R.id.imgbtn_choose_boy
                            );
                            ImageButton imgbtnGirl = (ImageButton) genderDialog.findViewById(
                                    R.id.imgbtn_choose_girl
                            );

                            // if button is clicked, close the custom dialog
                            imgbtnBoy.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    app.getMusicHandler().playButtonSfx(app.getSFXSwitch());
                                    setGender(true, txvMessage);
                                    btnAction.setClickable(true);
                                    genderDialog.dismiss();
                                }
                            });
                            imgbtnGirl.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    app.getMusicHandler().playButtonSfx(app.getSFXSwitch());
                                    setGender(false, txvMessage);
                                    btnAction.setClickable(true);
                                    genderDialog.dismiss();
                                }
                            });

                            genderDialog.show();
                        }
                        else if(mCurrentMessage == 16){
                            btnAction.setClickable(false);

                            final Dialog nameDialog = new Dialog(IntroductionActivity.this);
                            nameDialog.setContentView(R.layout.input_name_dialog);
                            app.setFontForContainer(
                                    (RelativeLayout) nameDialog.findViewById(R.id.name_group),
                                    PokemonApp.RETRO_FONT
                            );
                            nameDialog.setTitle("");
                            nameDialog.setCancelable(false);
                            nameDialog.setCanceledOnTouchOutside(false);

                            Button btnOk = (Button) nameDialog.findViewById(R.id.btn_name_ok);
                            final EditText edtName = (EditText) nameDialog.findViewById(
                                    R.id.edt_name_input
                            );
                            app.setAsOkButton(btnOk);

                            btnOk.setOnClickListener(
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            app.getMusicHandler().playButtonSfx(app.getSFXSwitch());
                                            if(edtName.getText().toString().length() > 15){
                                                edtName.setError("Name is too long");
                                            }
                                            else if(edtName.getText().toString().isEmpty()){
                                                edtName.setError("Please input a name");
                                            }
                                            else{
                                                mChosenName = edtName.getText().toString();
                                                mCurrentMessage++;
                                                mScript[17] = "OK... So, you're "
                                                        + mChosenName
                                                        + "?";
                                                mScript[19] = "All right, "
                                                        + mChosenName
                                                        + ", time to cram a life decision. "
                                                        + "Again. Maybe.";
                                                txvMessage.setText(mScript[mCurrentMessage] + "∇");
                                                mCurrentMessage = 18;
                                                btnAction.setClickable(true);
                                                nameDialog.dismiss();
                                            }
                                        }
                                    }
                            );


                            nameDialog.show();
                        }
                        else if(mCurrentMessage == 20){
                            btnAction.setClickable(false);

                            final Dialog starterDialog = new Dialog(IntroductionActivity.this);
                            starterDialog.setContentView(R.layout.choose_starter_dialog);
                            app.setFontForContainer(
                                    (RelativeLayout) starterDialog.findViewById(R.id.starter_group),
                                    PokemonApp.RETRO_FONT
                            );
                            starterDialog.setTitle("");
                            starterDialog.setCancelable(false);
                            starterDialog.setCanceledOnTouchOutside(false);
                            ImageButton imgbtnBulbasaur = (ImageButton) starterDialog.findViewById(
                                    R.id.imgbtn_bulbasaur
                            );
                            ImageButton imgbtnCharmander = (ImageButton) starterDialog.findViewById(
                                    R.id.imgbtn_charmander
                            );
                            ImageButton imgbtnSquirtle = (ImageButton) starterDialog.findViewById(
                                    R.id.imgbtn_squirtle
                            );

                            // if button is clicked, close the custom dialog
                            imgbtnBulbasaur.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    app.getMusicHandler().playButtonSfx(app.getSFXSwitch());
                                    setStarter(1, txvMessage);
                                    btnAction.setClickable(true);
                                    starterDialog.dismiss();
                                }
                            });
                            imgbtnCharmander.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    app.getMusicHandler().playButtonSfx(app.getSFXSwitch());
                                    setStarter(4, txvMessage);
                                    btnAction.setClickable(true);
                                    starterDialog.dismiss();
                                }
                            });
                            imgbtnSquirtle.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    app.getMusicHandler().playButtonSfx(app.getSFXSwitch());
                                    setStarter(7, txvMessage);
                                    btnAction.setClickable(true);
                                    starterDialog.dismiss();
                                }
                            });
                            starterDialog.show();
                        }
                        else if(mCurrentMessage == mScript.length - 1){
                            //NEEDS TWEAKING
                            PokémonProfile starter = new PokémonProfile(0, mChosenStarter, 5);
                            starter.getMoves().add(app.generateRandomMove());
                            starter.getMoves().add(app.generateRandomMove());
                            starter.getMoves().add(app.generateRandomMove());
                            starter.getMoves().add(app.generateRandomMove());
                            app.setPlayer(new Player(mChosenGender, mChosenName, starter));
                            Intent beginMainActivityIntent = new Intent(
                                    IntroductionActivity.this,
                                    MainActivity.class
                            );
                            startActivity(beginMainActivityIntent);
                        }
                        else {
                            mCurrentMessage++;
                        }
                    }
                }
        );

        imgbtnPokeBall.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        app.getMusicHandler().playSfx(
                                IntroductionActivity.this,
                                R.raw.omastar,
                                app.getSFXSwitch()
                        );
                        mCurrentMessage = 7;
                        imgOmastar.setVisibility(View.VISIBLE);
                        imgbtnPokeBall.setVisibility(View.INVISIBLE);
                        imgbtnPokeBall.setClickable(false);
                        btnAction.setClickable(true);
                    }
                }
        );

    }

    /**
     * This creates an array of String objects at which it would act as the message sequence.
     */
    public void updateScript(){
        mScript = new String[]{
                "Hello there! It's so very nice to meet you!",
                "Welcome to the world of Pokémon",
                "My name is Jerome.",
                "However, everyone just calls me Nekomonsterr.",
                "This world is widely inhabited by creatures known as Pokémon.",
                "Here, I have a Poke Ball.",
                "Touch the button on the middle of the PokéBall, if you'd please.",
                "There are different types of Pokéball; they are used to catch these Pokémons",
                "At times we play together, and at other times we work together.",
                "Heal them using Potions, Resurrect with Revive and Heal PP with Elixir",
                "What do I do?",
                "I am a coffee-fueled travelling researcher from parts Unown.",
                "Part of my endgame is using an army of robodogs to take over the world.",
                "Now, why don't you tell me a little bit about yourself?",
                "Are you a boy? Or are you a girl?",
                "All right, so you're a " + mChosenGender + "?",
                "Tell me, what is your name?",
                "OK... So, you're " + mChosenName + "?",
                "I'll probably forget that by next semester.",
                "All right, " + mChosenName + ", time to cram a life decision. Again. Maybe.",
                "Which starter do you want?",
                "Hmm... " + mChosenStarter + " seems to be rather happy.",
                "All righty then!",
                "I'll give " + mChosenStarter + " to you as a gift.",
                "Your very own tale of grand adventure is about to unfold.",
                "Battle those wild Pokemons to catch them",
                "And battle those Trainers to assert your dominance in this world!",
                "Now, go on, I have been awake now for 24 + 10 hours.",
                ""
        };
    }

    /**
     * Sets the Gender of the Player.
     * @param selectedGender    Either boy or girl.
     * @param message           The message to be shown.
     */
    public void setGender(boolean selectedGender, TextView message){
        mCurrentMessage++;
        mChosenGender = new Gender(selectedGender);
        mScript[15] = "All right, so you're a " + mChosenGender.getName() + "?";
        message.setText(mScript[mCurrentMessage] + "∇");
        mCurrentMessage = 16;
    }

    /**
     * Sets the starter Pokémon.
     * @param dexNumber The PokéDex number of the starter Pokémon.
     * @param message   The message to be shown.
     */
    public void setStarter(int dexNumber, TextView message){
        PokemonApp app = (PokemonApp) getApplication();
        mCurrentMessage++;
        mChosenStarter = app.getPokemon(dexNumber);
        mScript[21] = "Hmm... " + mChosenStarter.getName() + " seems to be rather happy.";
        mScript[23] = "I'll give " + mChosenStarter.getName() + " to you as a gift.";
        message.setText(mScript[mCurrentMessage] + "∇");
        mCurrentMessage = 22;
    }

    /**
     * Continues the music when switching Activities.
     */
    @Override
    protected void onResume() {
        super.onResume();
        PokemonApp app = (PokemonApp) getApplication();
        if(music == null){
            music.initMusic(this, MusicHandler.MUSIC_INTRO);
        }
        if(!music.getMusicPlayer().isPlaying()) {
            music.playMusic(app.getMusicSwitch());
        }
    }

    /**
     * Pause the music when switching Activities.
     */
    @Override
    protected void onPause() {
        super.onPause();
        music.getMusicPlayer().pause();
    }

    /**
     * Disables the back Button.
     */
    @Override
    public void onBackPressed(){

    }

}
