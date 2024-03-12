package jp.ac.jec.cm0135.running;

import static jp.ac.jec.cm0135.running.MainActivity.KEY_DAY_STEPS;
import static jp.ac.jec.cm0135.running.MainActivity.KEY_WEEKLY_STEPS;
import static jp.ac.jec.cm0135.running.MainActivity.WEEK_CALORIES;
import static jp.ac.jec.cm0135.running.MainActivity.WEEK_DISTANCE;
import static jp.ac.jec.cm0135.running.MainActivity.WEEK_STEPS;
import static jp.ac.jec.cm0135.running.SettingActivity.PREFERENCE_NAME;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GaugeActivity extends AppCompatActivity {

    private ProgressBar gaugeProgressBarToday;
    private ProgressBar gaugeProgressBarWeek;
    private ImageButton btnGauge;
    private ImageButton btnCal;
    private ImageButton btnGraph;
    private ImageButton btnBack;
    private ImageButton btnHome;
    private TextView txtSteps;
    private TextView txtCalories;
    private TextView txtDistance;
    private TextView txtStepsWeek;
    private TextView txtCaloriesWeek;
    private TextView txtDistanceWeek;
    private int steps;
    private Double distance;
    private Double calories;
    private int weekSteps;
    private Double weekDistance;
    private Double weekCalories;
    private int receivedWeekSteps;
    private int receivedTodaySteps = 0;

    @SuppressLint({"MissingInflatedId", "DefaultLocale"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gauge);

        gaugeProgressBarToday = findViewById(R.id.gaugeProgressBarT);
        gaugeProgressBarWeek = findViewById(R.id.gaugeProgressBarW);
        btnGauge = findViewById(R.id.btnGauge);
        btnCal = findViewById(R.id.btnCal);
        btnGraph = findViewById(R.id.btnGraph);
        btnHome = findViewById(R.id.btnHome);
        btnBack = findViewById(R.id.btnBack);
        txtSteps = findViewById(R.id.txtSteps);
        txtCalories = findViewById(R.id.txtCalories);
        txtDistance = findViewById(R.id.txtDistance);
        txtStepsWeek = findViewById(R.id.txtStepsWeek);
        txtCaloriesWeek = findViewById(R.id.txtCaloriesWeek);
        txtDistanceWeek = findViewById(R.id.txtDistanceWeek);

        new GetTodayStepsTask().execute();

        SharedPreferences preferences = getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        receivedTodaySteps = preferences.getInt(KEY_DAY_STEPS, 0);
        receivedWeekSteps = preferences.getInt(KEY_WEEKLY_STEPS, 0);
        weekSteps = preferences.getInt(WEEK_STEPS, 0);
        weekDistance = (double) preferences.getLong(WEEK_DISTANCE, 0);
        weekCalories = (double) preferences.getLong(WEEK_CALORIES, 0);

        txtStepsWeek.setText(String.format("%d / %d", weekSteps, receivedWeekSteps));
        txtDistanceWeek.setText(String.format("%.3f km", weekDistance / 1000.0));
        txtCaloriesWeek.setText(String.format("%s kcal", weekCalories));

        // 비율 계산
        double progressRatio = (double) weekSteps / receivedWeekSteps;

        // 게이지 업데이트
        int progress = (int) (progressRatio * 100);
        gaugeProgressBarWeek.setProgress(progress);

        btnGauge.setAlpha(0.1f);

        btnCal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GaugeActivity.this, CalendarActivity.class);
                startActivity(intent);
            }
        });

        btnGraph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GaugeActivity.this, GraphActivity.class);
                startActivity(intent);
            }
        });

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GaugeActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GaugeActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private class GetTodayStepsTask extends AsyncTask<Void, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(Void... params) {
            try {
                String serverUrl = "http://22cm0135.main.jp/running/getTodaySteps.php";
                URL url = new URL(serverUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                // 서버 응답 확인
                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // 서버에서 JSON 형식의 결과를 읽어오기
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    // JSON 파싱
                    return new JSONObject(response.toString());
                }
                conn.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        @SuppressLint("DefaultLocale")
        @Override
        protected void onPostExecute(JSONObject result) {
            super.onPostExecute(result);
            if (result != null) {
                try {
                    // 서버에서 받아온 값들 추출
                    int serverSteps = result.getInt("steps");

                    // 비율 계산
                    double progressRatio = (double) serverSteps / receivedTodaySteps;

                    // 게이지 업데이트
                    int progress = (int) (progressRatio * 100);
                    gaugeProgressBarToday.setProgress(progress);

                    // 나머지 UI 업데이트
                    double serverCalories = result.getDouble("calories");
                    double serverDistance = result.getDouble("distance");

                    steps = serverSteps;
                    distance = serverDistance;
                    calories = serverCalories;
                    txtSteps.setText(String.format("%d / %d", steps, receivedTodaySteps));
                    txtDistance.setText(String.format("%.3f km", distance / 1000.0));
                    txtCalories.setText(String.format("%s kcal", calories));
                    Log.i("eee", "eee " + progress + " " + steps + " " + distance + " " + calories);
                    // calories와 distance 업데이트도 수행
                    // (serverCalories와 serverDistance 변수를 활용하여 업데이트하는 로직이 필요)
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
