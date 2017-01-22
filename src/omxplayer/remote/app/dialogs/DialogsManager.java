package omxplayer.remote.app.dialogs;

import android.content.Context;
import omxplayer.remote.app.network.WifiConnection;
import omxplayer.remote.app.tasks.dialogs.RemovalVideoListPreparationAndViewTask;
import omxplayer.remote.app.tasks.dialogs.StoredVideoListPreparationAndViewTask;
import omxplayer.remote.app.utils.Sound;

public class DialogsManager {

	private StoredVideoListDialog storedVideoListDialog;
	private VideoListRemovalDialog videoListRemovalDialog;
	private AdminPasswordDialog adminPasswordDialog;
	private OptionsDialog optionsDialog;
	private ScanDialog scanDialog;
	private NetworkPasswordDialog networkPasswordDialog;

	private WifiConnection wifiConnection;
	private Context context;

	public DialogsManager(Context context, WifiConnection wifiConnection,
			Sound sound) {
		this.wifiConnection = wifiConnection;
		this.context = context;
		storedVideoListDialog = new StoredVideoListDialog(context, wifiConnection, sound);
		videoListRemovalDialog = new VideoListRemovalDialog(context, wifiConnection);
		adminPasswordDialog = new AdminPasswordDialog(context, wifiConnection);
		optionsDialog = new OptionsDialog(context, sound, this);
		scanDialog = new ScanDialog(context, wifiConnection);
		networkPasswordDialog = new NetworkPasswordDialog(context,
				wifiConnection);
	}

	public void prepareAndShowSendVideoListDialog() {
		StoredVideoListPreparationAndViewTask storedVideoListPreparationAndViewTask = new StoredVideoListPreparationAndViewTask(context,storedVideoListDialog);
		storedVideoListPreparationAndViewTask.execute();
	}

	public void prepareAndShowRemoalVideoListDialog() {
		RemovalVideoListPreparationAndViewTask removalVideoListPreparationAndViewTask = new RemovalVideoListPreparationAndViewTask(
				context, wifiConnection,videoListRemovalDialog);
		removalVideoListPreparationAndViewTask.execute();
	}

	public void prepareAndShowPasswordDialog() {
		adminPasswordDialog.show();
	}

	public void prepareAndShowOptionsDialog() {
		optionsDialog.show();
	}

	public void showScanDialog() {
		scanDialog.prepareAndShow(wifiConnection.getNetworksAdapter());
	}

	public void showNetworkPasswordDialog() {
		networkPasswordDialog.show();
	}

	public ScanDialog getScanDialog() {
		return scanDialog;
	}
}
