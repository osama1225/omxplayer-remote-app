package omxplayer.remote.app.tasks.dialogs;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Looper;
import android.view.View;
import android.widget.ProgressBar;

import omxplayer.remote.app.MainActivity;
import omxplayer.remote.app.adapters.CustomAdapter;
import omxplayer.remote.app.adapters.VideoListRemovalAdapter;
import omxplayer.remote.app.dialogs.CustomDialog;
import omxplayer.remote.app.network.CommandSender;
import omxplayer.remote.app.utils.Utils;

public class RemovalVideoListPreparationAndViewTask extends
        AsyncTask<Void, Void, Void> {

    private CommandSender commandSender;
    private CustomAdapter<String> videoListRemovalAdapter;
    private Context context;
    private CustomDialog<String> videoListRemovalDialog;

    public RemovalVideoListPreparationAndViewTask(Context context,
                                                  CommandSender commandSender, CustomDialog<String> videoListRemovalDialog) {
        this.commandSender = commandSender;
        this.context = context;
        this.videoListRemovalDialog = videoListRemovalDialog;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        MainActivity.progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected Void doInBackground(Void... params) {
        if (Looper.myLooper() == null)
            Looper.prepare();
        String response = commandSender.send(Utils.SSHCommands.retrieveplaylistCmd);
        String[] videoNames = response.split("\n");
        videoListRemovalAdapter = new VideoListRemovalAdapter(videoNames,
                context);
        if (Looper.myLooper() != null)
            Looper.myLooper().quit();
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        MainActivity.progressBar.setVisibility(ProgressBar.INVISIBLE);
        videoListRemovalDialog.prepareAndShow(videoListRemovalAdapter);
    }

    public void execute() {
        this.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{});
    }
}
