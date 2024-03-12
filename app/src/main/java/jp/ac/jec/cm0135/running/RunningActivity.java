package jp.ac.jec.cm0135.running;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class RunningActivity extends AppCompatActivity implements SensorEventListener {

    private TextView dateTextView;
    private TextView txtTemp;
    private Button btnStop;
    public TextView tv_sensor;
    SensorManager sm;
    Sensor sensor_step_detector;
    public static int steps = 0; // static으로 선언

    @SuppressLint({"MissingInflatedId", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_running);

        dateTextView = findViewById(R.id.dateTextView);
        txtTemp = findViewById(R.id.txtTemp);
        btnStop = findViewById(R.id.btnStop);
        tv_sensor = findViewById(R.id.sensor);
        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor_step_detector = sm.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy / MM / dd", Locale.getDefault());
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDate = dateFormat.format(calendar.getTime());
        String currentDate2 = dateFormat2.format(calendar.getTime());

        double currentTemperature = getIntent().getDoubleExtra("currentTemperature", 0.0);
        @SuppressLint("DefaultLocale") String temperatureString = String.format("%.1f", currentTemperature);
        txtTemp.setText(temperatureString + " °C");

        GetTodayStepsTask getTodayStepsTask = new GetTodayStepsTask(new MainActivity(), this);
        getTodayStepsTask.execute();

        // 서비스 시작
        Intent serviceIntent = new Intent(this, RunningService.class);
        startService(serviceIntent);

        // TextView에 날짜 표시
        dateTextView.setText(currentDate);

        // BroadcastReceiver 등록
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int updatedSteps = intent.getIntExtra("steps", 0);
                // 스텝수 업데이트 또는 다른 작업 수행
                tv_sensor.setText(String.valueOf(updatedSteps));
            }
        };

        IntentFilter filter = new IntentFilter("steps-update");
        registerReceiver(receiver, filter);

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 서비스 종료
                Intent serviceIntent = new Intent(RunningActivity.this, RunningService.class);
                stopService(serviceIntent);

                // SendDataTask를 생성할 때 Handler를 전달
                new SendDataTask().execute(currentDate2, String.valueOf(steps), "0", "0");

                // MainActivity 시작
                Intent intent = new Intent(RunningActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        sm.registerListener(this, sensor_step_detector, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sm.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    // 센서값이 변할때
    @Override
    public void onSensorChanged(SensorEvent event) {
        // 센서 유형이 스텝감지 센서인 경우 걸음수 +1
        switch (event.sensor.getType()) {
            case Sensor.TYPE_STEP_DETECTOR:
//                steps++;
//                Log.i("iii", "iii Front : " + steps);

                // 브로드캐스트 메시지 전송
                Intent intent = new Intent("steps-update");
                intent.putExtra("steps", steps);
                sendBroadcast(intent);
                break;
        }
    }
}



//    private class SendDataTask extends AsyncTask<String, Void, Void> {
//        @Override
//        protected Void doInBackground(String... params) {
//            String date = params[0];
//            String stepsStr = params[1];
//            int steps = Integer.parseInt(stepsStr);  // 걸음 수를 정수로 변환
//            double calories = steps * CALORIES_PER_STEP;
//            double distance = steps * DISTANCE_PER_STEP;
//
//            try {
//                String serverUrl = "http://22cm0135.main.jp/running/runningDB";
//                URL url = new URL(serverUrl);
//                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//                conn.setRequestMethod("POST");
//                conn.setDoOutput(true);
//
//                // POST 데이터 생성
//                HashMap<String, String> postDataParams = new HashMap<>();
//                postDataParams.put("date", date);
//                postDataParams.put("steps", stepsStr);
//                postDataParams.put("calories", String.format(Locale.US, "%.1f", calories));
//                postDataParams.put("distance", String.format(Locale.US, "%.1f", distance));
//
//                // 데이터 전송
//                OutputStream os = conn.getOutputStream();
//                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
//                writer.write(getPostDataString(postDataParams));
//                writer.flush();
//                writer.close();
//                os.close();
//
//                // 서버 응답 확인
//                int responseCode = conn.getResponseCode();
//                if (responseCode == HttpURLConnection.HTTP_OK) {
//                    // 전송 성공
//                    Log.d("aaa", "true: " + date);
//                    Log.d("aaa", "true: " + stepsStr);
//                    Log.d("aaa", "true: " + String.format(Locale.US, "%.1f", calories));
//                    Log.d("aaa", "true: " + String.format(Locale.US, "%.1f", distance));
//                } else {
//                    // 전송 실패
//                    Log.d("aaa", "false: " + steps);
//                    Log.d("aaa", "false: " + String.format(Locale.US, "%.1f", calories));
//                    Log.d("aaa", "false: " + String.format(Locale.US, "%.1f", distance));
//                }
//
//                conn.disconnect();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//            return null;
//        }
//
//        private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
//            StringBuilder result = new StringBuilder();
//            boolean first = true;
//
//            for (Map.Entry<String, String> entry : params.entrySet()) {
//                if (first) {
//                    first = false;
//                } else {
//                    result.append("&");
//                }
//
//                result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
//                result.append("=");
//                result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
//            }
//
//            return result.toString();
//        }
//    }

