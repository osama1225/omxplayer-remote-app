package omxplayer.remote.app;

import android.hardware.Camera;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import omxplayer.remote.app.adapters.PagerAdapter;
import omxplayer.remote.app.controls.PlayerControllers;
import omxplayer.remote.app.dialogs.DialogsManager;
import omxplayer.remote.app.handlers.ConnectionServiceHandler;
import omxplayer.remote.app.handlers.LongTouchListener;
import omxplayer.remote.app.network.SSHClient;
import omxplayer.remote.app.network.WifiConnection;
import omxplayer.remote.app.services.PlayerControlService;
import omxplayer.remote.app.tasks.FileSender;
import omxplayer.remote.app.utils.Sound;
import omxplayer.remote.app.utils.Utils;

public class MainActivity extends FragmentActivity implements OnClickListener {

    public static ProgressBar progressBar;
    protected static Sound sound;

    private boolean playing;
    private ImageButton playBtn;

    private boolean useImageBg;
    private Camera cam;
    private CameraView camView;
    private FrameLayout viewHolder;
    private FrameLayout containerLayout;

    private WifiConnection wifiConnection;
    private FileSender fileSender;
    private ViewPager pager;
    private PagerAdapter adapter;
    private LongTouchListener longTouchListener;

    private ConnectionServiceHandler connectionHandler;

    private DialogsManager dialogsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // to set it full screen and turn of title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // for full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        // the default is the cam src
        viewHolder = (FrameLayout) findViewById(R.id.cameraId);
        containerLayout = (FrameLayout) findViewById(R.id.container_id);
        useImageBg = true;
        changeBgSrc();

        playing = true;

        sound = new Sound();
        progressBar = (ProgressBar) findViewById(R.id.progressBar1);
        progressBar.setVisibility(View.INVISIBLE);

        initiateConnectionHandler();

        wifiConnection = new WifiConnection(this, connectionHandler);
        longTouchListener = new LongTouchListener(this, this);

        dialogsManager = new DialogsManager(MainActivity.this,
                getWifiConnection(), sound);
        wifiConnection.setDialogsManager(dialogsManager);

        pager = (ViewPager) findViewById(R.id.pager);
        adapter = new PagerAdapter(getSupportFragmentManager(), wifiConnection,
                connectionHandler);
        pager.setAdapter(adapter);
        pager.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                if (arg0 == 0)
                    swapViews(findViewById(R.id.controlles_container));
                else
                    swapViews(findViewById(R.id.pager));
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
        swapViews(findViewById(R.id.controlles_container));
        // WakeLocker.acquire(getApplicationContext());

        playBtn = (ImageButton) findViewById(R.id.play_btn);
        playBtn.setOnClickListener(this);
        // findViewById(R.id.next_txt).setOnClickListener(this);
        // findViewById(R.id.prev_txt).setOnClickListener(this);
        findViewById(R.id.inc_vol_txt).setOnClickListener(this);
        findViewById(R.id.dec_vol_txt).setOnClickListener(this);
        findViewById(R.id.movies_btn).setOnClickListener(this);
        findViewById(R.id.connect_btn).setOnClickListener(this);
        findViewById(R.id.src_changer_id).setOnClickListener(this);

        findViewById(R.id.next_txt).setOnTouchListener(longTouchListener);
        findViewById(R.id.prev_txt).setOnTouchListener(longTouchListener);
    }

    @Override
    public void onClick(View v) {

        if (pager.getCurrentItem() == 1)
            return;
        String cmd = "";
        switch (v.getId()) {
            case R.id.play_btn:
                if (!Utils.connected) {
                    Toast.makeText(this, "Not conected", Toast.LENGTH_SHORT).show();
                    return;
                }

                // updating the ui
                if (playing) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            playBtn.setImageResource(R.drawable.play);
                        }
                    });
                    cmd = Utils.SSHCommands.pauseCmd;
                } else {
                    playBtn.setImageResource(R.drawable.pause);
                    cmd = Utils.SSHCommands.playCmd;
                }
                playing = !playing;
                break;
            case R.id.next_txt:
                if (!Utils.connected) {
                    Toast.makeText(this, "Not conected", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (v.equals(v.getTag())) {
                    if (!playing)
                        onClick(findViewById(R.id.play_btn));
                    cmd = Utils.SSHCommands.fastForwardCmd;
                } else if (v.getTag() != null
                        && "Regular".equals(v.getTag().toString())) {
                    cmd = Utils.SSHCommands.nextCmd;
                }
                playing = true;
                playBtn.setImageResource(R.drawable.pause);
                break;
            case R.id.prev_txt:
                if (!Utils.connected) {
                    Toast.makeText(this, "Not conected", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (v.equals(v.getTag())) {
                    if (!playing)
                        onClick(findViewById(R.id.play_btn));
                    cmd = Utils.SSHCommands.rewindCmd;
                } else if (v.getTag() != null
                        && "Regular".equals(v.getTag().toString())) {
                    cmd = Utils.SSHCommands.prevCmd;
                }
                playing = true;
                playBtn.setImageResource(R.drawable.pause);
                break;
            case R.id.inc_vol_txt:
                if (!Utils.connected) {
                    Toast.makeText(this, "Not conected", Toast.LENGTH_SHORT).show();
                    return;
                }
                cmd = Utils.SSHCommands.incVolCmd;
                break;
            case R.id.dec_vol_txt:
                if (!Utils.connected) {
                    Toast.makeText(this, "Not conected", Toast.LENGTH_SHORT).show();
                    return;
                }
                cmd = Utils.SSHCommands.decVolCmd;
                break;
            case R.id.connect_btn:
                boolean allGranted = wifiConnection.askPermissionForNetworkScanning();
                if (allGranted) {
                    dialogsManager.showScanDialog();
                }
                break;
            case R.id.movies_btn:
                if (progressBar.getVisibility() != View.VISIBLE)
                    dialogsManager.prepareAndShowOptionsDialog();
                return;

            case R.id.src_changer_id:
                dialogsManager.showNeworkModeSelectionDialog();
//			changeBgSrc();
                break;
            default:
                cmd = "";
                return;
        }
        if (!cmd.equals("")) {
            sendControlMessage(cmd);
        }
    }

    /**
     * Sends a message.
     *
     * @param cmd A control command message to send.
     */
    private void sendControlMessage(final String cmd) {

        if (!Utils.connected) {
            Toast.makeText(this, "Not conected", Toast.LENGTH_SHORT).show();
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                wifiConnection.send(cmd);
            }
        }).start();

    }

    private void initiateConnectionHandler() {
        connectionHandler = new ConnectionServiceHandler() {

            @Override
            public void connectionFailed() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        MainActivity.progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(getApplicationContext(),
                                "Connection Failed", Toast.LENGTH_SHORT).show();
                        sound.play(getApplicationContext(), R.raw.fail);
                    }
                });
            }

            @Override
            public void connectionLost() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Connection Lost", Toast.LENGTH_SHORT).show();
                        PlayerControllers.destroyOnScreenNotification();
                        /*
                         * check connection to the network - if am connecting,
                         * try to establish ssh session again. - else the
                         * network maybe not exist anymore so do nothing
                         */
                        if (wifiConnection != null) {
                            if (wifiConnection.isConnected(Utils.SSID)) {
                                Toast.makeText(getApplicationContext(),
                                        "Reconnecting...", Toast.LENGTH_SHORT)
                                        .show();
                                MainActivity.progressBar
                                        .setVisibility(View.VISIBLE);
                                // establish new ssh session
                                wifiConnection.setSsh(null);
                                SSHClient client = new SSHClient(
                                        connectionHandler);
                                wifiConnection.setSsh(client);
                            }
                        }

                        Log.d("koko", "Disconnected");
                    }

                });
            }

            @Override
            public void connectionEstablished() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        MainActivity.progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(getApplicationContext(),
                                "Conection Established", Toast.LENGTH_SHORT)
                                .show();
                        sound.play(getApplicationContext(), R.raw.success);
                        Utils.connected = true;
                        Log.d("koko", "connected");
                        PlayerControllers.setActivity(MainActivity.this);
                    }
                });

            }

            @Override
            public void changePlayState(final String state) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Log.d("state", state);
                        if (state.toString().contains("s")) {
                            playBtn.setImageResource(R.drawable.play);
                            playing = false;
                        } else {
                            playBtn.setImageResource(R.drawable.pause);
                            playing = true;
                        }
                        PlayerControllers.createOnScreenNotification(
                                PlayerControlService.ACTION_INIT, playing);
                    }
                });
            }
        };
    }

    /*
     * Method that change the src of the bg either the bg image, or live stream
     * from the camera
     */
    @SuppressWarnings("deprecation")
    private void changeBgSrc() {
        if (!useImageBg) {
            /* for cam */
            try {
                containerLayout.setBackgroundResource(0);// remove the bg
                cam = Camera.open(0);
                cam.setDisplayOrientation(90);
                camView = new CameraView(getApplicationContext(), cam);
                viewHolder.addView(camView, 0);
            } catch (Exception e) {
                cam = null;
                Toast.makeText(getApplicationContext(),
                        "Can't use the camera!", Toast.LENGTH_SHORT).show();
            }
        } else {
            // remove the cam view
            containerLayout.setBackgroundResource(R.drawable.background);
            viewHolder.removeView(camView);
            // release the cam
            if (cam != null)
                cam.release();
            cam = null;
        }
        useImageBg = !useImageBg;
    }

    @Override
    public synchronized void onResume() {
        super.onResume();
        // wifiConn.registerNetworksReceiver();
    }

    @Override
    protected synchronized void onPause() {
        // wifiConn.unregisterNetworksReceiver();
        super.onPause();
    }

    @Override
    protected void onStop() {
        // TODO detect if screen is off
        super.onStop();
        if (Utils.connected) {
            PlayerControllers.createOnScreenNotification(
                    PlayerControlService.ACTION_INIT, playing);
        }
    }

    @Override
    protected void onDestroy() {
        PlayerControllers.destroyOnScreenNotification();
        if (wifiConnection != null)
            wifiConnection.close();
        wifiConnection = null;
        sound = null;
        if (fileSender != null) {
            fileSender = null;
        }
        if (cam != null) {
            cam.release();
        }
        // WakeLocker.release();
        progressBar = null;
        super.onDestroy();
        System.exit(0);
    }

    private void swapViews(View v) {
        containerLayout.bringChildToFront(v);
    }

    @Override
    public void onBackPressed() {
        if (pager.getCurrentItem() == 0)
            super.onBackPressed();
        else {
            // Otherwise, select the previous step.
            pager.setCurrentItem(pager.getCurrentItem() - 1);
        }
    }

    public WifiConnection getWifiConnection() {
        return wifiConnection;
    }

    public ConnectionServiceHandler getmHandler() {
        return connectionHandler;
    }
}
