package dianyo.apex;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

public class ShowImage extends AppCompatActivity {
    private ImageButton closeButton, gridButton, saveImageButton, backButton;
    private ImageView woundImageView, woundInfoView;
    private int woundOrigin, wound, woundGrid, woundInfo;
    private boolean grid = false;
    private SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image);
        sharedPreferences = getSharedPreferences(getString(R.string.preference_key), Context.MODE_PRIVATE);
        woundType();
        processView();
        woundImageView.setImageResource(woundOrigin);
        buttonClick();
    }

    private void woundType(){
        // get the type
        int type = sharedPreferences.getInt("User", -1);
        if (type == 0){
            wound = R.drawable.wound1;
            woundGrid = R.drawable.wound1_grid;
            woundOrigin = R.drawable.wound1_origin;
            woundInfo = R.drawable.wound1_info;
        } else if (type == 1){
            wound = R.drawable.wound2;
            woundGrid = R.drawable.wound2_grid;
            woundOrigin = R.drawable.wound2_origin;
            woundInfo = R.drawable.wound2_info;
        } else {
            Log.e("image", "shared preference error");
        }
    }
    private void processView(){
        closeButton = (ImageButton) findViewById(R.id.closeButton);
        gridButton = (ImageButton) findViewById(R.id.gridButton);
        gridButton.setEnabled(false);
        saveImageButton = (ImageButton) findViewById(R.id.saveImageButton);
        backButton = (ImageButton) findViewById(R.id.backButton);
        woundImageView = (ImageView) findViewById(R.id.woundImageView);
        woundInfoView = (ImageView) findViewById(R.id.woundInfoView);
        woundInfoView.setImageResource(woundInfo);
    }

    private void buttonClick(){
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        gridButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("image", "click grid button");
                if (grid)
                    woundImageView.setImageResource(wound);
                else {
                    woundImageView.setImageResource(woundGrid);
                }
                grid = !grid;
            }
        });

        saveImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                woundInfoView.setVisibility(View.VISIBLE);
                backButton.setClickable(false);
                backButton.setVisibility(View.INVISIBLE);
                saveImageButton.setClickable(false);
                saveImageButton.setVisibility(View.INVISIBLE);
                woundImageView.setImageResource(woundGrid);
                gridButton.setEnabled(true);
            }
        });
    }

}
