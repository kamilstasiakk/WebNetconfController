package pl.kamilstasiak.entities.requests;

/**
 * Created by Kamil on 2017-01-06.
 */
public class DeleteRouteRecordRequest {
    String prefix;
    String ipAddress; // router management address
    String outgoingInterface;

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getOutgoingInterface() {
        return outgoingInterface;
    }

    public void setOutgoingInterface(String outgoingInterface) {
        this.outgoingInterface = outgoingInterface;
    }
}
