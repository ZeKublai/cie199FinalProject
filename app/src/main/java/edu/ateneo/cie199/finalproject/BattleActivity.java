package edu.ateneo.cie199.finalproject;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by John, Duke and JV on 11/7/2017.
 * This Activity handles the Battle UI and the Battle object.
 */

public class BattleActivity extends AppCompatActivity {
    private Battle battle;
    MusicHandler music;

    private Button btnAction;
    private Button btnFight;
    private Button btnPokemon;
    private Button btnBag;
    private Button btnRun;

    private TextView txvMessage;
    private ListView lsvOptions;

    /**
     * Initializes the needed member functions and the Battle object.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battle);
        final PokemonApp app = (PokemonApp) getApplication();
        app.setFontForContainer(
                (RelativeLayout)findViewById(R.id.battle_group),
                PokemonApp.RETRO_FONT
        );

        //MUSIC INITIALIZATION
        music = new MusicHandler();
        music.initMusic(BattleActivity.this, MusicHandler.MUSIC_BATTLE);
        music.playMusic(app.getMusicSwitch());
        app.getMusicHandler().initButtonSfx(this);

        //BATTLE INITIALIZATION
        txvMessage = (TextView) findViewById(R.id.txv_battle_message);
        btnAction = (Button) findViewById(R.id.btn_battle_action);
        btnAction.setBackgroundColor(PokemonApp.TRANSPARENT_COLOR);

        btnFight = (Button) findViewById(R.id.btn_battle_attack);
        btnPokemon = (Button) findViewById(R.id.btn_battle_pokemon);
        btnBag = (Button) findViewById(R.id.btn_battle_bag);
        btnRun = (Button) findViewById(R.id.btn_battle_run);
        lsvOptions = (ListView)findViewById(R.id.lsv_battle_options);
        app.setFontForContainer(
                (ListView) findViewById(R.id.lsv_battle_options),
                PokemonApp.RETRO_FONT
        );

        DisplayInfoSetBuddy buddyInfo = new DisplayInfoSetBuddy(
                (TextView) findViewById(R.id.txv_battle_buddy_name),
                (TextView) findViewById(R.id.txv_battle_buddy_hp),
                (TextView) findViewById(R.id.txv_battle_buddy_level),
                (ProgressBar) findViewById(R.id.bar_battle_buddy_hp),
                (ProgressBar) findViewById(R.id.bar_battle_buddy_exp),
                (ImageButton) findViewById(R.id.imgbtn_battle_buddy)
        );

        DisplayInfoSet enemyInfo = new DisplayInfoSet(
                (TextView) findViewById(R.id.txv_battle_enemy_name),
                (TextView) findViewById(R.id.txv_battle_enemy_level),
                (ProgressBar) findViewById(R.id.bar_battle_enemy_hp),
                (ImageButton) findViewById(R.id.imgbtn_battle_enemy)
        );

        if(!app.getPokemon(app.getCurrentGoal().getTitle()).isEmpty()){
            battle = new Battle(app, buddyInfo, enemyInfo);
        }
        else{
            battle = new TrainerBattle(app, buddyInfo, enemyInfo);
        }

        //UI INITIALIZATION
        battle.setItemAdapter(new ItemList(BattleActivity.this, battle.getPlayer().getBag()));
        battle.setMoveAdapter(new MoveList(BattleActivity.this, battle.getBuddy().getMoves()));
        battle.setPokemonAdapter(new PokémonList(
                BattleActivity.this,
                battle.getPlayer().getPokemons())
        );

        battle.setBattleState(new BattleStandbyState(
                btnFight,
                btnPokemon,
                btnBag,
                btnRun,
                btnAction,
                lsvOptions,
                battle,
                txvMessage
        ));

        battle.getEnemyInfo().getImage().setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        app.showPokedexDialog(BattleActivity.this, battle.getEnemy().getDexData());
                    }
                }
        );

        battle.getBuddyInfo().getImage().setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getPokemonDialog(battle.getBuddy());
                    }
                }
        );

        lsvOptions.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int pos, long id) {
                app.getMusicHandler().playButtonSfx(app.getSFXSwitch());
                battle.getBattleState().executeLongPressListView(app, BattleActivity.this, pos);
                return true;
            }
        });

        lsvOptions.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
                        app.getMusicHandler().playButtonSfx(app.getSFXSwitch());
                        battle.getBattleState().executeListView(pos);
                    }
                }
        );

        btnFight.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        app.getMusicHandler().playButtonSfx(app.getSFXSwitch());
                        battle.getBattleState().executeFightButton();
                    }
                }
        );
        btnPokemon.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        app.getMusicHandler().playButtonSfx(app.getSFXSwitch());
                        battle.getBattleState().executePokemonButton();
                    }
                }
        );
        btnBag.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        app.getMusicHandler().playButtonSfx(app.getSFXSwitch());
                        battle.getBattleState().executeBagButton();
                    }
                }
        );

        btnRun.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        app.getMusicHandler().playButtonSfx(app.getSFXSwitch());
                        battle.getBattleState().executeRunButton();
                    }
                }
        );

        btnAction.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        app.getMusicHandler().playButtonSfx(app.getSFXSwitch());
                        if(!(battle.getMessageIndex() < battle.getMessages().size())){
                            battle.getMoveAdapter().notifyDataSetChanged();
                            battle.setMoveAdapter(new MoveList(
                                    BattleActivity.this,
                                    battle.getBuddy().getMoves()
                            ));
                            battle.getPokemonAdapter().notifyDataSetChanged();
                            battle.getItemAdapter().notifyDataSetChanged();
                            if(battle.isFinished()){
                                endBattle();
                            }
                        }
                        battle.getBattleState().executeActionButton();
                    }
                }
        );
    }

    /**
     * Ends the battle by finishing this Activity and going to the MainActivity.
     */
    private void endBattle(){
        Intent mainActivityIntent = new Intent(BattleActivity.this, MainActivity.class);
        mainActivityIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivityIfNeeded(mainActivityIntent, 0);
        finish();
    }

    /**
     * Shows a dialog containing information in a given PokémonProfile.
     * @param profile   The PokémonProfile that would be used.
     */
    public void getPokemonDialog(final PokémonProfile profile){
        final PokemonApp app = (PokemonApp) getApplication();
        final Dialog dialog = new Dialog(BattleActivity.this);
        dialog.setContentView(R.layout.pokemon_profile_dialog);
        final EditText edtNickname = (EditText) dialog.findViewById(R.id.edt_profile_nickname);
        edtNickname.setText(profile.getNickname());
        app.loadPokemonDetails(dialog, BattleActivity.this, profile);
        Button btnDialogOk = (Button) dialog.findViewById(R.id.btn_profile_back);
        btnDialogOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                app.getMusicHandler().playButtonSfx(app.getSFXSwitch());

                if(edtNickname.getText().toString().length() > 15){
                    edtNickname.setError("Nickname is too long!");
                }
                else{
                    if(edtNickname.getText().toString().isEmpty()){
                        profile.setNickname(profile.getDexData().getName());
                    }
                    else{
                        profile.setNickname(edtNickname.getText().toString());
                    }
                    battle.getBuddyInfo().updatePokemon(battle.getBuddy());
                    txvMessage.setText("What will " + battle.getBuddy().getNickname() + " do?");
                    dialog.dismiss();
                }
            }
        });
        app.setAsOkButton(btnDialogOk);
    }

    /**
     * Disables the back button.
     */
    @Override
    public void onBackPressed(){

    }

    /**
     * Continues the music when switching activities.
     */
    @Override
    protected void onResume() {
        super.onResume();
        PokemonApp app = (PokemonApp) getApplication();
        if(music == null){
            music.initMusic(this, MusicHandler.MUSIC_BATTLE);
        }
        if(!music.getMusicPlayer().isPlaying()) {
            music.playMusic(app.getMusicSwitch());
        }
    }

    /**
     * Pauses the music when switching activities.
     */
    @Override
    protected void onPause() {
        super.onPause();
        music.getMusicPlayer().pause();
    }
}
