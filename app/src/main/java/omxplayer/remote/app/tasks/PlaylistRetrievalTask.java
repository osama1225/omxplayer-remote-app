package omxplayer.remote.app.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.ListView;
import android.widget.ProgressBar;

import omxplayer.remote.app.MainActivity;
import omxplayer.remote.app.adapters.PlayListAdapter;
import omxplayer.remote.app.network.CommandSender;
import omxplayer.remote.app.utils.Utils;

/**
 * @author mohamedossama
 */
public class PlaylistRetrievalTask extends AsyncTask<Void, Void, String[]> {

    private CommandSender commandSender;
    private PlayListAdapter playListAdapter;
    private ListView playlistView;
    private Context context;
    private boolean initializationMode;

    public PlaylistRetrievalTask(CommandSender commandSender, PlayListAdapter playListAdapter,
                                 ListView playlistView, Context context, boolean initializationMode) {
        this.commandSender = commandSender;
        this.playListAdapter = playListAdapter;
        this.playlistView = playlistView;
        this.context = context;
        this.initializationMode = initializationMode;
    }

    @Override
    protected void onPreExecute() {
        if (initializationMode) {
            MainActivity.progressBar.setVisibility(ProgressBar.VISIBLE);
        }
        super.onPreExecute();
    }

    @Override
    protected String[] doInBackground(Void... voids) {
        String response = commandSender.send(Utils.SSHCommands.retrieveplaylistCmd);
        String[] videoNames = null;
        if (!"".equals(response)) {
            videoNames = response.split("\n");
        }
        playListAdapter.setContext(context);
        return videoNames;
    }

    @Override
    protected void onPostExecute(String[] videoNames) {
        playListAdapter.setNamesAndRefresh(videoNames);
        if (initializationMode) {
            MainActivity.progressBar.setVisibility(ProgressBar.INVISIBLE);
            playlistView.setAdapter(playListAdapter);
        }
        super.onPostExecute(videoNames);
    }
}
