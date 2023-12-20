package jp.ac.jec.cm0135.running;

import static jp.ac.jec.cm0135.running.BuildConfig.MY_KEY;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherActivity extends AppCompatActivity {

    private static final String API_KEY = MY_KEY;
    private double latitude;
    private double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        Intent intent = getIntent();
        latitude = intent.getDoubleExtra("lat", 0.0);
        longitude = intent.getDoubleExtra("lon", 0.0);

        new WeatherTask().execute();
    }

    private class WeatherTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                // 위치 정보를 가져와서 API에 요청하는 코드
                String apiUrl = "https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/"
                        + latitude + "," + longitude
                        + "?unitGroup=metric&include=days%2Ccurrent&key=" + API_KEY + "&contentType=json";

                Log.i("aaa", "url " + apiUrl);

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
//                        weatherInfoTextView.setText(weatherInfo);
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