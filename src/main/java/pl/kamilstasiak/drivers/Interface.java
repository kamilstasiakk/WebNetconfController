package pl.kamilstasiak.drivers;

/**
 * Created by Kamil on 2016-10-30.
 */

public class Interface {
    IpAddress ip;
    String name;
    String status;
    String protocol;
    String addressSource;
    String macAddress;


    public Interface(String ipAddress, String netMask, String name, String status, String protocol,
                     String addressSource) {
        super();
        if( ipAddress == null) {

            this.ip = new IpAddress("","");
        } else {
            if (netMask == null) {
                this.ip = new IpAddress(ipAddress, "");
            } else {
                this.ip = new IpAddress(ipAddress, netMask);
            }

        }

        this.name = name;
        this.status = status;
        this.protocol = protocol;
        this.addressSource = addressSource;
    }
    public String getIpAddress() {
        return ip.getAddress();
    }
    public void setIpAddress(String ipAddress) {
        this.ip.setAddress(ipAddress);
    }
    public String getNetMask() {
        return ip.getNetmask();
    }
    public void setNetMask(String netMask) {
        if(netMask != null) {
            this.ip.setNetmask(netMask);
        }

    }
    public void setNetMaskAsPrefix(String netMask) {
        if(netMask != null) {
            this.ip.setPrefix(Integer.parseInt(netMask));
        }

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

    public IpAddress getIp() {
        return ip;
    }

    public void setIp(IpAddress ip) {
        this.ip = ip;
    }

    public void basicsUpdate(Interface upToDate) {
        if (this.ip.getAddress() != upToDate.getIpAddress()) {
            this.ip.setAddress(upToDate.getIpAddress());
        }
        if (this.status != upToDate.getStatus()) {
            this.status = upToDate.getStatus();
        }
        if (this.protocol != upToDate.getProtocol()) {
            this.protocol = upToDate.getProtocol();
        }
        if (this.addressSource != upToDate.getAddressSource()) {
            this.addressSource = upToDate.getAddressSource();
        }
    }
    @Override
    public String toString() {
        return "Interface [ipAddress=" + ip.getAddress() + ", netMask=" + ip.getNetmask() + ", name=" + name + ", status=" + status
                + ", protocol=" + protocol + ", addressSource=" + addressSource + ", macAddress=" + macAddress + "]";
    }


}