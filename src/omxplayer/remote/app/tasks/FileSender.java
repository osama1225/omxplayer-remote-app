package omxplayer.remote.app.tasks;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import omxplayer.remote.app.VideoSentListener;
import omxplayer.remote.app.utils.Utils;
import omxplayer.remote.app.utils.Utils.SendStatus;
import omxplayer.remote.app.R;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Looper;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

/*
 * send the video file on another socket
 */
public class FileSender extends AsyncTask<String, Integer, Void> {

	private final int portNo = 8888;
	private Context c;
	private Dialog progressDialog;
	private ProgressBar progressBar;
	private TextView sentPercentageTextView;
	private TextView numOfSentFilesTextView;
	private VideoSentListener finishedListener;
	private boolean canceled;
	private long totalBytesToBeSent;
	private long totalSentBytes;
	private int numberOfFilesToBeSent;
	private int numberOfSentFiles;


	public FileSender() {
		totalBytesToBeSent = 0;
		totalSentBytes = 0;
		numberOfFilesToBeSent = 0;
		numberOfSentFiles = 0;
	}

	public void exec(Context c, String[] filePaths) {
		this.c = c;
		canceled = false;
		numberOfFilesToBeSent = filePaths.length;
		totalBytesToBeSent = calculateTotalBytesToBeSent(filePaths);
		executeOnExecutor(THREAD_POOL_EXECUTOR, filePaths);
	}

	private long calculateTotalBytesToBeSent(String[] filePaths) {
		long totalBytes = 0;
		for (String path : filePaths) {
			File file = new File(path);
			totalBytes += file.length();
		}
		return totalBytes;
	}

	@Override
	protected Void doInBackground(String... filePaths) {
		if (Looper.myLooper() == null){
			Looper.prepare();
		}	
		for (String filePath : filePaths) {
			if (canceled){
				return null;
			}
			SendStatus status =  sendFile(filePath);
			String fileName = filePath.substring(filePath.lastIndexOf("/") + 1, filePath.length());
			if(status.equals(SendStatus.SUCCESS)){
				numberOfSentFiles++;
			}else {
				fileName = Utils.videosDir + fileName;
			}
			notifyListener(status, fileName);
		}
		if (Looper.myLooper() != null){
			Looper.myLooper().quit();
		}
		return null;
	}

	private SendStatus sendFile(String filePath) {
		File file = new File(filePath);
		FileInputStream fileInputStream = null;
		ObjectOutputStream objectOutputStream = null;
		Socket socket = null;
		try {
			fileInputStream = new FileInputStream(file);
			socket = new Socket();
			socket.bind(null);
			socket.connect(new InetSocketAddress(Utils.hostName, portNo));
			objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
			// send name
			objectOutputStream.writeUTF(file.getName());
			// send length
			objectOutputStream.writeLong(file.length());
			// sand data
			byte[] data = new byte[1024];
			int read = fileInputStream.read(data);
			while (read != -1 && !canceled) {
				objectOutputStream.write(data);
				objectOutputStream.reset();
				publishProgress(read);
				read = fileInputStream.read(data);
			}
			objectOutputStream.flush();
			closeStreams(new InputStream[] { fileInputStream },
					new OutputStream[] { objectOutputStream}, socket);
			if(canceled){
				return SendStatus.CANCELED;
			}
			return SendStatus.SUCCESS;
		} catch (Exception e) {
			closeStreams(new InputStream[] { fileInputStream },
					new OutputStream[] { objectOutputStream}, socket);
		}
		return SendStatus.FAILED;
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		int lastSentChunkSize = values[0];
		totalSentBytes+= lastSentChunkSize;
		int percentageValue = (int)((totalSentBytes * 100) / totalBytesToBeSent); 
		progressBar.setProgress(percentageValue);
		sentPercentageTextView.setText(String.valueOf(percentageValue) + "%");
		numOfSentFilesTextView.setText("Sent: "+ numberOfSentFiles + "/" + numberOfFilesToBeSent);
		super.onProgressUpdate(values);
	}

	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		progressDialog.dismiss();
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		showProgressDialog();
	}

	public void setFinishedListener(VideoSentListener finishedListener) {
		this.finishedListener = finishedListener;
	}

	public void notifyListener(SendStatus state, String fileName) {
		if (finishedListener != null)
			finishedListener.finishedSending(state, fileName);
	}

	public void closeStreams(InputStream[] inputStreams,
			OutputStream[] outputStreams, Socket socket) {

		if (inputStreams != null) {
			for (InputStream inputStream : inputStreams) {
				if (inputStream != null) {
					try {
						inputStream.close();
					} catch (IOException e) {
						Log.d("Error:" + getClass(),
								"Error in closing stream: " + e);
					}
				}
			}
		}

		if (outputStreams != null) {
			for (OutputStream outputStream : outputStreams) {
				if (outputStream != null) {
					try {
						outputStream.close();
					} catch (IOException e) {
						Log.d("Error:" + getClass(),
								"Error in closing stream: " + e);
					}
				}
			}
		}
		if (socket != null) {
			try {
				socket.close();
			} catch (IOException e) {
				Log.d("Error:" + getClass(), "Error in closing socket: " + e);
			}
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

		sentPercentageTextView = (TextView) progressDialog.findViewById(R.id.percentage);
		numOfSentFilesTextView = (TextView) progressDialog.findViewById(R.id.filesSentTextView);
		progressBar = (ProgressBar) progressDialog.findViewById(R.id.bar_id);
		progressBar.setProgress(0);
		progressBar.setIndeterminate(false);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				canceled = true;
			}
		});
		// progressDialog.setCancelable(false);
		// bar.setProgressDrawable(c.getResources().getDrawable(R.drawable.progress_style));
		progressBar.setMax(100);
		progressDialog.show();
	}
}
