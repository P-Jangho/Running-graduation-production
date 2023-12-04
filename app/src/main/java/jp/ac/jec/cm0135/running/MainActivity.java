package jp.ac.jec.cm0135.running;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private TextView progressText;
    int i = 0;

    private Button btnP;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set the id for the progressbar and progress text
        progressBar = findViewById(R.id.progress_bar);
        progressText = findViewById(R.id.progress_text);

        btnP = findViewById(R.id.btnP);

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

        btnP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (i <= 100) {
                    progressText.setText("" + i);
                    progressBar.setProgress(i);
                    i++;
                } else {
                    i = 0;
                    progressText.setText("" + i);
                    progressBar.setProgress(i);
                }
            }
        });
    }
}
