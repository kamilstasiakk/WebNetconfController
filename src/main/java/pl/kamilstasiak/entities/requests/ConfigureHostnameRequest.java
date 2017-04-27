package pl.kamilstasiak.entities.requests;

/**
 * Created by Kamil on 2016-10-31.
 */
public class ConfigureHostnameRequest {
    private String ipAddress;
    private String newHostname;

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getNewHostname() {
        return newHostname;
    }

    public void setNewHostname(String newHostname) {
        this.newHostname = newHostname;
    }
}
