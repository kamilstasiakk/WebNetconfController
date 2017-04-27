package pl.kamilstasiak.drivers;

/**
 * Created by Kamil on 2016-10-30.
 */
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.xml.sax.SAXException;

public class CiscoRouter extends NetconfDevice implements NetconfRouterInterface {

    private RpcBuilder rpcBuilder;
    private List<Interface> interfaces;
    private List<String> commandsToPerform;

    public CiscoRouter(String hostName, String userName, String password,
                       String pemKeyFile, int port ) throws NetconfException, ParserConfigurationException {
        super(hostName, userName, password, pemKeyFile, port, getHelloMessage());
        rpcBuilder = new RpcBuilder();
        commandsToPerform = null;
    }

    public CiscoRouter(String hostName, String userName, String password,
                       String pemKeyFile ) throws ParserConfigurationException, NetconfException {
        super(hostName, userName, password, pemKeyFile, getHelloMessage());
        rpcBuilder = new RpcBuilder();
        commandsToPerform = null;
    }

    public List<Interface> getInterfaces() {
        return interfaces;
    }

    private static String getHelloMessage() {
        RpcBuilder rpcBuilder = new RpcBuilder();
        return rpcBuilder.buildHelloMessage(getDefaultCapabilities());
    }

    private static String[] getDefaultCapabilities() {
        String[] defaultCapa = new String [4];
        defaultCapa[0] = "urn:ietf:params:netconf:base:1.0";
        defaultCapa[1] = "urn:ietf:params:netconf:capability:url:1.0";
        defaultCapa[2] = "urn:cisco:params:netconf:capability:pi-data-model:1.0";
        defaultCapa[3] = "urn:cisco:params:netconf:capability:notification:1.0";
        return defaultCapa;
    }

    @Override
    public String toString() {
        return "CiscoRouter [interfaces=" + interfaces + "]";
    }

    @Override
    public void updateInterfaceInformation() {
        List<Interface> interfacesFromReply = null;
        try {
            String reply = executeRPC(rpcBuilder.buildGetXmlMessage("ip interface brief"));
            RpcParser rpcParser = new RpcParser();
            interfacesFromReply = rpcParser.parseInterfaces(reply);
        } catch (SAXException | IOException | ParserConfigurationException | XPathExpressionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if(interfacesFromReply != null) {
            for( Interface oneInterface : interfacesFromReply) {
                String[] commands = new String[1];
                commands[0] = "show interfaces " + oneInterface.getName();
                String reply2 = null;
                try {
                    reply2 = executeRPC(rpcBuilder.buildGetCmdMessage(commands));
                } catch (SAXException | IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                RpcParser rpcParser = new RpcParser();
                if ( reply2 != null) {
                    String[] macAndMask = rpcParser.parseMacAndMask(reply2);
                    oneInterface.setNetMaskAsPrefix(macAndMask[1]);
                    oneInterface.setMacAddress(macAndMask[0]);
                }

            }

            interfaces = new ArrayList<Interface>(interfacesFromReply);

        }

    }


    @Override
    public void setInterfaceIpAddress(String interfaceName, String ipAddress, String netmask) {
        for (Interface someInterface : interfaces) {
            if (someInterface.getName().equals(interfaceName) ) {
                if (!someInterface.getIpAddress().equals(ipAddress)  || !someInterface.getNetMask().equals(netmask) ) {
                    addCommandToPerform("interface " + interfaceName);
                    addCommandToPerform("ip address " + ipAddress + " " + netmask);
                }
                break;
            }
        }

    }

    @Override
    public void setInterfaceMacAddress(String interfaceName, String macAddress) {
        for (Interface someInterface : interfaces) {
            if (someInterface.getName().equals(interfaceName) ) {
                if (!someInterface.getMacAddress().equals(macAddress)) {
                    addCommandToPerform("interface " + interfaceName);
                    addCommandToPerform("mac-address " + macAddress );
                }
                break;
            }
        }

    }

    @Override
    public void setInterfaceStatus(String interfaceName, String status) {
        for (Interface someInterface : interfaces) {
            if (someInterface.getName().equals(interfaceName) ) {
                if (!someInterface.getProtocol().equals(status) ) {
                    addCommandToPerform("interface " + interfaceName);
                    if (status.equals("down")) {
                        addCommandToPerform("shutdown" );
                    } else {
                        addCommandToPerform("no shutdown" );
                    }
                }
                break;
            }
        }

    }

    @Override
    public void clearInterfaceAddress(String interfaceName, String ipAddress, String netmask) {
        for (Interface someInterface : interfaces) {
            if (someInterface.getName().equals(interfaceName) ) {
                if (someInterface.getIpAddress().equals(ipAddress)  || someInterface.getNetMask().equals(netmask) ) {
                    addCommandToPerform("interface " + interfaceName);
                    addCommandToPerform("no ip address " + ipAddress + " " + netmask);
                }
                break;
            }
        }
    }

    @Override
    public boolean changeRouterConfiguration() {
        try {
            String reply = executeRPC(rpcBuilder.buildSetCmdMessage(commandsToPerform));
            if (reply.contains("<ok />")) {
                commandsToPerform = null;
                return true;
            } else {
                return false;
            }
        } catch (SAXException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }

    }

    private void addCommandToPerform(String command) {
        if (commandsToPerform == null) {
            commandsToPerform = new ArrayList<String>();
        }
        commandsToPerform.add(command);
    }

    @Override
    public void setStaticRoutingEntryViaInterface(IpAddress ip, String outPortName) {
        addCommandToPerform("ip route " + ip.getAddress() + " " + ip.getNetmask() + " " + outPortName);
    }

    @Override
    public void setStaticRoutingEntryViaInterfaceIp(IpAddress ip, String ipAddress) {
        addCommandToPerform("ip route " + ip.getAddress() + " " + ip.getNetmask() + " " + ipAddress);
    }

    @Override
    public void deleteStaticRoutingEntryViaInterface(IpAddress ip, String outPortName) {
        addCommandToPerform("no ip route " + ip.getAddress() + " " + ip.getNetmask() + " " + outPortName);
    }

    @Override
    public void deleteStaticRoutingEntryViaInterfaceIp(IpAddress ip, String ipAddress) {
        addCommandToPerform("no ip route " + ip.getAddress() + " " + ip.getNetmask() + " " + ipAddress);
    }

    @Override
    public void setRouterHostname(String hostName) {
        addCommandToPerform("hostname " + hostName);
    }

    @Override
    public String getRouterHostname() {
        try {
            String reply = executeRPC(rpcBuilder.buildGetCmdMessage(new String[] {"show running-config | include hostname"}));
            RpcParser rpcParser = new RpcParser();
            String hostname = rpcParser.parseHostname(reply);
            return hostname;
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "pusto";
    }

    public List<RouteTableRecord> getRouteTable() {
        try {
            String reply = executeRPC(rpcBuilder.buildGetCmdMessage(new String[] {"show running-config | include ip route"}));
            RpcParser rpcParser = new RpcParser();
            List<RouteTableRecord> routeTable = rpcParser.parseRouteTable(reply);
            String reply2 = executeRPC(rpcBuilder.buildGetCmdMessage(new String[] {"show ip route"}));
            String ipRouteResult = reply2.split("Codes: L - local, C - connected")[1];
            for (int recordNumber =0; recordNumber < routeTable.size(); recordNumber++) {
                RouteTableRecord routeTableRecord = routeTable.get(recordNumber);
                if (!ipRouteResult.contains(routeTableRecord.getPrefix().split("/")[0])) {
                    routeTable.remove(routeTableRecord);
                }
            }
            return routeTable;
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Interface getInterface(String name) {
        for (Interface interfc : interfaces) {
            if (interfc.getName().equals(name)) {
                return interfc;
            }
        }
        return null;
    }
}