package jp.ac.jec.cm0135.running;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class TimeActivity extends AppCompatActivity {

    private Spinner hourSpinner1;
    private Spinner hourSpinner2;
    private Button btnOK;
    private Button btnReset;

    public static final String PREFS_NAME = "MyPrefs1";
    public static final String FIRST_SPINNER_HOUR_KEY = "first_spinner_hour";
    public static final String SECOND_SPINNER_HOUR_KEY = "second_spinner_hour";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time);

        hourSpinner1 = findViewById(R.id.hourSpinner1);
        hourSpinner2 = findViewById(R.id.hourSpinner2);
        btnOK = findViewById(R.id.btnOK);
        btnReset = findViewById(R.id.btnReset);

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // SharedPreferences에 시간 저장
                saveTimeToPreferences(FIRST_SPINNER_HOUR_KEY, hourSpinner1.getSelectedItem().toString());
                saveTimeToPreferences(SECOND_SPINNER_HOUR_KEY, hourSpinner2.getSelectedItem().toString());

                Intent intent = new Intent(TimeActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 리셋 버튼 클릭 시 스피너 초기화
                resetSpinners();
            }
        });

        // 시간 스피너 설정
        setupHourSpinner(hourSpinner1, FIRST_SPINNER_HOUR_KEY);
        setupHourSpinner(hourSpinner2, SECOND_SPINNER_HOUR_KEY);
        Log.i("ccc", "ccc : "  + hourSpinner1.getSelectedItem().toString() + " " + hourSpinner2.getSelectedItem().toString());
    }

    private void resetSpinners() {
        // 시간 스피너 초기화
        hourSpinner1.setSelection(0);
        hourSpinner2.setSelection(0);

        // SharedPreferences에 저장된 값도 초기화
        saveTimeToPreferences(FIRST_SPINNER_HOUR_KEY, "0");
        saveTimeToPreferences(SECOND_SPINNER_HOUR_KEY, "0");

        showToast("初期化");
    }

    private void setupHourSpinner(Spinner spinner, String key) {
        // 0부터 23시까지의 시간 배열 생성
        String[] hours = new String[24];
        for (int i = 0; i < 24; i++) {
            hours[i] = String.valueOf(i);
        }

        // 어댑터 설정
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, hours);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // 스피너에 어댑터 연결
        spinner.setAdapter(adapter);

        // SharedPreferences에서 저장된 값을 가져와서 스피너에 설정
        String savedHour = getTimeFromPreferences(key);
        if (savedHour != null) {
            int position = adapter.getPosition(savedHour);
            spinner.setSelection(position);
        }

        // 스피너 아이템 선택 이벤트 처리
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedHour = (String) parentView.getSelectedItem();

                // 첫 번째 스피너에서 선택한 시간과 두 번째 스피너에서 선택한 시간을 비교
                String firstSpinnerHour = (String) hourSpinner1.getSelectedItem();
                if (Integer.parseInt(selectedHour) < Integer.parseInt(firstSpinnerHour)) {
                    // 두 번째 스피너가 첫 번째 스피너보다 전 시간일 경우 토스트 메시지 표시
                    showToast("翌日 " + selectedHour + " 時に設定されます。");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // 아무 것도 선택되지 않았을 때의 처리
            }
        });
    }

    private void saveTimeToPreferences(String key, String value) {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    private String getTimeFromPreferences(String key) {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return preferences.getString(key, null);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
