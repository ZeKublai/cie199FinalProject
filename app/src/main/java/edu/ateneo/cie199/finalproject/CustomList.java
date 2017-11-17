package edu.ateneo.cie199.finalproject;

import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by John on 10/8/2017.
 */

public class CustomList extends ArrayAdapter<PokemonProfile>{

    private final Activity context;
    private final ArrayList<PokemonProfile> profiles;
    Typeface font;

    public CustomList(Activity context, ArrayList<PokemonProfile> profiles) {
        super(context, R.layout.list_single, profiles);
        this.context = context;
        this.profiles = profiles;
        font = Typeface.createFromAsset(context.getAssets(), "generation6.ttf");
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.list_single, null, true);
        TextView txtName = (TextView) rowView.findViewById(R.id.txv_pokemon_name);
        TextView txtHp = (TextView) rowView.findViewById(R.id.txv_pokemon_hp);
        ImageView imageIcon = (ImageView) rowView.findViewById(R.id.img_pokemon_icon);
        ProgressBar barHp = (ProgressBar) rowView.findViewById(R.id.bar_pokemon_hp);

        txtName.setTypeface(font);
        txtHp.setTypeface(font);
        rowView.setBackground(PokemonGoApp.getShape(PokemonGoApp.POKEMON_COLOR));
        PokemonGoApp.updateHpBarColor(profiles.get(position).getCurrentHP(), profiles.get(position).getHP(), barHp);
        barHp.setMax(profiles.get(position).getHP());
        barHp.setProgress(profiles.get(position).getCurrentHP());
        txtName.setText(profiles.get(position).getNickname());
        txtHp.setText("HP " + profiles.get(position).getCurrentHP() + "/" + profiles.get(position).getHP());
        imageIcon.setImageResource(profiles.get(position).getDexData().getIcon());

        return rowView;
    }
}