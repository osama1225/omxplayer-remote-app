package omxplayer.remote.app.adapters;

import omxplayer.remote.app.fragments.PlayListFragment;
import omxplayer.remote.app.fragments.TransparentFragment;
import omxplayer.remote.app.handlers.ConnectionServiceHandler;
import omxplayer.remote.app.network.WifiConnection;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class PagerAdapter extends FragmentStatePagerAdapter {

	private PlayListFragment playListFragment;
	private TransparentFragment transparentFragment;

	private final int NUM_PAGES = 2;

	public PagerAdapter(FragmentManager fm, WifiConnection wc, ConnectionServiceHandler connectionServiceHandler) {
		super(fm);
		transparentFragment = new TransparentFragment();
		playListFragment = new PlayListFragment(wc,connectionServiceHandler);
	}

	@Override
	public android.support.v4.app.Fragment getItem(int index) {
		switch (index) {
		case 0:
			return transparentFragment;
		case 1:
			return playListFragment;
		}
		return null;
	}

	@Override
	public int getCount() {
		return NUM_PAGES;
	}
}
