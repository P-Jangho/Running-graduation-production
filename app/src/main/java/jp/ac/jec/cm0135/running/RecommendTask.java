package jp.ac.jec.cm0135.running;

import static jp.ac.jec.cm0135.running.TimeActivity.FIRST_SPINNER_HOUR_KEY;
import static jp.ac.jec.cm0135.running.TimeActivity.PREFS_NAME;
import static jp.ac.jec.cm0135.running.TimeActivity.SECOND_SPINNER_HOUR_KEY;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

public class RecommendTask extends AsyncTask<Void, Void, Void> {

    private final MainActivity mainActivity;

    public RecommendTask(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            // OpenWeatherMap API에 요청하는 코드
            String apiUrl = "https://api.openweathermap.org/data/2.5/forecast" +
                    "?lat=" + mainActivity.latitude +
                    "&lon=" + mainActivity.longitude +
                    "&appid=" + mainActivity.API_KEY2;

            Log.i("aaa", "apiURL: " + apiUrl);
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
            Log.e("MainActivity", "Error fetching weather data", e);
        }
        return null;
    }

    @SuppressLint("SetTextI18n")
    private void parseJson(String json) {
        try {
            // JSON 응답을 JsonObject로 파싱
            JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();

            // "list" 배열 가져오기
            JsonArray listArray = jsonObject.getAsJsonArray("list");
            String recommendTime = "";

            if (listArray != null && listArray.size() > 0) {
                for (int i = 0; i < listArray.size(); i++) {
                    JsonObject itemObject = listArray.get(i).getAsJsonObject();

                    // "main" 오브젝트 가져오기
                    JsonObject mainObject = itemObject.getAsJsonObject("main");
                    double temperature = mainObject.getAsJsonPrimitive("temp").getAsDouble();
                    int humidity = mainObject.getAsJsonPrimitive("humidity").getAsInt();
                    double tempCurrent = temperature - 273.15;

                    // "weather" 배열 가져오기
                    JsonArray weatherArray = itemObject.getAsJsonArray("weather");
                    JsonObject weatherObject = weatherArray.get(0).getAsJsonObject();
                    String weather = weatherObject.getAsJsonPrimitive("main").getAsString();

                    // "wind" 오브젝트 가져오기
                    JsonObject windObject = itemObject.getAsJsonObject("wind");
                    double wind = windObject.getAsJsonPrimitive("speed").getAsDouble();

                    // "dt_txt" 값 가져오기
                    String dtTxt = itemObject.getAsJsonPrimitive("dt_txt").getAsString();

                    // 현재 날짜와 시간에 해당하는 정보만 처리
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        LocalDateTime dateTime = LocalDateTime.parse(dtTxt, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                        String timeOnly = dateTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"));

                        // 현재 시간 이후의 추천 시간만 고려
                        if (dtTxt.contains(mainActivity.currentDate2) && dateTime.isAfter(LocalDateTime.now())) {
                            if (tempCurrent <= 25 && tempCurrent >= 0 &&
                                    (weather.equals("Clear") || weather.equals("Clouds")) &&
                                    humidity >= 30 && humidity <= 100 &&
                                    wind >= 0 && wind <= 20) {

                                SharedPreferences preferences = mainActivity.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                                String firstSpinnerHour = preferences.getString(FIRST_SPINNER_HOUR_KEY, "");
                                String secondSpinnerHour = preferences.getString(SECOND_SPINNER_HOUR_KEY, "");
                                Log.i("ddd", "ddd : " + firstSpinnerHour + " " + secondSpinnerHour);

                                int recommendHour = Integer.parseInt(timeOnly.split(":")[0]);
                                int firstSpinner = Integer.parseInt(firstSpinnerHour);
                                int secondSpinner = Integer.parseInt(secondSpinnerHour);

                                if (secondSpinner <= firstSpinner) {
                                    secondSpinner += 24;
                                    if (firstSpinner == 0 && secondSpinner == 24) {
                                        secondSpinner = 0;
                                    }
                                }

                                Log.i("aaa", "aaa " + recommendHour + " " + firstSpinner + " " + secondSpinner);

                                if (recommendHour >= firstSpinner && recommendHour <= secondSpinner) {
                                    recommendTime = "0";
                                    Log.i("aaa", "aaa " + tempCurrent + " " + weather + " " + humidity + " " + wind);
                                    Log.i("aaa", "aaa " + 1);
                                    continue;
                                } else {
                                    recommendTime = timeOnly;
                                }
                                Log.i("aaa", "aaa " + tempCurrent + " " + weather + " " + humidity + " " + wind);
                                Log.i("aaa", "aaa " + 2);
                                break;
                            } else {
                                recommendTime = "0";
                            }
                        }else {
                            recommendTime = "0";
                        }
                    }
                }
            }

            // UI 업데이트
            String finalRecommendTime = recommendTime;
            mainActivity.runOnUiThread(() -> {
                if ("0".equals(finalRecommendTime)) {
                    mainActivity.recommendTxt.setText("No recommended time");
                } else {
                    // Assuming finalRecommendTime is a string in the format "HH:mm"
                    SimpleDateFormat inputFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

                    try {
                        Date recommendTimeDate = inputFormat.parse(finalRecommendTime);

                        // Now, format the Date object as "HH時"
                        SimpleDateFormat outputFormat = new SimpleDateFormat("HH時", Locale.getDefault());
                        String formattedRecommendTime = outputFormat.format(recommendTimeDate);

                        // Update the TextView
                        mainActivity.recommendTxt.setText("Recommend : " + formattedRecommendTime);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            Log.e("MainActivity", "Error parsing JSON", e);
        }
    }
}
