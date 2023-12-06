package jp.ac.jec.cm0135.running;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class TimeActivity extends AppCompatActivity {

    private Spinner hourSpinner1;
    private Spinner hourSpinner2;
    private Button btnOK;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time);

        hourSpinner1 = findViewById(R.id.hourSpinner1);
        hourSpinner2 = findViewById(R.id.hourSpinner2);
        btnOK = findViewById(R.id.btnOK);

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TimeActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // 시간 스피너 설정
        setupHourSpinner(hourSpinner1);
        setupHourSpinner(hourSpinner2);
    }

    private void setupHourSpinner(Spinner spinner) {
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

        // 스피너 아이템 선택 이벤트 처리
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedHour = (String) parentView.getSelectedItem();
                // 원하는 동작 추가
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // 아무 것도 선택되지 않았을 때의 처리
            }
        });
    }
}
