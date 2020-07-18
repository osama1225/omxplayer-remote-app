package omxplayer.remote.app.handlers;

// This interface will let us communicate between the main activity and the
// client class
public interface ConnectionServiceHandler {

    public void connectionFailed();

    public void connectionLost();

    public void connectionEstablished();

    public void changePlayState(String state);

}