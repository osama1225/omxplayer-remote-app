package omxplayer.remote.app.dialogs;

import omxplayer.remote.app.adapters.CustomAdapter;
import android.app.Dialog;
import android.content.Context;

public abstract class CustomDialog extends Dialog{

	public CustomDialog(Context context) {
		super(context);
	}

	public abstract void prepareAndShow(CustomAdapter adapter);
}
