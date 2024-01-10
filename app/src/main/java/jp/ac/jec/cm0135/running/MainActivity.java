package jp.ac.jec.cm0135.running;

import static jp.ac.jec.cm0135.running.BuildConfig.MY_KEY;
import static jp.ac.jec.cm0135.running.BuildConfig.MY_KEY2;

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
import android.widget.ImageView;
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
    private Button btnStart;
    private ImageButton btnSet;
    private ImageView sunImageView;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private FusedLocationProviderClient fusedLocationClient;
    private TextView weatherInfoTextView;
    private static final String API_KEY = MY_KEY;
    private static final String API_KEY2 = MY_KEY2;
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
        sunImageView = findViewById(R.id.sunImageView);

        weatherInfoTextView = findViewById(R.id.weatherInfoTextView);
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
//            new WeatherTask().execute();
//            new WeatherTask2().execute();
        }

        // 현재 날짜 가져오기
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy / MM / dd", Locale.getDefault());
        String currentDate = dateFormat.format(calendar.getTime());

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

        sunImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, WeatherActivity.class);
                intent.putExtra("lat", latitude);
                intent.putExtra("lon", longitude);
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

                        new WeatherTask2().execute();
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

    private class WeatherTask2 extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                // Construct the URL for the OpenWeatherMap API
                String apiUrl = "https://api.openweathermap.org/data/2.5/weather" +
                        "?lat=" + latitude +
                        "&lon=" + longitude +
                        "&appid=" + API_KEY2;

                URL url = new URL(apiUrl);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                try {
                    // Read the API response
                    BufferedReader bufferedReader = new BufferedReader(
                            new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();

                    // Parse JSON response
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
                // Parse the JSON response
                JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();

                // Extract temperature-related information
                JsonObject mainObject = jsonObject.getAsJsonObject("main");
                double currentTemp = mainObject.getAsJsonPrimitive("temp").getAsDouble();
                double feelsLikeTemp = mainObject.getAsJsonPrimitive("feels_like").getAsDouble();
                double minTemp = mainObject.getAsJsonPrimitive("temp_min").getAsDouble();
                double maxTemp = mainObject.getAsJsonPrimitive("temp_max").getAsDouble();

                // Extract weather icon
                JsonArray weatherArray = jsonObject.getAsJsonArray("weather");
                if (weatherArray != null && weatherArray.size() > 0) {
                    JsonObject weatherObject = weatherArray.get(0).getAsJsonObject();
                    String weatherIcon = weatherObject.getAsJsonPrimitive("icon").getAsString();

                    Log.i("aaa", "weatherICON" + weatherIcon);

                    if (weatherIcon.contains("02")) {
                        sunImageView.setImageResource(R.drawable.cloud);
                    } else if (weatherIcon.contains("03") || weatherIcon.contains("04")) {
                        sunImageView.setImageResource(R.drawable.cloudy);
                    } else if (weatherIcon.contains("09") || weatherIcon.contains("10")) {
                        sunImageView.setImageResource(R.drawable.rain);
                    } else if (weatherIcon.contains("11")) {
                        sunImageView.setImageResource(R.drawable.cloudy);
                    } else if (weatherIcon.contains("13")) {
                        sunImageView.setImageResource(R.drawable.cloudy);
                    } else if (weatherIcon.contains("50")) {
                        sunImageView.setImageResource(R.drawable.cloudy);
                    } else {
                        return;
                    }

                    // Update UI on the main thread
                    runOnUiThread(() -> {
                        // Display temperature and weather icon information in TextViews
                        String temperatureInfo = String.format(
                                "현재 온도: %.2f°C\n체감 온도: %.2f°C\n최저 온도: %.2f°C\n최고 온도: %.2f°C",
                                currentTemp - 273.15, feelsLikeTemp - 273.15,
                                minTemp - 273.15, maxTemp - 273.15);

                        weatherInfoTextView.setText(temperatureInfo);

                        // Use weatherIcon as needed, for example, set it to an ImageView
                        // imageView.setImageResource(getIconResourceId(weatherIcon));
                    });
                }
            } catch (Exception e) {
                Log.e("WeatherActivity", "Error parsing JSON", e);
            }
        }
    }
}
