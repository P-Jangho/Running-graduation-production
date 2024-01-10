package jp.ac.jec.cm0135.running;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class GraphActivity extends AppCompatActivity {

    private ImageButton btnGauge;
    private ImageButton btnCal;
    private ImageButton btnGraph;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        btnGauge = findViewById(R.id.btnGauge);
        btnCal = findViewById(R.id.btnCal);
        btnGraph = findViewById(R.id.btnGraph);

        btnGraph.setAlpha(0.1f);

        btnGauge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GraphActivity.this, GaugeActivity.class);
                startActivity(intent);
            }
        });

        btnCal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GraphActivity.this, CalendarActivity.class);
                startActivity(intent);
            }
        });
    }
}