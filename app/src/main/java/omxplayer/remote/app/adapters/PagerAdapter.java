package omxplayer.remote.app.adapters;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import omxplayer.remote.app.fragments.PlayListFragment;
import omxplayer.remote.app.fragments.TransparentFragment;
import omxplayer.remote.app.handlers.ConnectionServiceHandler;
import omxplayer.remote.app.network.CommandSender;

public class PagerAdapter extends FragmentStatePagerAdapter {

    private final int NUM_PAGES = 2;
    private PlayListFragment playListFragment;
    private TransparentFragment transparentFragment;

    public PagerAdapter(FragmentManager fm, CommandSender commandSender, ConnectionServiceHandler connectionServiceHandler) {
        super(fm);
        transparentFragment = new TransparentFragment();
        playListFragment = new PlayListFragment(commandSender, connectionServiceHandler);
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
