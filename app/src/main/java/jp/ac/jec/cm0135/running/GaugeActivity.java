package jp.ac.jec.cm0135.running;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

public class GaugeActivity extends AppCompatActivity {

    private ProgressBar gaugeProgressBar;
    private Button increaseButton;
    private ImageButton btnGauge;
    private ImageButton btnCal;
    private ImageButton btnGraph;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gauge);

        gaugeProgressBar = findViewById(R.id.gaugeProgressBar);
        increaseButton = findViewById(R.id.increaseButton);
        btnGauge = findViewById(R.id.btnGauge);
        btnCal = findViewById(R.id.btnCal);
        btnGraph = findViewById(R.id.btnGraph);

        btnGauge.setAlpha(0.1f);

        btnCal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GaugeActivity.this, CalendarActivity.class);
                startActivity(intent);
            }
        });

        btnGraph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GaugeActivity.this, GraphActivity.class);
                startActivity(intent);
            }
        });

        // 버튼 클릭 시 게이지 증가
        increaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                increaseGauge();
            }
        });
    }

    // 게이지를 5%씩 증가시키는 메서드
    private void increaseGauge() {
        int currentProgress = gaugeProgressBar.getProgress();
        int newProgress = currentProgress + 5;

        if (newProgress <= gaugeProgressBar.getMax()) {
            gaugeProgressBar.setProgress(newProgress);
        }
    }
}
