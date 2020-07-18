package omxplayer.remote.app.network;

public interface CommandSender {

    public String send(String cmd, String... optionalParams);

}
