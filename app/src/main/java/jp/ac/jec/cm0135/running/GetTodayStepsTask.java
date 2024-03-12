package jp.ac.jec.cm0135.running;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetTodayStepsTask extends AsyncTask<Void, Void, Integer> {

    private final MainActivity mainActivity;
    private final RunningActivity runningActivity;

    public GetTodayStepsTask(MainActivity mainActivity, RunningActivity runningActivity) {
        this.mainActivity = mainActivity;
        this.runningActivity = runningActivity;
    }

    @Override
    protected Integer doInBackground(Void... params) {
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
                JSONObject jsonObject = new JSONObject(response.toString());
                int serverSteps = jsonObject.getInt("steps");
                Log.d("aaa", "Server steps: " + serverSteps);
                return serverSteps;
            }
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    protected void onPostExecute(Integer result) {
        super.onPostExecute(result);

        mainActivity.stepsMain = result;
        if (mainActivity.stepsMain == 0) {
            new SendDataTask().execute(mainActivity.currentDate2, String.valueOf(mainActivity.stepsMain), "0", "0");
        }

        // Null 체크를 추가하여 NullPointerException 방지
        if (runningActivity != null && runningActivity.tv_sensor != null) {
            // 서버에서 받아온 걸음 수를 초기값으로 설정

            runningActivity.steps = result;

            runningActivity.tv_sensor.setText(String.valueOf(runningActivity.steps));
        } else {
            Log.e("GetTodayStepsTask", "runningActivity or tv_sensor is null");
        }
    }
}
