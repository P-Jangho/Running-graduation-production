package jp.ac.jec.cm0135.running;

import static jp.ac.jec.cm0135.running.BuildConfig.MY_KEY;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView dateTextView;
    private ProgressBar progressBar;
    private TextView progressText;
    int i = 0;

    private Button btnStart;
    private ImageButton btnSet;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private FusedLocationProviderClient fusedLocationClient;
    private TextView weatherInfoTextView; // 추가된 부분
    private static final String API_KEY = MY_KEY;
    private double latitude;
    private double longitude;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dateTextView = findViewById(R.id.dateTextView);

        // set the id for the progressbar and progress text
        progressBar = findViewById(R.id.progress_bar);
        progressText = findViewById(R.id.progress_text);

        btnStart = findViewById(R.id.btnStart);
        btnSet = findViewById(R.id.btnSet);

        weatherInfoTextView = findViewById(R.id.weatherInfoTextView); // 추가된 부분
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // 권한이 없는 경우 권한을 요청합니다.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            // 권한이 있는 경우 위치 업데이트를 시작합니다.
            requestLocationUpdates();
            new WeatherTask().execute();
        }

        // 현재 날짜 가져오기
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy / MM / dd", Locale.getDefault());
        String currentDate = dateFormat.format(calendar.getTime());

        // TextView에 날짜 표시
        dateTextView.setText(currentDate);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, RunningActivity.class);
                startActivity(intent);
            }
        });

        btnSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });

        progressBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 프로그레스바를 클릭하면 다른 액티비티로 이동
                Intent intent = new Intent(MainActivity.this, GaugeActivity.class);
                startActivity(intent);
            }
        });
    }

    private void requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        // 위치를 가져와서 텍스트뷰에 표시합니다.
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
//                        String locationText = "위도: " + latitude + "\n경도: " + longitude;
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 권한이 승인되면 위치 업데이트를 시작합니다.
                requestLocationUpdates();
            } else {
                // 권한이 거부되면 처리할 내용을 추가할 수 있습니다.
            }
        }
    }

    private class WeatherTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                // 위치 정보를 가져와서 API에 요청하는 코드
                String apiUrl = "https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/"
                        + latitude + "," + longitude
                        + "?unitGroup=metric&include=days%2Ccurrent&key=" + API_KEY + "&contentType=json";

                URL url = new URL(apiUrl);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                try {
                    // API 응답을 읽어오기
                    BufferedReader bufferedReader = new BufferedReader(
                            new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();

                    // JSON 파싱
                    String jsonResult = stringBuilder.toString();
                    parseJson(jsonResult);
                } finally {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                Log.e("WeatherActivity", "Error fetching weather data", e);
            }
            return null;
        }

        private void parseJson(String json) {
            try {
                // JSON을 JsonObject로 파싱
                JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();

                // "days" 배열 가져오기
                JsonArray daysArray = jsonObject.getAsJsonArray("days");

                if (daysArray != null && daysArray.size() > 0) {
                    // "days" 배열의 첫 번째 요소 가져오기
                    JsonObject firstDayObject = daysArray.get(0).getAsJsonObject();

                    // "temp"과 "icon" 필드 추출
                    double temp = firstDayObject.getAsJsonPrimitive("temp").getAsDouble();
                    String icon = firstDayObject.getAsJsonPrimitive("icon").getAsString();

                    // UI 업데이트를 메인 스레드에서 수행
                    runOnUiThread(() -> {
                        // 날씨 정보를 TextView에 표시
                        String weatherInfo = "날씨: " + icon + "\n온도: " + temp + "°C";
                        weatherInfoTextView.setText(weatherInfo);
                    });
                } else {
                    Log.e("WeatherActivity", "No data available in 'days' array");
                }
            } catch (Exception e) {
                Log.e("WeatherActivity", "Error parsing JSON", e);
            }
        }
    }
}
