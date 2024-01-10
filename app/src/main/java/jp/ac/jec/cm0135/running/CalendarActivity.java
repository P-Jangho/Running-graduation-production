package jp.ac.jec.cm0135.running;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class CalendarActivity extends AppCompatActivity {

    private ImageButton btnGauge;
    private ImageButton btnCal;
    private ImageButton btnGraph;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        btnGauge = findViewById(R.id.btnGauge);
        btnCal = findViewById(R.id.btnCal);
        btnGraph = findViewById(R.id.btnGraph);

        btnCal.setAlpha(0.1f);

        btnGauge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CalendarActivity.this, GaugeActivity.class);
                startActivity(intent);
            }
        });

        btnGraph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CalendarActivity.this, GraphActivity.class);
                startActivity(intent);
            }
        });
    }
}