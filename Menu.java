package dianyo.apex;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

public class Menu extends AppCompatActivity {
    private ImageButton helpButton, infoButton, cameraButton, settingButton;
    final int CAMERA_PERMISSION = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        processView();
        pressButton();
    }
    private void processView() {
        helpButton = (ImageButton) findViewById(R.id.helpButton);
        infoButton = (ImageButton) findViewById(R.id.infoButton);
        cameraButton = (ImageButton) findViewById(R.id.cameraButton);
        settingButton = (ImageButton) findViewById(R.id.settingButton);
    }
    private void pressButton() {
        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent contactIntent = new Intent(Menu.this, Contact.class);
                startActivity(contactIntent);
            }
        });

        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent personalIntent = new Intent(Menu.this, Personal.class);
                startActivity(personalIntent);
            }
        });

        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settingIntent = new Intent(Menu.this, Setting.class);
                startActivity(settingIntent);
            }
        });

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] permissions = {"android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE"};

                if (ContextCompat.checkSelfPermission(Menu.this,
                        Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(Menu.this,
                            permissions,
                            CAMERA_PERMISSION
                    );

                }else{
//                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                    startActivity(intent);
                    startActivity(new Intent(Menu.this, Camera.class));
                }
            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case CAMERA_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Intent intent = new Intent(this, Camera.class);
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivity(intent);
                } else {
                Toast.makeText(this, "Please grant camera permission to use the QR Scanner", Toast.LENGTH_SHORT).show();
            }
            return;
        }
    }
}
