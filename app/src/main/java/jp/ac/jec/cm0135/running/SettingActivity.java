package jp.ac.jec.cm0135.running;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class SettingActivity extends AppCompatActivity {

    public static final String PREFERENCE_NAME = "MyPrefs";
    public static final String KEY_S = "sKey";
    public static final String KEY_C = "cKey";
    public static final String KEY_R = "rKey";

    private Button btnOK;
    private Button btnTime;
    private Button btnReset; // New reset button
    private EditText edtS;
    private EditText edtC;
    private EditText edtR;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        btnOK = findViewById(R.id.btnOK);
        btnTime = findViewById(R.id.btnTime);
        btnReset = findViewById(R.id.btnReset); // Initialize the reset button
        edtS = findViewById(R.id.edtS);
        edtC = findViewById(R.id.edtC);
        edtR = findViewById(R.id.edtR);

        // Load values from SharedPreferences and set them to EditText
        SharedPreferences preferences = getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        edtS.setText(preferences.getString(KEY_S, ""));
        edtC.setText(preferences.getString(KEY_C, ""));
        edtR.setText(preferences.getString(KEY_R, ""));

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Save values to SharedPreferences
                SharedPreferences.Editor editor = getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE).edit();
                editor.putString(KEY_S, edtS.getText().toString());
                editor.putString(KEY_C, edtC.getText().toString());
                editor.putString(KEY_R, edtR.getText().toString());
                editor.apply();

                // After saving, you can navigate to another activity if needed
                Intent intent = new Intent(SettingActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        btnTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingActivity.this, TimeActivity.class);
                startActivity(intent);
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Reset values in SharedPreferences
                SharedPreferences.Editor editor = getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE).edit();
                editor.remove(KEY_S);
                editor.remove(KEY_C);
                editor.remove(KEY_R);
                editor.apply();

                // Reset EditText values to empty
                edtS.setText("");
                edtC.setText("");
                edtR.setText("");
            }
        });

//        btnReset.setVisibility(View.GONE);
    }
}
