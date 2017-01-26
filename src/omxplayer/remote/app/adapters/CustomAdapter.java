package omxplayer.remote.app.adapters;

import java.util.ArrayList;
import java.util.List;

import android.widget.BaseAdapter;

public abstract class CustomAdapter<T> extends BaseAdapter{

	protected List<Integer> selectedIndecies;

	public abstract void setItems(List<T> items);
	public abstract T[] getSelectedItems();
	
	public CustomAdapter() {
		selectedIndecies = new ArrayList<Integer>();
	}
	
	public void toggleFromSelectedIndecies(int selectedIndex) {
		if(selectedIndecies.contains(selectedIndex)){
			selectedIndecies.remove(Integer.valueOf(selectedIndex));
		}else{
			selectedIndecies.add(selectedIndex);
		}
		notifyDataSetChanged();
	}
	
}
