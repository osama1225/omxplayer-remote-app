package omxplayer.remote.app.adapters;

import java.util.List;

import android.widget.BaseAdapter;

public abstract class CustomAdapter<T> extends BaseAdapter{

	public abstract void toggleFromSelectedIndecies(int index);
	
	public abstract List<Integer> getSelectedIndecies();
	
	public abstract void setItems(List<T> items);
}
