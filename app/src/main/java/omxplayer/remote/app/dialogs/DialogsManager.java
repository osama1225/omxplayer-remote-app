package omxplayer.remote.app.dialogs;

import android.app.Activity;

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
    private NetworkModeSelectionDialog networkModeSelectionDialog;

    private WifiConnection wifiConnection;
    private Activity activity;

    public DialogsManager(Activity activity, WifiConnection wifiConnection,
                          Sound sound) {
        this.wifiConnection = wifiConnection;
        this.activity = activity;
        storedVideoListDialog = new StoredVideoListDialog(this.activity, wifiConnection, sound);
        videoListRemovalDialog = new VideoListRemovalDialog(this.activity, wifiConnection);
        adminPasswordDialog = new AdminPasswordDialog(this.activity, wifiConnection);
        optionsDialog = new OptionsDialog(this.activity, this);
        scanDialog = new ScanDialog(this.activity, wifiConnection);
        networkPasswordDialog = new NetworkPasswordDialog(this.activity,
                wifiConnection);
        networkModeSelectionDialog = new NetworkModeSelectionDialog(this.activity, wifiConnection);
    }

    public void prepareAndShowSendVideoListDialog() {
        StoredVideoListPreparationAndViewTask storedVideoListPreparationAndViewTask = new StoredVideoListPreparationAndViewTask(activity, storedVideoListDialog);
        storedVideoListPreparationAndViewTask.execute();
    }

    public void prepareAndShowRemovalVideoListDialog() {
        RemovalVideoListPreparationAndViewTask removalVideoListPreparationAndViewTask = new RemovalVideoListPreparationAndViewTask(
                activity, wifiConnection, videoListRemovalDialog);
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

    public void showNeworkModeSelectionDialog() {
        networkModeSelectionDialog.show();
    }

    public ScanDialog getScanDialog() {
        return scanDialog;
    }
}
