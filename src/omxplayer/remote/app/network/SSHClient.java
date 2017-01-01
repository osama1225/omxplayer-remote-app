package omxplayer.remote.app.network;

import java.io.ByteArrayOutputStream;

import omxplayer.remote.app.handlers.ConnectionServiceHandler;
import omxplayer.remote.app.tasks.CheckPauseLog;
import omxplayer.remote.app.utils.Utils;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import android.os.AsyncTask;
import android.util.Log;

public class SSHClient extends AsyncTask<Void, Void, Void> {

	private Session session;
	private ConnectionServiceHandler connectionServiceHandler;
	private CheckPauseLog pauseLog;

	public SSHClient(ConnectionServiceHandler connectionServiceHandler) {
		this.connectionServiceHandler = connectionServiceHandler;
		executeOnExecutor(THREAD_POOL_EXECUTOR, new Void[] {});
	}

	@Override
	protected Void doInBackground(Void... params) {
		try {

			JSch channel = new JSch();
			session = channel.getSession(Utils.uName, Utils.hostName);
			session.setConfig("StrictHostKeyChecking", "no");
			session.setPassword(Utils.password);
			session.connect();
			Utils.connected = true;
		} catch (Exception e) {
			Log.d("error", "Error in connecting: " + e.getMessage());
			Utils.connected = false;
		}
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		if (Utils.connected) {
			connectionServiceHandler.connectionEstablished();
			pauseLog = new CheckPauseLog(connectionServiceHandler, this);
		} else {
			connectionServiceHandler.connectionFailed();
		}
	}

	public synchronized String executeCmd(String cmd) {
		try {
			ChannelExec channelssh = (ChannelExec) session.openChannel("exec");
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			channelssh.setOutputStream(baos);
			// Execute command
			channelssh.setCommand(cmd);
			channelssh.connect();
			Thread.sleep(400);
			String response = baos.toString();
			channelssh.disconnect();
			return response;
		} catch (Exception e) {
			Log.d("Error", "error in executing a command: " + e.getMessage());
			closeConnection();
			connectionServiceHandler.connectionLost();
		}
		return "";
	}

	public void closeConnection() {
		Utils.connected = false;
		if (session != null)
			session.disconnect();
		session = null;
		if (pauseLog != null)
			pauseLog = null;
	}

}
