package jp.ac.jec.cm0135.running;

import static jp.ac.jec.cm0135.running.BuildConfig.MY_KEY;
import static jp.ac.jec.cm0135.running.BuildConfig.MY_KEY2;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    public static final String KEY_LAST_EXECUTION_DATE = "last_execution_date";
    public LinearLayout linearlayout;
    private TextView dateTextView;
    public ProgressBar progressBar;
    public ProgressBar progressBarWeek;
    public TextView progressText;
    public TextView progressTextWeek;
    private Button btnStart;
    private ImageButton btnSet;
    private ImageButton btnClock;
    public ImageView sunImageView;
    public TextView recommendTxt;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private FusedLocationProviderClient fusedLocationClient;
    public TextView weatherInfoTextView;
    public TextView weatherInfoTextViewF;
    public final String API_KEY = MY_KEY;
    public final String API_KEY2 = MY_KEY2;
    public double latitude;
    public double longitude;
    public int stepsMain = 0;
    public int weekSteps = 0;
    public double weekCalories = 0.0;
    public double weekDistance = 0.0;
    public int todaySteps = 0;
    public int storedWeeklySteps = 0;
    public String currentDate2;
    public static final String KEY_WEEKLY_STEPS = "key_weekly_steps";
    public static final String KEY_DAY_STEPS = "key_day_steps";
    public static final String WEEK_STEPS = "week_steps";
    public static final String WEEK_DISTANCE = "week_distance";
    public static final String WEEK_CALORIES = "week_calories";
    public double currentTemperature;

    private Button btnRefresh;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 최초로 어플을 열었을 때 또는 매주 월요일에만 RefreshActivity를 띄웁니다.
        if (shouldShowRefreshScreenOnMonday()) {
            // AsyncTask를 사용하여 백그라운드에서 작업을 수행하고 완료 후에 MainActivity를 시작합니다.
            new RefreshActivityTask(this).execute();
        } else {
            // 나머지 부분은 기존과 동일
            setContentView(R.layout.activity_main);
            // Add your MainActivity initialization code here
        }

//        AlarmReceiver.setAlarm(this);

        // Load values from SharedPreferences
        SharedPreferences preferences = getSharedPreferences(SettingActivity.PREFERENCE_NAME, MODE_PRIVATE);
        String keyS = preferences.getString(SettingActivity.KEY_S, "");
        String keyC = preferences.getString(SettingActivity.KEY_C, "");
        String keyR = preferences.getString(SettingActivity.KEY_R, "");

        // Check if values are empty
        if (keyS.isEmpty() || keyC.isEmpty() || keyR.isEmpty()) {
            // Values are empty, start SettingActivity
            Intent intent = new Intent(MainActivity.this, SettingActivity.class);
            startActivity(intent);
            finish(); // Finish MainActivity to prevent going back to it when back button is pressed
        } else {
            // Values are not empty, start MainActivity
            setContentView(R.layout.activity_main);
            // Add your MainActivity initialization code here
        }

        linearlayout = findViewById(R.id.linearlayout);
        dateTextView = findViewById(R.id.dateTextView);
        progressBar = findViewById(R.id.progress_bar);
        progressBarWeek = findViewById(R.id.progress_bar1);
        progressText = findViewById(R.id.progress_text);
        progressTextWeek = findViewById(R.id.progress_text_week);
        recommendTxt = findViewById(R.id.recommendTxt);
        btnStart = findViewById(R.id.btnStart);
        btnSet = findViewById(R.id.btnSet);
        btnClock = findViewById(R.id.btnClock);
        sunImageView = findViewById(R.id.sunImageView);
        weatherInfoTextView = findViewById(R.id.weatherInfoTextView);
        weatherInfoTextViewF = findViewById(R.id.weatherInfoTextViewF);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        btnRefresh = findViewById(R.id.btnRefresh);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // 권한이 없는 경우 권한을 요청합니다.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            // 권한이 있는 경우 위치 업데이트를 시작합니다.
            requestLocationUpdates();
        }

        // 현재 날짜 가져오기
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy / MM / dd", Locale.getDefault());
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDate = dateFormat.format(calendar.getTime());
        currentDate2 = dateFormat2.format(calendar.getTime());

        dateTextView.setText(currentDate);

        GetTodayStepsTask getTodayStepsTask = new GetTodayStepsTask(this, new RunningActivity());
        getTodayStepsTask.execute();

        GetWeekStepsTask getWeekStepsTask = new GetWeekStepsTask(this);
        getWeekStepsTask.execute();

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, RunningActivity.class);
                intent.putExtra("currentTemperature", currentTemperature);
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

        btnClock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, TimeActivity.class);
                startActivity(intent);
            }
        });

        progressBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 프로그레스바를 클릭하면 다른 액티비티로 이동
                Intent intent = new Intent(MainActivity.this, GaugeActivity.class);
                // todaySteps 값을 넘겨줌
                intent.putExtra("TODAY_STEPS", todaySteps);
                startActivity(intent);
            }
        });

        progressBarWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 프로그레스바를 클릭하면 다른 액티비티로 이동
                Intent intent = new Intent(MainActivity.this, GaugeActivity.class);
                // todaySteps 값을 넘겨줌
                intent.putExtra("TODAY_STEPS", todaySteps);
                startActivity(intent);
            }
        });

        sunImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 현재 뷰의 바탕화면색 추출
                ColorDrawable mainActivityBackground = (ColorDrawable) linearlayout.getBackground();
                int backgroundColor = mainActivityBackground != null ? mainActivityBackground.getColor() : Color.WHITE;

                // Intent를 생성하고 색상 정보를 추가하여 WeatherActivity로 이동
                Intent intent = new Intent(MainActivity.this, WeatherActivity.class);
                intent.putExtra("lat", latitude);
                intent.putExtra("lon", longitude);
                intent.putExtra("backgroundColor", backgroundColor);
                startActivity(intent);
            }
        });

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, RefreshActivity.class);
                startActivity(intent);
            }
        });

//        btnRefresh.setVisibility(View.GONE);
    }

    private boolean shouldShowRefreshScreenOnMonday() {
        // SharedPreferences를 사용하여 최초로 어플을 열었는지 여부를 확인
        boolean isFirstTime = getSharedPreferences("MyPrefs", MODE_PRIVATE)
                .getBoolean("isFirstTime", true);

        Log.i("kkk", "kkk : " + isFirstTime);

        // 매주 월요일인지 확인
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        boolean isMonday = (dayOfWeek == Calendar.MONDAY);

        if (isFirstTime && isMonday) {
            // 최초로 어플을 열었고, 매주 월요일이면 isFirstTime 값을 true로 설정
            getSharedPreferences("MyPrefs", MODE_PRIVATE).edit().putBoolean("isFirstTime", false).apply();

            Log.i("kkk", "kkk : " + 1);
            return true;
        } else if (!isMonday) {
            // 다음 주 월요일을 위해 isFirstTime 값을 true로 초기화
            getSharedPreferences("MyPrefs", MODE_PRIVATE).edit().putBoolean("isFirstTime", true).apply();
            Log.i("kkk", "kkk : " + 2);
        }

        return false;
    }

    private static class RefreshActivityTask extends AsyncTask<Void, Void, Void> {
        private WeakReference<MainActivity> activityReference;

        RefreshActivityTask(MainActivity context) {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Intent intent = new Intent(activityReference.get(), RefreshActivity.class);
            activityReference.get().startActivity(intent);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            activityReference.get().finish();
        }
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
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();

//                        new WeatherTaskToday().execute();

                        WeatherTaskToday weatherTaskToday = new WeatherTaskToday(this);
                        weatherTaskToday.execute();

                        WeatherTaskWeek weatherTaskWeek = new WeatherTaskWeek(this);
                        weatherTaskWeek.execute();

                        scheduleJob();

                        RecommendTask recommendTask = new RecommendTask(this);
                        recommendTask.execute();
                    }
                });
    }

    private void scheduleJob() {
        WeatherJobScheduler.scheduleJob(this);
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
}

//    public static class SendDataTask extends AsyncTask<String, Void, Void> {
//        @Override
//        protected Void doInBackground(String... params) {
//            String date = params[0];
//            String stepsStr = params[1];
//            try {
//                String serverUrl = "http://22cm0135.main.jp/running/runningDB";
//                URL url = new URL(serverUrl);
//                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//                conn.setRequestMethod("POST");
//                conn.setDoOutput(true);
//
//                // POST 데이터 생성
//                HashMap<String, String> postDataParams = new HashMap<>();
//                postDataParams.put("date", date);
//                postDataParams.put("steps", stepsStr);
//                postDataParams.put("calories", String.valueOf(0));
//                postDataParams.put("distance", String.valueOf(0));
//
//                // 데이터 전송
//                OutputStream os = conn.getOutputStream();
//                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
//                writer.write(getPostDataString(postDataParams));
//                writer.flush();
//                writer.close();
//                os.close();
//
//                // 서버 응답 확인
//                int responseCode = conn.getResponseCode();
//                if (responseCode == HttpURLConnection.HTTP_OK) {
//                    // 전송 성공
//                } else {
//                    // 전송 실패
//                }
//
//                conn.disconnect();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            return null;
//        }
//
//        private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
//            StringBuilder result = new StringBuilder();
//            boolean first = true;
//
//            for (Map.Entry<String, String> entry : params.entrySet()) {
//                if (first) {
//                    first = false;
//                } else {
//                    result.append("&");
//                }
//
//                result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
//                result.append("=");
//                result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
//            }
//
//            return result.toString();
//        }
//    }

