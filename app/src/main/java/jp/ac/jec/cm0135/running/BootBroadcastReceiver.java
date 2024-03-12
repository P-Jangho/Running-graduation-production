package jp.ac.jec.cm0135.running;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null && intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            // 재부팅이 완료되면 WeatherJobService 시작
            Intent serviceIntent = new Intent(context, WeatherJobService.class);
            context.startService(serviceIntent);
        }
    }
}

