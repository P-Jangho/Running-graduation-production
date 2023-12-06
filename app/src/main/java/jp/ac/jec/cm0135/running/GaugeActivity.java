package jp.ac.jec.cm0135.running;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import androidx.appcompat.app.AppCompatActivity;

public class GaugeActivity extends AppCompatActivity {

    private ProgressBar gaugeProgressBar;
    private Button increaseButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gauge);

        gaugeProgressBar = findViewById(R.id.gaugeProgressBar);
        increaseButton = findViewById(R.id.increaseButton);

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
