package pl.kamilstasiak.drivers;

/**
 * Created by Kamil on 2016-10-30.
 */

public class IpAddress {
    private String address;
    private String netmask;

    public IpAddress(String address, String netmask) {
        super();
        this.address = address;
        this.netmask = netmask;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNetmask() {
        return netmask;
    }

    public void setNetmask(String netmask) {
        this.netmask = netmask;
    }

    public int getPrefix() {
        return convertNetmaskToPrefix();
    }
    public void setPrefix(int prefix) {
        this.netmask = convertPrefixToNetmask(prefix);
    }

    public String getSubnetAddress() {
        String[] netmaskOctets = netmask.split("\\.");
        String[] addressOctets = address.split("\\.");
        int[] netmaskOctetsValue = new int[netmaskOctets.length];
        int[] addressOctetsValue = new int[addressOctets.length];
        StringBuilder subnet = new StringBuilder();
        for( int i=0; i < addressOctets.length; i++) {
            addressOctetsValue[i] = Integer.parseInt(addressOctets[i]);
            netmaskOctetsValue[i] = Integer.parseInt(netmaskOctets[i]);
            subnet.append((addressOctetsValue[i] & netmaskOctetsValue[i]));
            if(i <3) {
                subnet.append(".");
            }
        }
        return subnet.toString();
    }

    public String getBroadcastAddress() {
        String[] netmaskOctets = netmask.split("\\.");
        String[] addressOctets = address.split("\\.");
        int[] netmaskOctetsValue = new int[netmaskOctets.length];
        int[] addressOctetsValue = new int[addressOctets.length];
        StringBuilder subnet = new StringBuilder();
        int a = 0b01111111111111111111111111111111 >> convertNetmaskToPrefix();
        int b = 0b1<<(31-convertNetmaskToPrefix());
        int maxHost = a + b;
        int maxHostOctets[] = new int[addressOctets.length];
        maxHostOctets[0] = ((maxHost & 0b11111111000000000000000000000000) >> 24);
        maxHostOctets[1] = (maxHost & 0b00000000111111110000000000000000) >> 16;
        maxHostOctets[2] = (maxHost & 0b00000000000000001111111100000000) >> 8;
        maxHostOctets[3] = maxHost & 0b00000000000000000000000011111111;
        for( int i=0; i < addressOctets.length; i++) {
            addressOctetsValue[i] = Integer.parseInt(addressOctets[i]);
            netmaskOctetsValue[i] = Integer.parseInt(netmaskOctets[i]);
            subnet.append((addressOctetsValue[i] & netmaskOctetsValue[i]) +
                    maxHostOctets[i] );
            if(i <3) {
                subnet.append(".");
            }
        }
        return subnet.toString();
    }

    public static IpAddress parseIpAddress(String text) {
        String ip = text.split("/")[0];
        String prefix = text.split("/")[1];
        IpAddress result = new IpAddress(ip, null);
        result.setPrefix( Integer.parseInt(prefix));
        return result;
    }

    private int convertNetmaskToPrefix() {
        int prefix = 0;
        String[] octets = netmask.split("\\.");
        int[] octetsValue = new int[octets.length];
        for( int i=0; i < octets.length; i++) {
            octetsValue[i] = Integer.parseInt(octets[i]);
            int j=0;
            for(j = 0; j <8; j++ ) {
                if(getNthBit(octetsValue[i],j) == 1) {
                    prefix++;
                }
                else {
                    break;
                }
            }
            if (j != 8) {
                break;
            }
        }
        return prefix;
    }

    private int getNthBit(int number, int possition) {
        return ((number << possition) & 128) == 0 ? 0 : 1;
    }

    private String convertPrefixToNetmask(int prefix) {

        Integer num = 0b11111111111111111111111111111111;
        num = num << (32-prefix);
        Integer[] octets = new Integer[4];
        octets[0] = 256 + ((num & 0b11111111000000000000000000000000) >> 24);
        octets[1] = (num & 0b00000000111111110000000000000000) >> 16;
        octets[2] = (num & 0b00000000000000001111111100000000) >> 8;
        octets[3] = num & 0b00000000000000000000000011111111;
        StringBuilder sb =new StringBuilder();
        for(int k = 0; k < 4; k++) {
            sb.append(octets[k].toString());
            if(k != 3) {
                sb.append(".");
            }
        }
        return sb.toString();
    }


}
