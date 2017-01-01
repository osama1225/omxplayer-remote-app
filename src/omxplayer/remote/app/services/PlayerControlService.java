package omxplayer.remote.app.services;

import omxplayer.remote.app.controls.PlayerControllers;

import omxplayer.remote.app.R;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.session.MediaController;
import android.media.session.MediaSession;
import android.media.session.MediaSession.Callback;
import android.os.IBinder;

public class PlayerControlService extends Service {

	public static final String ACTION_PLAY = "1";
	public static final String ACTION_PAUSE = "2";
	public static final String ACTION_NEXT = "3";
	public static final String ACTION_PREV = "4";
	public static final String ACTION_STOP = "7";
	public static final String ACTION_INIT = "8";

	private MediaSession mediaSession;
	private MediaController mediaController;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (mediaSession == null) {
			initMediaSessions();
		}
		handleIntent(intent);
		return super.onStartCommand(intent, flags, startId);
	}

	private void initMediaSessions() {
		mediaSession = new MediaSession(getApplicationContext(),
				"Holomote Session");
		mediaController = new MediaController(getApplicationContext(),
				mediaSession.getSessionToken());
		mediaSession.setCallback(new Callback() {
			@Override
			public void onPlay() {
				buildNotification(generateAction(
						android.R.drawable.ic_media_pause, "Pause",
						ACTION_PAUSE));
				PlayerControllers.playPause();
				super.onPlay();
			}

			@Override
			public void onPause() {
				buildNotification(generateAction(
						android.R.drawable.ic_media_play, "Play", ACTION_PLAY));
				PlayerControllers.playPause();
				super.onPause();
			}

			@Override
			public void onSkipToNext() {
				buildNotification(generateAction(
						android.R.drawable.ic_media_pause, "Pause",
						ACTION_PAUSE));
				PlayerControllers.next();
				super.onSkipToNext();
			}

			@Override
			public void onSkipToPrevious() {
				buildNotification(generateAction(
						android.R.drawable.ic_media_pause, "Pause",
						ACTION_PAUSE));
				PlayerControllers.prev();
				super.onSkipToPrevious();
			}

			@Override
			public void onStop() {
				PlayerControllers.destroyOnScreenNotification();
			}
		});
	}

	private void handleIntent(Intent intent) {
		if (intent == null || intent.getAction() == null) {
			return;
		}
		String action = intent.getAction();
		if (action.equals(ACTION_PAUSE)) {
			mediaController.getTransportControls().pause();
		} else if (action.equals(ACTION_PLAY)) {
			mediaController.getTransportControls().play();
		} else if (action.equals(ACTION_NEXT)) {
			mediaController.getTransportControls().skipToNext();
		} else if (action.equals(ACTION_PREV)) {
			mediaController.getTransportControls().skipToPrevious();
		} else if (action.equals(ACTION_STOP)) {
			mediaController.getTransportControls().stop();
		} else if (action.equals(ACTION_INIT)) {
			handleInitialState(intent.getExtras().getBoolean("playing", false));
		}

	}

	private void buildNotification(Notification.Action action) {

		Notification.MediaStyle style = new Notification.MediaStyle();
		Intent intent = new Intent(getApplicationContext(),
				PlayerControlService.class);
		intent.setAction(ACTION_STOP);
		PendingIntent pendingIntent = PendingIntent.getService(
				getApplicationContext(), 1, intent, 0);
		Notification.Builder builder = new Notification.Builder(this)
				.setSmallIcon(R.drawable.icon).setDeleteIntent(pendingIntent)
				.setStyle(style).setContentTitle("Holomote");

		builder.addAction(generateAction(android.R.drawable.ic_media_previous,
				"Previous", ACTION_PREV));
		builder.addAction(action);
		builder.addAction(generateAction(android.R.drawable.ic_media_next,
				"Next", ACTION_NEXT));
		style.setShowActionsInCompactView(0, 1, 2);

		NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(1, builder.build());

	}

	private Notification.Action generateAction(int icon, String title,
			String intentAction) {
		Intent intent = new Intent(getApplicationContext(),
				PlayerControlService.class);
		intent.setAction(intentAction);
		PendingIntent pendingIntent = PendingIntent.getService(
				getApplicationContext(), 1, intent, 0);
		return new Notification.Action.Builder(icon, title, pendingIntent)
				.build();
	}

	private void handleInitialState(boolean playing) {
		if (playing) {
			buildNotification(generateAction(android.R.drawable.ic_media_pause,
					"Pause", ACTION_PAUSE));
		} else {
			buildNotification(generateAction(android.R.drawable.ic_media_play,
					"Play", ACTION_PLAY));
		}
	}

	@Override
	public boolean onUnbind(Intent intent) {
		if (mediaSession != null) {
			mediaSession.release();
		}
		return super.onUnbind(intent);
	}
}
