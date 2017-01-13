package omxplayer.remote.app.handlers;


import omxplayer.remote.app.R;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;

public class LongTouchListener implements OnTouchListener {

	public final static int FAST_FORWARD_MSG = 1;
	public final static int FAST_BACKWARD_MSG = 2;
	private final int MSG_DURATION = 600;
	private final int LONG_TOUCH_DURATION = 1000;

	private final Handler handler;
	private OnClickListener listener;
	private long beforePress;

	public LongTouchListener(final Activity context, final OnClickListener listener) {
		this.listener = listener;
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				Log.d("hamada", "long press fired!!");
				// arg1 --> view id
				// arg2 --> schedule duration
				View view = context.findViewById(msg.arg1);
				if (view.getTag() != null) {
					LongTouchListener.this.listener.onClick(view);
						Message newMsg = new Message();
						newMsg.copyFrom(msg);
						handler.sendMessageDelayed(newMsg, newMsg.arg2);
				}
			}
		};
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			beforePress = System.currentTimeMillis();
			Message msg = new Message();
			msg.arg1 = v.getId();
			msg.arg2 = MSG_DURATION;
			v.setTag(v);
			switch (v.getId()) {
			case R.id.next_txt:
				msg.what = FAST_FORWARD_MSG;
				break;
			case R.id.prev_txt:
				msg.what = FAST_BACKWARD_MSG;
				break;
			}
			handler.sendMessageDelayed(msg, msg.arg2);
			break;
		case MotionEvent.ACTION_UP:
			if ((System.currentTimeMillis() - beforePress) < LONG_TOUCH_DURATION) {
				handler.removeMessages(FAST_FORWARD_MSG);
				handler.removeMessages(FAST_BACKWARD_MSG);
				v.setTag(new String("Regular"));
				listener.onClick(v);
			}	
			v.setTag(null);
			break;
		case MotionEvent.ACTION_CANCEL:
			v.setTag(null);
			break;
		}
		return true;
	}

}
