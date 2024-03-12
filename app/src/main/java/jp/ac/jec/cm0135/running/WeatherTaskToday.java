package jp.ac.jec.cm0135.running;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;

public class WeatherTaskToday extends AsyncTask<Void, Void, Void> {

    private final MainActivity mainActivity;

    public WeatherTaskToday(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            // Construct the URL for the OpenWeatherMap API
            String apiUrl = "https://api.openweathermap.org/data/2.5/weather" +
                    "?lat=" + mainActivity.latitude +
                    "&lon=" + mainActivity.longitude +
                    "&appid=" + mainActivity.API_KEY2;

            Log.i("aaa", "apiURL1: " + apiUrl);
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
            Log.e("MainActivity", "Error fetching weather data", e);
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

            // Extract weather icon
            JsonArray weatherArray = jsonObject.getAsJsonArray("weather");
            if (weatherArray != null && weatherArray.size() > 0) {
                JsonObject weatherObject = weatherArray.get(0).getAsJsonObject();
                String weatherIcon = weatherObject.getAsJsonPrimitive("icon").getAsString();

                Log.i("aaa", "weatherICON" + weatherIcon);

                // Extract sunset time
                JsonObject sysObject = jsonObject.getAsJsonObject("sys");
                long sunsetTimestamp = sysObject.getAsJsonPrimitive("sunset").getAsLong() * 1000;
                long sunriseTimestamp = sysObject.getAsJsonPrimitive("sunrise").getAsLong() * 1000;

                Log.i("lll", "sunset : " + sunsetTimestamp);
                Log.i("lll", "sunrise : " + sunriseTimestamp);
                Log.i("lll", "System.currentTimeMillis() : " + System.currentTimeMillis());

                // Check if the sun has set
                boolean isSunset = System.currentTimeMillis() > sunsetTimestamp;

                // Update UI on the main thread
                mainActivity.runOnUiThread(new Runnable() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void run() {
                        // 현재 온도
                        mainActivity.currentTemperature = currentTemp - 273.15;
                        String formattedCurrentTemp = new DecimalFormat("0.0").format(mainActivity.currentTemperature);
                        mainActivity.weatherInfoTextView.setText(formattedCurrentTemp + "°C");

                        Log.i("aaa", "apiURL1: " + mainActivity.currentTemperature);

                        // 체감 온도
                        double feelsLikeTemperature = feelsLikeTemp - 273.15;
                        String formattedFeelsLikeTemp = new DecimalFormat("0.0").format(feelsLikeTemperature);
                        mainActivity.weatherInfoTextViewF.setText("( 体感気温 : " + formattedFeelsLikeTemp + "°C )");

                        if (weatherIcon.contains("02")) {
                            mainActivity.linearlayout.setBackgroundColor(Color.parseColor("#69A9A9A9"));
                            mainActivity.sunImageView.setImageResource(R.drawable.cloud);
                        } else if (weatherIcon.contains("03") || weatherIcon.contains("04")) {
                            mainActivity.linearlayout.setBackgroundColor(Color.parseColor("#69505050"));
                            mainActivity.sunImageView.setImageResource(R.drawable.cloudy);
                        } else if (weatherIcon.contains("09") || weatherIcon.contains("10")) {
                            mainActivity.linearlayout.setBackgroundColor(Color.parseColor("#69226A6F"));
                            mainActivity.sunImageView.setImageResource(R.drawable.rain);
                        } else if (weatherIcon.contains("11")) {
                            mainActivity.linearlayout.setBackgroundColor(Color.parseColor("#69505050"));
                            mainActivity.sunImageView.setImageResource(R.drawable.cloudy);
                        } else if (weatherIcon.contains("13")) {
                            mainActivity.linearlayout.setBackgroundColor(Color.parseColor("#69505050"));
                            mainActivity.sunImageView.setImageResource(R.drawable.snow);
                        } else if (weatherIcon.contains("50")) {
                            mainActivity.linearlayout.setBackgroundColor(Color.parseColor("#69505050"));
                            mainActivity.sunImageView.setImageResource(R.drawable.cloudy);
                        } else {
                            if (sunriseTimestamp < System.currentTimeMillis() && System.currentTimeMillis() < sunsetTimestamp) {
                                mainActivity.linearlayout.setBackgroundColor(Color.parseColor("#6906D1FE"));
                                mainActivity.sunImageView.setImageResource(R.drawable.sun);
                            }else {
                                mainActivity.linearlayout.setBackgroundColor(Color.parseColor("#69200DE7"));
                                mainActivity.sunImageView.setImageResource(R.drawable.moon);
                            }
                        }

                        // Use weatherIcon as needed, for example, set it to an ImageView
                        // imageView.setImageResource(getIconResourceId(weatherIcon));
                    }
                });
            }
        } catch (Exception e) {
            Log.e("MainActivity", "Error parsing JSON", e);
        }
    }
}
