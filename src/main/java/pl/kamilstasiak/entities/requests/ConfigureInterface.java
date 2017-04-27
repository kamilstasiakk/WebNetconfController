package pl.kamilstasiak.entities.requests;

/**
 * Created by Kamil on 2017-01-01.
 */
public class ConfigureInterface {
    String ipAddress;
    String name;
    String interfaceIpAddress;
    String netmask;
    String status;

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
