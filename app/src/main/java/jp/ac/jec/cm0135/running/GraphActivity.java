package jp.ac.jec.cm0135.running;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class GraphActivity extends AppCompatActivity {

    private LineChart chart;
    private ArrayList<Entry> stepsEntries;
    private ArrayList<Entry> caloriesEntries;
    private ArrayList<Entry> distanceEntries;
    private ImageButton btnGauge;
    private ImageButton btnCal;
    private ImageButton btnGraph;
    private ImageButton btnHome;
    private ImageButton btnBack;
    private Button btnPrevMonth;
    private Button btnNextMonth;
    private TextView monthTextView;
    private TextView noDataTextView;
    private int currentYear;
    private int currentMonth;

    @SuppressLint({"MissingInflatedId", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        btnGauge = findViewById(R.id.btnGauge);
        btnCal = findViewById(R.id.btnCal);
        btnGraph = findViewById(R.id.btnGraph);
        btnHome = findViewById(R.id.btnHome);
        btnBack = findViewById(R.id.btnBack);
        btnPrevMonth = findViewById(R.id.btnPrevMonth);
        btnNextMonth = findViewById(R.id.btnNextMonth);
        monthTextView = findViewById(R.id.monthTextView);
        noDataTextView = findViewById(R.id.noDataTextView);

        chart = findViewById(R.id.chart);
        stepsEntries = new ArrayList<>();
        caloriesEntries = new ArrayList<>();
        distanceEntries = new ArrayList<>();

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

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GraphActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GraphActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        btnPrevMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 현재 연도와 월을 업데이트
                currentMonth -= 1;
                if (currentMonth < 1) {
                    currentMonth = 12;
                    currentYear -= 1;
                }
                loadMonthlyData(currentYear, currentMonth);
            }
        });

        btnNextMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 현재 연도와 월을 업데이트
                currentMonth += 1;
                if (currentMonth > 12) {
                    currentMonth = 1;
                    currentYear += 1;
                }
                loadMonthlyData(currentYear, currentMonth);
            }
        });

        // 현재 연도와 월을 가져와서 데이터 요청
        Calendar calendar = Calendar.getInstance();
        currentYear = calendar.get(Calendar.YEAR);
        currentMonth = calendar.get(Calendar.MONTH) + 1; // Calendar.MONTH는 0부터 시작하므로 +1
//        monthTextView.setText(currentYear + " / " + currentMonth);
        new FetchMonthlyDataFromServerTask().execute(currentYear, currentMonth);
    }

    private void loadMonthlyData(int year, int month) {
        // 현재 연도와 월을 업데이트
        currentYear = year;
        currentMonth = month;

        // 데이터 요청
        new FetchMonthlyDataFromServerTask().execute(year, month);
    }

    private class FetchMonthlyDataFromServerTask extends AsyncTask<Integer, Void, JSONArray> {
        private String firstDayOfMonth; // 변수를 메서드 외부로 이동

        @Override
        protected JSONArray doInBackground(Integer... params) {
            try {
                int year = params[0];
                int month = params[1];

                // 해당 월의 데이터를 가져오기 위해 시작일과 종료일을 계산
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month - 1, 1); // Calendar.MONTH는 0부터 시작하므로 -1
                firstDayOfMonth = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.getTime());
                calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
                String lastDayOfMonth = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.getTime());

                String serverUrl = "http://22cm0135.main.jp/running/getDailyActivity.php?startDate=" + firstDayOfMonth + "&endDate=" + lastDayOfMonth;
                URL url = new URL(serverUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                Log.i("zzz", "url: " + serverUrl);
                Log.i("zzz", "url: " + firstDayOfMonth.split("-")[0] + " / " + firstDayOfMonth.split("-")[1]);

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                    return new JSONArray(response.toString());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONArray result) {
            if (result != null && result.length() > 0) {
                // 데이터가 있을 경우, 리스트 초기화 후 데이터 추가
                stepsEntries.clear();
                caloriesEntries.clear();
                distanceEntries.clear();

                try {
                    for (int i = 0; i < result.length(); i++) {
                        JSONObject data = result.getJSONObject(i);
                        int day = Integer.parseInt(data.getString("date").split("-")[2]);
                        int steps = Integer.parseInt(data.getString("steps"));
                        float calories = Float.parseFloat(data.getString("calories"));
                        float distance = Float.parseFloat(data.getString("distance"));

                        stepsEntries.add(new Entry(day, steps));
                        caloriesEntries.add(new Entry(day, calories));
                        distanceEntries.add(new Entry(day, distance));
                    }

                    // 그래프 초기화 및 스타일 설정
                    setupGraph();
                    // 그래프 갱신
                    updateGraph();

                    // UI 업데이트를 메인(UI) 스레드에서 수행
                    runOnUiThread(new Runnable() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void run() {
                            chart.setVisibility(View.VISIBLE);
                            noDataTextView.setVisibility(View.GONE);
                            monthTextView.setText(firstDayOfMonth.split("-")[0] + "年 / " + firstDayOfMonth.split("-")[1] + "月");
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                // 데이터가 없을 경우
                Toast.makeText(GraphActivity.this, "NO DATA", Toast.LENGTH_SHORT).show();

                // UI 업데이트를 메인(UI) 스레드에서 수행
                runOnUiThread(new Runnable() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void run() {
                        chart.setVisibility(View.GONE);
                        noDataTextView.setVisibility(View.VISIBLE);
                        monthTextView.setText(currentYear + "年 / " + currentMonth + "月");
                    }
                });
            }
        }
    }

    private void setupGraph() {
        // 그래프 스타일 설정
        chart.setBackgroundColor(Color.TRANSPARENT);
        chart.getDescription().setEnabled(false);

        // X축 설정
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(12f);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(true);
        xAxis.setGranularity(1f);

        // X축 라벨 간격 조절
        float labelInterval = 1f;
        xAxis.setGranularity(labelInterval);

        // 그래프 스케롤 가능하도록 설정
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);

        // x축과 범례 사이의 간격 조절
        xAxis.setSpaceMin(0.5f); // 최소 간격
        xAxis.setSpaceMax(0.5f); // 최대 간격

        // X축 라벨 포매터 설정
        xAxis.setValueFormatter(new com.github.mikephil.charting.formatter.ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return (int) value + "日";
            }
        });

        // Legend 설정
        Legend legend = chart.getLegend();
        legend.setForm(Legend.LegendForm.CIRCLE); // 범례 아이콘 모양을 원으로 설정 (다른 모양으로도 설정 가능)

        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setYOffset(20f); // 범례와 차트 사이의 간격 조절
        legend.setXEntrySpace(30f);
        legend.setTextSize(15f); // 범례 텍스트 크기 조절

        // 초기에 표시되는 범위를 설정
        float maxVisibleRange = stepsEntries.size();
        float visibleRange = 5f;

        // 그래프 스케일 조정 비활성화
        chart.setScaleEnabled(false);

        // 초기에 보여지는 범위의 시작점을 마지막 10개 데이터의 시작으로 이동하여 오른쪽으로 스크롤되도록 함
        chart.moveViewToX(maxVisibleRange - visibleRange);

        // 그래프 데이터가 변경되었을 때 X축의 범위가 함께 변경되도록 리스너 설정
        chart.setOnChartValueSelectedListener(new com.github.mikephil.charting.listener.OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, com.github.mikephil.charting.highlight.Highlight h) {
                float selectedX = e.getX();

                if (selectedX > visibleRange) {
                    chart.moveViewToX(selectedX - visibleRange);
                } else {
                    chart.moveViewToX(0);
                }
            }

            public void onNothingSelected() {
                // 선택 해제 시 필요한 동작을 처리할 수 있음 (여기서는 필요 없음)
            }
        });

        chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                // 범례 항목을 클릭할 때 수행할 동작을 여기에 추가
                String label = chart.getData().getDataSetByIndex(h.getDataSetIndex()).getLabel();
                toggleDataSetVisibility(label); // 데이터셋 표시/숨김 토글
            }

            @Override
            public void onNothingSelected() {
                // 선택 해제 시 필요한 동작을 처리할 수 있음 (여기서는 필요 없음)
            }
        });
    }

    private void toggleDataSetVisibility(String label) {
        for (ILineDataSet dataSet : chart.getData().getDataSets()) {
            if (dataSet.getLabel().equals(label)) {
                dataSet.setVisible(!dataSet.isVisible());
            }
        }
        chart.invalidate(); // 그래프 갱신
    }

    private void updateGraph() {
        // 그래프 데이터 설정
        LineDataSet stepsDataSet = new LineDataSet(stepsEntries, "STEPS");
        LineDataSet caloriesDataSet = new LineDataSet(caloriesEntries, "CALORIES");
        LineDataSet distanceDataSet = new LineDataSet(distanceEntries, "DISTANCE");

        // 그래프 스타일 및 선 색상 설정
        stepsDataSet.setColor(Color.RED);
        stepsDataSet.setCircleColor(Color.RED);
        stepsDataSet.setLineWidth(2f); // 선 굵기 설정
        stepsDataSet.setValueTextSize(15f); // 텍스트 크기 설정
        stepsDataSet.setCircleRadius(6f); // 원의 크기 설정

        caloriesDataSet.setColor(Color.BLUE);
        caloriesDataSet.setCircleColor(Color.BLUE);
        caloriesDataSet.setLineWidth(2f); // 선 굵기 설정
        caloriesDataSet.setValueTextSize(15f); // 텍스트 크기 설정
        caloriesDataSet.setCircleRadius(6f); // 원의 크기 설정

        distanceDataSet.setColor(Color.GREEN);
        distanceDataSet.setCircleColor(Color.GREEN);
        distanceDataSet.setLineWidth(2f); // 선 굵기 설정
        distanceDataSet.setValueTextSize(15f); // 텍스트 크기 설정
        distanceDataSet.setCircleRadius(6f); // 원의 크기 설정

        // 그래프에 데이터 설정
        LineData lineData = new LineData(stepsDataSet, caloriesDataSet, distanceDataSet);
        chart.setData(lineData);

        // 스크롤할 때마다 보여지는 데이터의 범위 변경
        float maxVisibleRange = stepsEntries.size();
        float visibleRange = 5f;
        if (maxVisibleRange > visibleRange) {
            chart.setVisibleXRange(visibleRange, visibleRange);
        } else {
            chart.setVisibleXRange(0, maxVisibleRange);
        }

        // 여백 조절
        chart.setExtraOffsets(10f, 10f, 10f, 10f); // 왼쪽, 위, 오른쪽, 아래 여백 설정

        chart.invalidate(); // 그래프 갱신
    }
}
