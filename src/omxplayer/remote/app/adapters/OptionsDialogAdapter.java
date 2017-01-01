package omxplayer.remote.app.adapters;

import omxplayer.remote.app.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class OptionsDialogAdapter extends BaseAdapter {

	private int[] options;
	private int[] optionsPressed;
	private Context c;

	private int pressed;

	public OptionsDialogAdapter(Context c) {

		options = new int[3];
		optionsPressed = new int[3];

		options[0] = R.drawable.send_media;
		options[1] = R.drawable.remove_media;
		options[2] = R.drawable.close_app;

		optionsPressed[0] = R.drawable.send_media_p;
		optionsPressed[1] = R.drawable.remove_media_p;
		optionsPressed[2] = R.drawable.close_app_p;

		pressed = -1;
		this.c = c;

	}

	@Override
	public int getCount() {
		return 3;
	}

	@Override
	public Object getItem(int arg0) {
		return options[arg0];
	}

	@Override
	public long getItemId(int arg0) {
		return options[arg0];
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		View view = LayoutInflater.from(c).inflate(R.layout.options_item, null);
		ImageView v = (ImageView) view.findViewById(R.id.image_item_id);
		if (pressed == arg0)
			v.setImageResource(optionsPressed[arg0]);
		else
			v.setImageResource(options[arg0]);
		return view;
	}

	public void setPressed(int pressed) {
		this.pressed = pressed;
	}
}
