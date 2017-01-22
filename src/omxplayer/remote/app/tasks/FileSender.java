package omxplayer.remote.app.tasks;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import omxplayer.remote.app.VideoSentListener;
import omxplayer.remote.app.network.CommandSender;
import omxplayer.remote.app.utils.Sound;
import omxplayer.remote.app.utils.Utils;
import omxplayer.remote.app.R;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

/*
 * send the video file on another socket
 */
public class FileSender extends AsyncTask<Void, Integer, String> {

	private final int portNo = 8888;
	private Socket socket;
	private FileInputStream fis;
	private OutputStream out;
	private ObjectOutputStream oOut;
	private Context c;
	private Dialog progressDialog;
	private ProgressBar bar;
	private TextView percentage;
	private VideoSentListener finishedListener;
	private CommandSender commandSender;
	private boolean canceled;
	private File f;
	private Sound sound;

	public FileSender(Sound sound) {
		this.sound = sound;
	}

	public void exec(Context c, String fileName, CommandSender commandSender) {
		try {
			f = new File(fileName);
			fis = new FileInputStream(f);
			this.c = c;
			this.commandSender = commandSender;
			canceled = false;
		} catch (Exception e) {
			closeStreams();
			return;
		}
		executeOnExecutor(THREAD_POOL_EXECUTOR, new Void[] {});

	}

	@Override
	protected String doInBackground(Void... params) {

		try {
			socket = new Socket();
			socket.bind(null);
			socket.connect(new InetSocketAddress(Utils.hostName, portNo));
			out = socket.getOutputStream();
			oOut = new ObjectOutputStream(out);
			// send name
			oOut.writeUTF(f.getName());
			// send length
			oOut.writeLong(f.length());
			// sand data
			byte[] data = new byte[1024];
			long sent = 0;
			long length = f.length();
			int x = fis.read(data);
			while (x != -1) {
				oOut.write(data);
				oOut.reset();
				sent += x;
				publishProgress((int) ((sent * 100) / length));
				x = fis.read(data);
			}
			fis.close();
			closeStreams();
			progressDialog.dismiss();
			return "sent";

		} catch (Exception e) {
			try {
				progressDialog.dismiss();
				if (fis != null)
					fis.close();
			} catch (Exception e1) {
				closeStreams();
			}
		}

		return "error";
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		bar.setProgress(values[0]);
		percentage.setText(String.valueOf(values[0]) + "%");
		super.onProgressUpdate(values);
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		if (result.equals("error")) {
			// remove the partially created there
			commandSender.send(Utils.removeCmd, f.getName());
			if (sound != null) {
				Toast.makeText(c, "Not Sent!", Toast.LENGTH_LONG).show();
				sound.play(c, R.raw.fail);
			}
			// notifyListener(false);
		} else {
			Toast.makeText(c, "Successfully Sent!", Toast.LENGTH_LONG).show();
			sound.play(c, R.raw.success);
		}
		notifyListener(true);

	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		notifyListener(false);

		showProgressDialog();
	}

	public void setFinishedListener(VideoSentListener finishedListener) {
		this.finishedListener = finishedListener;
	}

	public void notifyListener(boolean finished) {
		if (finishedListener != null)
			finishedListener.finishedSending(finished);
	}

	public void closeStreams() {
		try {
			if (fis != null)
				fis.close();
			if (oOut != null)
				oOut.close();
			if (out != null)
				out.close();
			if (socket != null && !socket.isClosed())
				socket.close();

		} catch (Exception e) {
			Log.d("koko", "Error in closing of sending video");
		}
	}

	private void showProgressDialog() {
		progressDialog = new Dialog(c);
		progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		progressDialog.setContentView(R.layout.progress_view);
		progressDialog.getWindow().clearFlags(
				WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		progressDialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(Color.TRANSPARENT));

		percentage = (TextView) progressDialog.findViewById(R.id.percentage);
		bar = (ProgressBar) progressDialog.findViewById(R.id.bar_id);
		bar.setProgress(0);
		bar.setIndeterminate(false);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				canceled = true;
				closeStreams();
			}
		});
		// progressDialog.setCancelable(false);
		// bar.setProgressDrawable(c.getResources().getDrawable(R.drawable.progress_style));
		bar.setMax(100);
		progressDialog.show();
	}

	public boolean isCanceled() {
		return canceled;
	}
}
