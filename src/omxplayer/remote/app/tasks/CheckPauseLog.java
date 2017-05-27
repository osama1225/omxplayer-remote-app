package omxplayer.remote.app.tasks;

import omxplayer.remote.app.handlers.ConnectionServiceHandler;
import omxplayer.remote.app.network.SSHClient;
import omxplayer.remote.app.utils.Utils;
import android.os.AsyncTask;
import android.util.Log;

public class CheckPauseLog extends AsyncTask<Void, Void, Void> {

	private ConnectionServiceHandler connectionServiceHandler;
	private SSHClient client;

	public CheckPauseLog(ConnectionServiceHandler connectionServiceHandler, SSHClient client) {
		this.connectionServiceHandler = connectionServiceHandler;
		this.client = client;
		executeOnExecutor(THREAD_POOL_EXECUTOR, new Void[] {});
	}

	@Override
	protected Void doInBackground(Void... params) {

		try {
			String state = client.executeCmd(Utils.SSHCommands.pauseLogCmd);
			connectionServiceHandler.changePlayState(state);
		} catch (Exception e) {
			Log.d("error", "Error in connecting: " + e.getMessage());
			Utils.connected = false;

		}
		return null;
	}
}