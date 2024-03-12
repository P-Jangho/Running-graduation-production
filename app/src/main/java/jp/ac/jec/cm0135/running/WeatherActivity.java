package jp.ac.jec.cm0135.running;

import static jp.ac.jec.cm0135.running.BuildConfig.MY_KEY;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherActivity extends AppCompatActivity {

    private LinearLayout layoutWeather;
    private static final String API_KEY = MY_KEY;
    private double latitude;
    private double longitude;
    ImageView[] weatherViews = new ImageView[7];
    TextView[] textViews = new TextView[7];
    private ImageButton btnBack;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        layoutWeather = findViewById(R.id.layoutWeather);
        // Initialize ImageView array elements
        weatherViews[0] = findViewById(R.id.weatherToday);
        weatherViews[1] = findViewById(R.id.weather1);
        weatherViews[2] = findViewById(R.id.weather2);
        weatherViews[3] = findViewById(R.id.weather3);
        weatherViews[4] = findViewById(R.id.weather4);
        weatherViews[5] = findViewById(R.id.weather5);
        weatherViews[6] = findViewById(R.id.weather6);

        textViews[0] = findViewById(R.id.text);
        textViews[1] = findViewById(R.id.text1);
        textViews[2] = findViewById(R.id.text2);
        textViews[3] = findViewById(R.id.text3);
        textViews[4] = findViewById(R.id.text4);
        textViews[5] = findViewById(R.id.text5);
        textViews[6] = findViewById(R.id.text6);

        btnBack = findViewById(R.id.btnBack);

        Intent intent = getIntent();
        latitude = intent.getDoubleExtra("lat", 0.0);
        longitude = intent.getDoubleExtra("lon", 0.0);
        // Weather 액티비티에서의 코드
        int backgroundColor = getIntent().getIntExtra("backgroundColor", Color.WHITE);
        layoutWeather.setBackgroundColor(backgroundColor);

        new WeatherTask().execute();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WeatherActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
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

                    for (int i = 0; i < 7; i++) {
                        Log.i("aaa", "ccc " + i);
                        JsonObject firstDayObject = daysArray.get(i).getAsJsonObject();

                        String datetime = firstDayObject.getAsJsonPrimitive("datetime").getAsString();
                        double temp = firstDayObject.getAsJsonPrimitive("temp").getAsDouble();
                        String icon = firstDayObject.getAsJsonPrimitive("icon").getAsString();

//                    UI 업데이트를 메인 스레드에서 수행
                        int finalI = i;
                        runOnUiThread(() -> {
                            if (icon.equals("partly-cloudy-day")) {
                                weatherViews[finalI].setImageResource(R.drawable.cloud);
                            }else if (icon.equals("cloudy")) {
                                weatherViews[finalI].setImageResource(R.drawable.cloudy);
                            }else if (icon.equals("rain")) {
                                weatherViews[finalI].setImageResource(R.drawable.rain);
                            }else if (icon.equals("snow")) {
                                weatherViews[finalI].setImageResource(R.drawable.snow);
                            }
//                            else if (icon.equals("wind")) {
//                                weatherViews[finalI].setImageResource(R.drawable.wind);
//                            }

                            String weatherInfo = datetime +  "\n" + temp + "°C";
//                            "\n" + icon +
                            textViews[finalI].setText(weatherInfo);
                        });

                    }

                } else {
                    Log.e("WeatherActivity", "No data available in 'days' array");
                }
            } catch (Exception e) {
                Log.e("WeatherActivity", "Error parsing JSON", e);
            }
        }
    }
}