package jp.ac.jec.cm0135.running;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

public class RunningService extends Service implements SensorEventListener {

    private SensorManager sm;
    private Sensor sensor_step_detector;
    private PowerManager.WakeLock wakeLock;

    public RunningService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor_step_detector = sm.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);

        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyApp::MyWakelockTag");

        wakeLock.acquire(); // 서비스가 동작 중일 때 화면이 켜져 있도록 함
        sm.registerListener(this, sensor_step_detector, SensorManager.SENSOR_DELAY_NORMAL);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        wakeLock.release(); // 서비스 종료 시 화면이 꺼지도록 함
        sm.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // 센서 유형이 스텝 감지 센서인 경우 걸음 수 +1
        switch (event.sensor.getType()) {
            case Sensor.TYPE_STEP_DETECTOR:
                RunningActivity.steps++;
                Log.i("iii", "iii STEPS : " + RunningActivity.steps);

                // 브로드캐스트 메시지 전송
                Intent intent = new Intent("steps-update");
                intent.putExtra("steps", RunningActivity.steps);
                sendBroadcast(intent);
                break;
        }
    }
}
