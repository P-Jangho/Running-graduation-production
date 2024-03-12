package jp.ac.jec.cm0135.running;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDate2 = dateFormat2.format(calendar.getTime());
        // 작업을 수행할 코드를 여기에 추가
        new SendDataTask().execute(currentDate2, "0", "0", "0");
    }

    public static void setAlarm(Context context) {
        // AlarmManager를 사용하여 매일 2시 41분에 알람 예약
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        // 현재 시간을 가져와서 Calendar 객체 생성
        long currentTime = System.currentTimeMillis();
        long nextAlarmTime = getTomorrow2AM41InMillis(currentTime);

        // 다음 알람 시간이 현재 시간보다 이전이면 다음 날로 설정
        if (nextAlarmTime <= currentTime) {
            nextAlarmTime += AlarmManager.INTERVAL_DAY;
        }

        // 알람 예약
        alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                nextAlarmTime,
                AlarmManager.INTERVAL_DAY,
                pendingIntent
        );
    }

    private static long getTomorrow2AM41InMillis(long currentTime) {
        // 현재 시간을 Calendar 객체로 변환
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(currentTime);

        calendar.set(Calendar.HOUR_OF_DAY, 2);
        calendar.set(Calendar.MINUTE, 05);
        calendar.set(Calendar.SECOND, 0);

        return calendar.getTimeInMillis();
    }
}

//어플이 활성화되어 있을 경우 핸드폰이 꺼져있거나 다른 작업을 하고 있어도 지정된 시간에 코드가 작동됨