package jp.ac.jec.cm0135.running;

import android.annotation.SuppressLint;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.util.Log;

@SuppressLint("SpecifyJobSchedulerIdRange")
public class WeatherJobService extends JobService {

    private static final String TAG = "WeatherJobService";

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(TAG, "onStartJob");

        // 백그라운드 작업을 수행할 AsyncTask 실행
        WeatherTaskWeek weatherTaskWeek = new WeatherTaskWeek(getApplicationContext());
        weatherTaskWeek.execute();

        // 작업이 완료되면 true 반환
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(TAG, "onStopJob");

        // 작업이 중지될 경우 다시 예약하도록 true 반환
        return true;
    }
}

