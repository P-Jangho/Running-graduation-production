package jp.ac.jec.cm0135.running;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;

public class WeatherJobScheduler {

    private static final int JOB_ID = 1;
    private static final long JOB_INTERVAL = 24 * 60 * 60 * 1000; // 24시간 주기

    public static void scheduleJob(Context context) {
        ComponentName componentName = new ComponentName(context, WeatherJobService.class);
        JobInfo jobInfo = new JobInfo.Builder(JOB_ID, componentName)
                .setPeriodic(JOB_INTERVAL)
                .build();

        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(jobInfo);
    }
}
