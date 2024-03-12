package jp.ac.jec.cm0135.running;

import static jp.ac.jec.cm0135.running.SettingActivity.KEY_C;
import static jp.ac.jec.cm0135.running.SettingActivity.KEY_R;
import static jp.ac.jec.cm0135.running.SettingActivity.KEY_S;
import static jp.ac.jec.cm0135.running.SettingActivity.PREFERENCE_NAME;

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
import java.util.Calendar;

public class WeatherTaskWeek extends AsyncTask<Void, Void, Void> {

    private final Context context;

    public WeatherTaskWeek(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            if (context instanceof MainActivity) {
                Log.i("ggg", "ggg: " + 1);
                MainActivity mainActivity = (MainActivity) context;

                // 위치 정보를 가져와서 API에 요청하는 코드
                String apiUrl = "https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/"
                        + mainActivity.latitude + "," + mainActivity.longitude
                        + "?unitGroup=metric&include=days%2Ccurrent&key=" + mainActivity.API_KEY + "&contentType=json";

                Log.i("WeatherTaskWeek", "url " + apiUrl);

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
            } else {
                Log.e("WeatherTaskWeek", "Context is not an instance of MainActivity");
            }
        } catch (Exception e) {
            Log.e("WeatherTaskWeek", "Error fetching weather data", e);
        }
        return null;
    }

    @SuppressLint("SetTextI18n")
    private void parseJson(String json) {
        try {
            // JSON을 JsonObject로 파싱
            JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();

            // "days" 배열 가져오기
            JsonArray daysArray = jsonObject.getAsJsonArray("days");

            if (daysArray != null && daysArray.size() > 0) {

                JsonObject firstDayObject = daysArray.get(0).getAsJsonObject();

                String icon = firstDayObject.getAsJsonPrimitive("icon").getAsString();

                SharedPreferences preferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);

                if (icon.equals("partly-cloudy-day")) {
                    ((MainActivity) context).todaySteps = Integer.parseInt(preferences.getString(KEY_C, ""));
                } else if (icon.equals("cloudy")) {
                    ((MainActivity) context).todaySteps = Integer.parseInt(preferences.getString(KEY_C, ""));
                } else if (icon.equals("rain")) {
                    ((MainActivity) context).todaySteps = Integer.parseInt(preferences.getString(KEY_R, ""));
                } else {
                    ((MainActivity) context).todaySteps = Integer.parseInt(preferences.getString(KEY_S, ""));
                }

                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt(MainActivity.KEY_DAY_STEPS, ((MainActivity) context).todaySteps);
                editor.apply();

                int weeklySteps = 0;
                Calendar calendar = Calendar.getInstance();
                int currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

                // 마지막으로 실행된 날짜를 가져옴
                long lastExecutionDate = preferences.getLong(MainActivity.KEY_LAST_EXECUTION_DATE, 0);

                // 현재 날짜를 가져옴
                long currentDate = System.currentTimeMillis();
                Log.i("WeatherTaskWeek", "lastExecutionDate " + lastExecutionDate);
                Log.i("WeatherTaskWeek", "getMondayOfCurrentWeek() " + getMondayOfCurrentWeek());
                // 만약 마지막 실행 날짜가 현재 주의 월요일보다 이전이라면 코드 실행
                if (lastExecutionDate < getMondayOfCurrentWeek()) {
                    if (currentDayOfWeek == Calendar.SUNDAY) {
                        JsonObject dayObject = daysArray.get(0).getAsJsonObject();
                        String iconWeek = dayObject.getAsJsonPrimitive("icon").getAsString();

                        if (iconWeek.equals("partly-cloudy-day")) {
                            weeklySteps += Integer.parseInt(preferences.getString(KEY_C, ""));
                        } else if (iconWeek.equals("cloudy")) {
                            weeklySteps += Integer.parseInt(preferences.getString(KEY_C, ""));
                        } else if (iconWeek.equals("rain")) {
                            weeklySteps += Integer.parseInt(preferences.getString(KEY_R, ""));
                        } else {
                            weeklySteps += Integer.parseInt(preferences.getString(KEY_S, ""));
                        }

                        // 계산된 주간 걸음수를 SharedPreferences에 저장 (여기서는 임의로 KEY_WEEKLY_STEPS로 저장)
                        SharedPreferences.Editor editor1 = preferences.edit();
                        editor1.putInt(MainActivity.KEY_WEEKLY_STEPS, weeklySteps);
                        editor1.apply();
                    } else {
                        if (currentDayOfWeek >= Calendar.MONDAY) {
                            // 일주일간의 날씨 정보를 바탕으로 걸음수를 계산
                            for (int i = 0; i < 7 - (currentDayOfWeek - 2); i++) {
                                JsonObject dayObject = daysArray.get(i).getAsJsonObject();
                                String iconWeek = dayObject.getAsJsonPrimitive("icon").getAsString();
                                Log.i("WeatherTaskWeek", "icon " + iconWeek);
                                if (iconWeek.equals("partly-cloudy-day")) {
                                    weeklySteps += Integer.parseInt(preferences.getString(KEY_C, ""));
                                } else if (iconWeek.equals("cloudy")) {
                                    weeklySteps += Integer.parseInt(preferences.getString(KEY_C, ""));
                                } else if (iconWeek.equals("rain")) {
                                    weeklySteps += Integer.parseInt(preferences.getString(KEY_R, ""));
                                } else {
                                    weeklySteps += Integer.parseInt(preferences.getString(KEY_S, ""));
                                }
                            }

                            // 계산된 주간 걸음수를 SharedPreferences에 저장 (여기서는 임의로 KEY_WEEKLY_STEPS로 저장)
                            SharedPreferences.Editor editor1 = preferences.edit();
                            editor1.putInt(MainActivity.KEY_WEEKLY_STEPS, weeklySteps);
                            editor1.apply();
                        }
                    }

                    // 현재 날짜를 저장하여 다음 주 월요일까지 실행이 되지 않도록 함
                    editor.putLong(MainActivity.KEY_LAST_EXECUTION_DATE, currentDate);
                    editor.apply();
                }

                Log.i("WeatherTaskWeek", "icon: " + icon);

                ((MainActivity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((MainActivity) context).progressText.setText(((MainActivity) context).stepsMain + " / " + ((MainActivity) context).todaySteps);
                        int percent = (int) ((double) ((MainActivity) context).stepsMain / ((MainActivity) context).todaySteps * 100);
                        ((MainActivity) context).progressBar.setProgress(percent);

                        ((MainActivity) context).storedWeeklySteps = preferences.getInt(MainActivity.KEY_WEEKLY_STEPS, 0);
                        ((MainActivity) context).progressTextWeek.setText(((MainActivity) context).weekSteps + " / " + ((MainActivity) context).storedWeeklySteps);
                        int weeklyPercent = (int) ((double) ((MainActivity) context).weekSteps / ((MainActivity) context).storedWeeklySteps * 100);
                        ((MainActivity) context).progressBarWeek.setProgress(weeklyPercent);
                    }
                });
            } else {
                Log.e("WeatherTaskWeek", "No data available in 'days' array");
            }
        } catch (Exception e) {
            Log.e("WeatherTaskWeek", "Error parsing JSON", e);
        }
    }

    // 현재 주의 월요일을 가져오는 메서드
    private long getMondayOfCurrentWeek() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        return calendar.getTimeInMillis();
    }
}
