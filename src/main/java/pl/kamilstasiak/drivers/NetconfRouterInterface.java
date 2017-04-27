package pl.kamilstasiak.drivers;

/**
 * Created by Kamil on 2016-10-30.
 */
public interface NetconfRouterInterface {
    public void updateInterfaceInformation();
    public void setInterfaceIpAddress(String interfaceName, String ipAddress, String netmask);
    public void setInterfaceMacAddress(String interfaceName, String macAddress);
    public void setInterfaceStatus(String interfaceName, String status);
    public void clearInterfaceAddress(String interfaceName, String ipAddress, String netmask);
    public void setStaticRoutingEntryViaInterface(IpAddress ip, String outPortName);
    public void setStaticRoutingEntryViaInterfaceIp(IpAddress ip, String ipAddress);
    public void deleteStaticRoutingEntryViaInterface(IpAddress ip, String outPortName);
    public void deleteStaticRoutingEntryViaInterfaceIp(IpAddress ip, String ipAddress);
    public void setRouterHostname(String hostName);
    public String getRouterHostname();
    public boolean changeRouterConfiguration();
}
