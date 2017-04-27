package pl.kamilstasiak.entities.requests;

/**
 * Created by Kamil on 2017-01-01.
 */
public class AddRouteRecordRequest {
    String nextHop;
    String ipAddress;
    String destNetworkAddress;
    String netmask;

    public String getDestNetworkAddress() {
        return destNetworkAddress;
    }

    public void setDestNetworkAddress(String destNetworkAddress) {
        this.destNetworkAddress = destNetworkAddress;
    }

    public String getNetmask() {
        return netmask;
    }

    public void setNetmask(String netmask) {
        this.netmask = netmask;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }



    public String getNextHop() {
        return nextHop;
    }

    public void setNextHop(String nextHop) {
        this.nextHop = nextHop;
    }
}
