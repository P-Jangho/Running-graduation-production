package jp.ac.jec.cm0135.running;

// RefreshActivity.java

import static jp.ac.jec.cm0135.running.MainActivity.KEY_WEEKLY_STEPS;
import static jp.ac.jec.cm0135.running.MainActivity.WEEK_CALORIES;
import static jp.ac.jec.cm0135.running.MainActivity.WEEK_DISTANCE;
import static jp.ac.jec.cm0135.running.MainActivity.WEEK_STEPS;
import static jp.ac.jec.cm0135.running.SettingActivity.PREFERENCE_NAME;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class RefreshActivity extends AppCompatActivity {

    private Button refreshButton;
    private Button resettingButton;
    private ProgressBar gaugeProgressBarRefresh;
    private TextView txtStepsWeek;
    private TextView txtCaloriesWeek;
    private TextView txtDistanceWeek;
    private TextView txtRefresh;
    private ImageView imgRefresh;
    private int weekSteps;
    private Double weekDistance;
    private Double weekCalories;
    private int receivedWeekSteps;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refresh);

        refreshButton = findViewById(R.id.refreshButton);
        resettingButton = findViewById(R.id.resettingButton);
        gaugeProgressBarRefresh = findViewById(R.id.gaugeProgressBarRefresh);
        txtStepsWeek = findViewById(R.id.txtStepsWeek);
        txtDistanceWeek = findViewById(R.id.txtDistanceWeek);
        txtCaloriesWeek = findViewById(R.id.txtCaloriesWeek);
        txtRefresh = findViewById(R.id.txtRefresh);
        imgRefresh = findViewById(R.id.imgRefresh);

        SharedPreferences preferences = getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        receivedWeekSteps = preferences.getInt(KEY_WEEKLY_STEPS, 0);
        weekSteps = preferences.getInt(WEEK_STEPS, 0);
        weekDistance = (double) preferences.getLong(WEEK_DISTANCE, 0);
        weekCalories = (double) preferences.getLong(WEEK_CALORIES, 0);

        txtStepsWeek.setText(String.format("%d / %d", weekSteps, receivedWeekSteps));
        txtDistanceWeek.setText(String.format("%s km", weekDistance / 1000.0));
        txtCaloriesWeek.setText(String.format("%s kcal", weekCalories));

        // 비율 계산
        double progressRatio = (double) weekSteps / receivedWeekSteps;

        // 게이지 업데이트
        int progress = (int) (progressRatio * 100);
        gaugeProgressBarRefresh.setProgress(progress);

        if(progress < 100) {
            imgRefresh.setImageResource(R.drawable.baseline_mood_bad_24);
            txtRefresh.setText("CHEER UP!!!");
        }

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: 데이터 갱신 및 화면 갱신하는 로직 추가
                // 이후에는 MainActivity로 이동하거나 다른 화면으로 전환하는 등의 동작을 수행할 수 있습니다.
                Intent intent = new Intent(RefreshActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        resettingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RefreshActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });
    }
}
