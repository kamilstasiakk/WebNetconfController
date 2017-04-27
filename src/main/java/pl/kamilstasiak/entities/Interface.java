package pl.kamilstasiak.entities;

/**
 * Created by Kamil on 2017-01-01.
 */
public class Interface {
    String ipAddress;
    String netmask;
    String name;
    String status;
    String protocol;
    String addressSource;
    String macAddress;

    public Interface(String ipAddress, String netmask, String name, String status, String protocol, String addressSource, String macAddress) {
        this.ipAddress = ipAddress;
        this.netmask = netmask;
        this.name = name;
        this.status = status;
        this.protocol = protocol;
        this.addressSource = addressSource;
        this.macAddress = macAddress;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getNetmask() {
        return netmask;
    }

    public void setNetmask(String netmask) {
        this.netmask = netmask;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getAddressSource() {
        return addressSource;
    }

    public void setAddressSource(String addressSource) {
        this.addressSource = addressSource;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }
}
