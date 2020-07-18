package omxplayer.remote.app.utils;

import android.content.Context;
import android.os.PowerManager;

/*
 * class that acquire the device to wake. to keep the GCMIntentService working in the background waiting for an action
 */
public abstract class WakeLocker {
    private static PowerManager.WakeLock wakeLock;

    @SuppressWarnings("deprecation")
    public static void acquire(Context context) {
        if (wakeLock != null)
            wakeLock.release();

        PowerManager pm = (PowerManager) context
                .getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK
                | PowerManager.ACQUIRE_CAUSES_WAKEUP
                | PowerManager.ON_AFTER_RELEASE, "WakeLock");
        wakeLock.acquire();
    }

    public static void release() {
        if (wakeLock != null)
            wakeLock.release();
        wakeLock = null;
    }
}
