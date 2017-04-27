package pl.kamilstasiak.entities.requests;

/**
 * Created by Kamil on 2017-01-06.
 */
public class ClearInterfaceIpAddressRequest {
    String ipAddress;
    String name;
    String interfaceIpAddress;
    String netmask;

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInterfaceIpAddress() {
        return interfaceIpAddress;
    }

    public void setInterfaceIpAddress(String interfaceIpAddress) {
        this.interfaceIpAddress = interfaceIpAddress;
    }

    public String getNetmask() {
        return netmask;
    }

    public void setNetmask(String netmask) {
        this.netmask = netmask;
    }
}
