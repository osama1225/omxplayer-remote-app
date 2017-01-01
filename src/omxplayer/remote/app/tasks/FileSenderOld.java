package omxplayer.remote.app.tasks;
//package com.example.autovideoplayerremoterasp;
//
//import java.io.ByteArrayOutputStream;
//import java.io.File;
//import java.io.FileInputStream;
//
//import com.jcraft.jsch.ChannelExec;
//import com.jcraft.jsch.ChannelSftp;
//import com.jcraft.jsch.JSch;
//import com.jcraft.jsch.Session;
//import com.jcraft.jsch.SftpATTRS;
//
//import android.content.Context;
//import android.os.AsyncTask;
//import android.util.Log;
//import android.widget.Toast;
//
//public class FileSender extends AsyncTask<Void, Void, Boolean> {
//
//	private VideoItem item;
//	private VideoSentListener finishedListener;
//	private Context c;
//	private Session session;
//	private ChannelSftp channelSftp;
//	private ChannelExec channelssh;
//	private WifiConnection conn;
//	public FileSender(WifiConnection conn) {
//		this.conn=conn;
//	}
//
//	public void exec(Context c, VideoItem item) {
//		this.c = c;
//		this.item = new VideoItem(item.getName(), item.getPath(),
//				item.getVideoImage(), item.getSize());
//		executeOnExecutor(THREAD_POOL_EXECUTOR, new Void[] {});
//	}
//
//	@Override
//	protected void onPreExecute() {
//		super.onPreExecute();
//		notifyListener(false);
//		Toast.makeText(c, "Sending...", Toast.LENGTH_SHORT).show();
//	}
//
//	@Override
//	protected Boolean doInBackground(Void... params) {
//
//		try {
//			JSch channel = new JSch();
//			session = channel.getSession(Utils.uName, Utils.hostName);
//			session.setConfig("StrictHostKeyChecking", "no");
//			session.setPassword(Utils.password);
//			session.connect();
//			channelssh = (ChannelExec) session.openChannel("exec");
//			ByteArrayOutputStream baos = new ByteArrayOutputStream();
//			channelssh.setOutputStream(baos);
//			// Execute command
//			channelssh.setCommand("mv " + item.getPath() + " Videos/"
//					+ item.getName());
//			channelssh.connect();
//			String response = baos.toString();
//			channelssh.disconnect();
//
//			while (true) {
//				 response = conn.send(Utils.retrieveRemovalListCmd);
//				if (!response.equals("")) {
//					if (response.contains(item.getName()))
//						break;
//				}
//				Thread.sleep(5000);
//
//			}
//			return true;
//		} catch (Exception ex) {
//			if (channelssh != null)
//				channelssh.disconnect();
//			if (session != null)
//				session.disconnect();
//			Log.d("hamada", "Can't send the video: " + ex.getMessage());
//		}
//
//		return false;
//	}
//
//	@Override
//	protected void onPostExecute(Boolean result) {
//
//		if (!result) {
//			if (MainActivity.sound != null) {
//				Toast.makeText(c, "Not Sent!", Toast.LENGTH_LONG).show();
//				MainActivity.sound.play(c, R.raw.fail);
//				notifyListener(false);
//			}
//		} else {
//			// check the length of the file on the server
//			try {
//
//				Toast.makeText(c, "Successfully Sent!", Toast.LENGTH_LONG)
//						.show();
//				MainActivity.sound.play(c, R.raw.success);
//				notifyListener(true);
//
//			} catch (Exception e) {
//				Log.d("error", "error after sending: " + e.getMessage());
//			}
//		}
//		// close connection
//		channelssh.disconnect();
//		session.disconnect();
//		session = null;
//		super.onPostExecute(result);
//	}
//
//	public void setFinishedListener(VideoSentListener finishedListener) {
//		this.finishedListener = finishedListener;
//	}
//
//	public void notifyListener(boolean finished) {
//		if (finishedListener != null)
//			finishedListener.finishedSending(finished);
//	}
//}
