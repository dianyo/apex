package dianyo.apex;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Debug;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {
    private ImageButton loginButton;
    private EditText userName;
    private EditText password;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = getSharedPreferences(getString(R.string.preference_key), Context.MODE_PRIVATE);
        processViews();
        login();
    }

    private void processViews() {
        loginButton = (ImageButton) findViewById(R.id.loginButton);
        userName = (EditText) findViewById(R.id.loginUserNameText);
        password = (EditText) findViewById(R.id.loginPasswordText);
    }

    private void login() {
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //send the login message to back-end
                //demo userName:Joe password:12345
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if (userName.getText().toString().equals("Joe")) {
                    if (password.getText().toString().equals("12345")) {
                        editor.putInt("User", 0);
                        editor.apply();
                        Intent menuIntent = new Intent(MainActivity.this, Menu.class);
                        startActivity(menuIntent);
                    }
                } else if (userName.getText().toString().equals("Eileen")
                        && password.getText().toString().equals("12345")) {
                    editor.putInt("User", 1);
                    editor.apply();
                    Intent menuIntent = new Intent(MainActivity.this, Menu.class);
                    startActivity(menuIntent);
                }
            }
        });
    }
}
