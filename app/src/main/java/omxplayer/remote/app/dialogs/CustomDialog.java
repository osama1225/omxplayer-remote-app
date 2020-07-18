package omxplayer.remote.app.dialogs;

import android.app.Dialog;
import android.content.Context;

import omxplayer.remote.app.adapters.CustomAdapter;

public abstract class CustomDialog<T> extends Dialog {

    public CustomDialog(Context context) {
        super(context);
    }

    public abstract void prepareAndShow(CustomAdapter<T> adapter);
}
