package jp.ac.jec.cm0135.running;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView dateTextView;
    private ProgressBar progressBar;
    private TextView progressText;
    int i = 0;

    private Button btnP;
    private Button btnStart;
    private ImageButton btnSet;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dateTextView = findViewById(R.id.dateTextView);

        // set the id for the progressbar and progress text
        progressBar = findViewById(R.id.progress_bar);
        progressText = findViewById(R.id.progress_text);

//        btnP = findViewById(R.id.btnP);
        btnStart = findViewById(R.id.btnStart);
        btnSet = findViewById(R.id.btnSet);

//        final Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                // set the limitations for the numeric
//                // text under the progress bar
//                if (i <= 100) {
//                    progressText.setText("" + i);
//                    progressBar.setProgress(i);
//                    i++;
//                    handler.postDelayed(this, 200);
//                } else {
//                    handler.removeCallbacks(this);
//                }
//            }
//        }, 200);

//        btnP.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (i <= 100) {
//                    progressText.setText("" + i);
//                    progressBar.setProgress(i);
//                    i++;
//                } else {
//                    i = 0;
//                    progressText.setText("" + i);
//                    progressBar.setProgress(i);
//                }
//            }
//        });

        // 현재 날짜 가져오기
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy / MM / dd", Locale.getDefault());
        String currentDate = dateFormat.format(calendar.getTime());

        // TextView에 날짜 표시
        dateTextView.setText(currentDate);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, RunningActivity.class);
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

        progressBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 프로그레스바를 클릭하면 다른 액티비티로 이동
                Intent intent = new Intent(MainActivity.this, GaugeActivity.class);
                startActivity(intent);
            }
        });
    }
}
