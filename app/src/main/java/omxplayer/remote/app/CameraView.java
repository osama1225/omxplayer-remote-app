package omxplayer.remote.app;

import android.content.Context;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

public class CameraView extends SurfaceView implements SurfaceHolder.Callback {

	private SurfaceHolder mholder;
	private Camera cam;

	public CameraView(Context context, Camera cam) {
		super(context);
		this.cam = cam;
		// set surfaceholder callback to be notified when the surface is created
		// or destroyed
		mholder = getHolder();
		mholder.addCallback(this);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// try to start camera view
		try {
			cam.setPreviewDisplay(mholder);
			cam.startPreview();
		} catch (Exception e) {
			Toast.makeText(getContext(), "Can't start previewing!",
					Toast.LENGTH_LONG	).show();
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub

	}

}
