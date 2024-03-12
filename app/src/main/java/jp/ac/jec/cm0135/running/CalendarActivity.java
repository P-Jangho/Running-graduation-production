package jp.ac.jec.cm0135.running;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CalendarView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CalendarActivity extends AppCompatActivity {

    private CalendarView calendarView;
    private ImageButton btnGauge;
    private ImageButton btnCal;
    private ImageButton btnGraph;
    private ImageButton btnBack;
    private ImageButton btnHome;
    private TextView txtDate;
    private TextView txtCalories;
    private TextView txtDistance;
    private TextView txtSteps;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        calendarView = findViewById(R.id.calendarView);
        btnGauge = findViewById(R.id.btnGauge);
        btnCal = findViewById(R.id.btnCal);
        btnGraph = findViewById(R.id.btnGraph);
        btnBack = findViewById(R.id.btnBack);
        btnHome= findViewById(R.id.btnHome);
        txtDate = findViewById(R.id.txtDate);
        txtCalories = findViewById(R.id.txtCalories);
        txtDistance = findViewById(R.id.txtDistance);
        txtSteps = findViewById(R.id.txtSteps);

        btnCal.setAlpha(0.1f);
        btnGauge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CalendarActivity.this, GaugeActivity.class);
                startActivity(intent);
            }
        });

        btnGraph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CalendarActivity.this, GraphActivity.class);
                startActivity(intent);
            }
        });

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CalendarActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CalendarActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                // 선택한 날짜의 정보를 가져오기
                fetchDataForSelectedDate(year, month, dayOfMonth);
            }
        });

        // 액티비티가 생성되면서 자동으로 오늘 날짜의 정보를 가져와 표시
        Date currentDate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String formattedDate = sdf.format(currentDate);
        fetchDataForSelectedDate(currentDate.getYear() + 1900, currentDate.getMonth(), currentDate.getDate());
    }

    private void fetchDataForSelectedDate(int year, int month, int dayOfMonth) {
        // 날짜 형식을 맞춰서 서버에 요청하기
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date selectedDate = new Date(year - 1900, month, dayOfMonth);
        String formattedDate = sdf.format(selectedDate);

        // AsyncTask를 사용하여 서버에서 데이터 가져오기
        new FetchDataFromServerTask().execute(formattedDate);
    }

    private class FetchDataFromServerTask extends AsyncTask<String, Void, JSONArray> {
        @Override
        protected JSONArray doInBackground(String... params) {
            try {
                String serverUrl = "http://22cm0135.main.jp/running/getDailyActivity.php?date=" + params[0];
                URL url = new URL(serverUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                Log.i("zzz", "zzz " + serverUrl);

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                    return new JSONArray(response.toString());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @SuppressLint({"DefaultLocale", "SetTextI18n"})
        @Override
        protected void onPostExecute(JSONArray result) {
            if (result != null && result.length() > 0) {
                try {
                    // JSON에서 첫 번째 데이터 추출
                    JSONObject data = result.getJSONObject(0);

                    // 데이터에서 필요한 정보 추출
                    String date = data.getString("date");
                    int steps = data.getInt("steps");
                    double calories = data.getDouble("calories");
                    double distance = data.getDouble("distance");

                    txtSteps.setVisibility(View.VISIBLE);
                    txtCalories.setVisibility(View.VISIBLE);
                    txtDistance.setVisibility(View.VISIBLE);

                    // 결과를 텍스트 뷰에 표시
                    txtDate.setText(date);
                    txtSteps.setText(steps + " steps");
                    txtCalories.setText(String.format("%s kcal", calories));
                    txtDistance.setText(String.format("%.3f km", distance / 1000.0));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                // 데이터가 없을 경우
                Toast.makeText(CalendarActivity.this, "NO DATA", Toast.LENGTH_SHORT).show();
                // 텍스트 뷰 초기화
                txtDate.setText("NO DATA");
                txtSteps.setVisibility(View.GONE);
                txtCalories.setVisibility(View.GONE);
                txtDistance.setVisibility(View.GONE);
            }
        }
    }
}
