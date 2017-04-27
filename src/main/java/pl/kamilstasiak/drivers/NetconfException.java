package pl.kamilstasiak.drivers;

/**
 * Created by Kamil on 2016-10-30.
 */
import java.io.IOException;

public class NetconfException extends IOException {

    public final String netconfErrorMsg;

    NetconfException(String msg) {
        super(msg);
        netconfErrorMsg = msg;
    }

    public String getNetconfErrorMessage() {
        return netconfErrorMsg;
    }
}
