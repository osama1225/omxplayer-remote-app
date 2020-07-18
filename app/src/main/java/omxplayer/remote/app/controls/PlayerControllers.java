package omxplayer.remote.app.controls;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;

import omxplayer.remote.app.MainActivity;
import omxplayer.remote.app.R;
import omxplayer.remote.app.services.PlayerControlService;

public class PlayerControllers {

    private static MainActivity activity;

    public static void playPause() {
        if (activity != null) {
            activity.onClick(activity.findViewById(R.id.play_btn));
        }
    }

    public static void next() {
        if (activity != null) {
            activity.onClick(activity.findViewById(R.id.next_txt));
        }
    }

    public static void prev() {
        if (activity != null) {
            activity.onClick(activity.findViewById(R.id.prev_txt));
        }
    }

    public static void setActivity(MainActivity activity) {
        if (activity != null) {
            PlayerControllers.activity = activity;
        }
    }

    public static void createOnScreenNotification(String state,
                                                  boolean extraState) {
        if (activity != null) {
            Intent intent = new Intent(activity.getApplicationContext(),
                    PlayerControlService.class);
            intent.setAction(state);
            intent.putExtra("playing", extraState);
            activity.startService(intent);
        }
    }

    public static void destroyOnScreenNotification() {
        // Stop media player here
        if (activity != null) {
            NotificationManager notificationManager = (NotificationManager) activity
                    .getApplicationContext().getSystemService(
                            Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(1);
            Intent intent = new Intent(activity.getApplicationContext(),
                    PlayerControlService.class);
            activity.stopService(intent);
        }
    }
}
