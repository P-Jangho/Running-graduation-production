package jp.ac.jec.cm0135.running;

import static android.content.Context.MODE_PRIVATE;
import static jp.ac.jec.cm0135.running.MainActivity.WEEK_CALORIES;
import static jp.ac.jec.cm0135.running.MainActivity.WEEK_DISTANCE;
import static jp.ac.jec.cm0135.running.MainActivity.WEEK_STEPS;
import static jp.ac.jec.cm0135.running.SettingActivity.PREFERENCE_NAME;

import android.content.SharedPreferences;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class GetWeekStepsTask extends AsyncTask<Void, Void, JSONObject> {

    private final MainActivity mainActivity;

    public GetWeekStepsTask(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }
    @Override
    protected JSONObject doInBackground(Void... params) {
        try {
            String serverUrl = "http://22cm0135.main.jp/running/getDailyActivity.php";
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
                JSONArray jsonArray = new JSONArray(response.toString());

                // 오늘의 날짜를 "yyyy-MM-dd" 형식으로 추출
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Calendar calendar = Calendar.getInstance();
                String todayDate = sdf.format(calendar.getTime());

                // 주간 걸음 수를 저장할 변수 초기화
                int totalWeekSteps = 0;
                double totalWeekCalories = 0.0;
                double totalWeekDistance = 0.0;

                // 현재 요일을 기준으로 해당 주의 시작 날짜 계산
                int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
                int daysToSubtract = (dayOfWeek - Calendar.MONDAY + 7) % 7;
                calendar.add(Calendar.DAY_OF_MONTH, -daysToSubtract);
                String startOfWeek = sdf.format(calendar.getTime());

                // JSON 배열을 반복하여 해당 주의 걸음 수 계산
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String entryDate = jsonObject.getString("date");

                    // 해당 주의 시작 날짜 이후의 날짜라면 걸음 수 더하기
                    if (sdf.parse(entryDate).compareTo(sdf.parse(startOfWeek)) >= 0
                            && sdf.parse(entryDate).compareTo(sdf.parse(todayDate)) <= 0) {
                        int steps = jsonObject.getInt("steps");
                        double calories = jsonObject.getDouble("calories");
                        double distance = jsonObject.getDouble("distance");
                        totalWeekSteps += steps;
                        totalWeekCalories += calories;
                        totalWeekDistance += distance;
                    }
                }

                // JSON 객체에 계산된 값을 담아서 리턴
                JSONObject resultObject = new JSONObject();
                resultObject.put("totalWeekSteps", totalWeekSteps);
                resultObject.put("totalWeekCalories", totalWeekCalories);
                resultObject.put("totalWeekDistance", totalWeekDistance);

                return resultObject;
            }
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(JSONObject result) {
        super.onPostExecute(result);
        if (result != null) {
            try {
                SharedPreferences preferences = mainActivity.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
                // 결과에서 값을 가져와서 사용
                mainActivity.weekSteps = result.getInt("totalWeekSteps");
                mainActivity.weekCalories = result.getDouble("totalWeekCalories");
                mainActivity.weekDistance = result.getDouble("totalWeekDistance");

                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt(WEEK_STEPS, mainActivity.weekSteps);
                editor.putLong(WEEK_DISTANCE, (long) mainActivity.weekDistance);
                editor.putLong(WEEK_CALORIES, (long) mainActivity.weekCalories);
                editor.apply();
//                Log.i("aaa", "aaa " + weekSteps + " " + weekCalories + " " + weekDistance);

                // 나머지 UI 업데이트 등의 작업 수행
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
