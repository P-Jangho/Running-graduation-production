package jp.ac.jec.cm0135.running;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SendDataTask extends AsyncTask<String, Void, Void> {

    private static final double CALORIES_PER_STEP = 0.04;  // 걸음당 일정한 칼로리 (예: 0.04 칼로리)
    private static final double DISTANCE_PER_STEP = 0.762;  // 걸음당 일정한 거리 (예: 0.762 미터)

    @Override
    protected Void doInBackground(String... params) {
        String date = params[0];
        String stepsStr = params[1];
        int steps = Integer.parseInt(stepsStr);  // 걸음 수를 정수로 변환
        double calories = steps * CALORIES_PER_STEP;
        double distance = steps * DISTANCE_PER_STEP;

        try {
            String serverUrl = "http://22cm0135.main.jp/running/runningDB";
            URL url = new URL(serverUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            // POST 데이터 생성
            HashMap<String, String> postDataParams = new HashMap<>();
            postDataParams.put("date", date);
            postDataParams.put("steps", stepsStr);
            postDataParams.put("calories", String.format(Locale.US, "%.1f", calories));
            postDataParams.put("distance", String.format(Locale.US, "%.1f", distance));

            // 데이터 전송
            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(getPostDataString(postDataParams));
            writer.flush();
            writer.close();
            os.close();

            // 서버 응답 확인
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // 전송 성공
                Log.d("aaa", "true: " + date);
                Log.d("aaa", "true: " + stepsStr);
                Log.d("aaa", "true: " + String.format(Locale.US, "%.1f", calories));
                Log.d("aaa", "true: " + String.format(Locale.US, "%.1f", distance));
            } else {
                // 전송 실패
                Log.d("aaa", "false: " + steps);
                Log.d("aaa", "false: " + String.format(Locale.US, "%.1f", calories));
                Log.d("aaa", "false: " + String.format(Locale.US, "%.1f", distance));
            }

            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first) {
                first = false;
            } else {
                result.append("&");
            }

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }
}