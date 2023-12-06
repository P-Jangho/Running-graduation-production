package jp.ac.jec.cm0135.running;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class RunningActivity extends AppCompatActivity implements SensorEventListener {

    private TextView dateTextView;
    private Button btnStop;
    TextView tv_sensor;
    SensorManager sm;
    Sensor sensor_step_detector;
    int steps = 0;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_running);

        dateTextView = findViewById(R.id.dateTextView);
        btnStop = findViewById(R.id.btnStop);

        tv_sensor = (TextView)findViewById(R.id.sensor);        // 텍스트뷰 인식
        tv_sensor.setText("0"); // 걸음 수 초기화 및 출력
        sm = (SensorManager)getSystemService(SENSOR_SERVICE);   // 센서 매니저 생성
        sensor_step_detector = sm.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);  // 스템 감지 센서 등록

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RunningActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // 현재 날짜 가져오기
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy / MM / dd", Locale.getDefault());
        String currentDate = dateFormat.format(calendar.getTime());

        // TextView에 날짜 표시
        dateTextView.setText(currentDate);
    }

    @Override
    protected void onResume(){
        super.onResume();
        sm.registerListener(this, sensor_step_detector, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause(){
        super.onPause();
        sm.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy){

    }

    // 센서값이 변할때
    @Override
    public void onSensorChanged(SensorEvent event){

        // 센서 유형이 스텝감지 센서인 경우 걸음수 +1
        switch (event.sensor.getType()){
            case Sensor.TYPE_STEP_DETECTOR:
                tv_sensor.setText("" + (++steps));
                break;
        }
    }
}