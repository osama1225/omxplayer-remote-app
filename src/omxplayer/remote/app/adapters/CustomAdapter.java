package omxplayer.remote.app.adapters;

import java.util.List;

import android.widget.BaseAdapter;

public abstract class CustomAdapter<T> extends BaseAdapter{

	public abstract void setSelectedIndex(int index);
	
	public abstract void setItems(List<T> items);
}
