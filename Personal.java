package dianyo.apex;

import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class Personal extends AppCompatActivity {
    private ImageButton backButton, backButton1;
    private ImageButton cautionButton, scheduleButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal);
        processView();
        pressButton();
    }
    private void processView() {
        backButton = (ImageButton) findViewById(R.id.backButton);
        cautionButton = (ImageButton) findViewById(R.id.cautionButton);
        scheduleButton = (ImageButton) findViewById(R.id.scheduleButton);
    }
    private void processViewInCaution() {
        backButton1 = (ImageButton) findViewById(R.id.backButton1);
    }
    private void pressButton() {
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("click back");
                finish();
            }
        });

        cautionButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Personal.this.setContentView(R.layout.personal_caution);
                processViewInCaution();
                pressButtonInCaution();
            }
        });

        scheduleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Personal.this.setContentView(R.layout.personal_schedule);
            }
        });
    }
    private void pressButtonInCaution() {
        backButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("click back");
                Personal.this.setContentView(R.layout.activity_personal);
                processView();
                pressButton();
            }
        });
    }

}
