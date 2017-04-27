package pl.kamilstasiak.drivers;

/**
 * Created by Kamil on 2016-10-30.
 */

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class RpcParser {
    public List<Interface> parseInterfaces(String reply) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {

        String name, address, status, protocol, method;
        int indexOfEndMark = reply.lastIndexOf("]]>]]>");
        if( indexOfEndMark != -1) {
            reply = reply.substring(0, indexOfEndMark);
        }

        List <Interface> interfaces = new ArrayList<Interface>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        ByteArrayInputStream input =  new ByteArrayInputStream(
                reply.getBytes("UTF-8"));
        Document doc = builder.parse(input);
        doc.getDocumentElement().normalize();

        XPath xPath =  XPathFactory.newInstance().newXPath();
        String expression = "/rpc-reply/data/xml-oper-data/item/response/ShowIpInterfaceBrief/IPInterfaces/entry";
        NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(doc, XPathConstants.NODESET);
        Element element;
        Node node;
        for(int i=0; i<nodeList.getLength(); i++) {
            node = nodeList.item(i);
            System.out.println("xpath result number " + i + ": " + node.getNodeName());
            if( node.getNodeType() == Node.ELEMENT_NODE) {
                element = (Element) node;
                name = null;
                address = null;
                status = null;
                protocol = null;
                method = null;

                if (element.getElementsByTagName("Interface").item(0) != null) {
                    name = element.getElementsByTagName("Interface").item(0).getTextContent();

                    if (element.getElementsByTagName("IP-Address").item(0) != null) {
                        address = element.getElementsByTagName("IP-Address").item(0).getTextContent();
                    }

                    if (element.getElementsByTagName("Status").item(0) != null) {
                        status = element.getElementsByTagName("Status").item(0).getTextContent();
                    }

                    if (element.getElementsByTagName("Method").item(0) != null) {
                        method = element.getElementsByTagName("Method").item(0).getTextContent();
                    }

                    if (element.getElementsByTagName("Protocol").item(0) != null) {
                        protocol = element.getElementsByTagName("Protocol").item(0).getTextContent();
                    }


                    interfaces.add(new Interface(address, null, name, status, protocol, method));
                }


            }
        }

        return interfaces;
    }

    public String[] parseMacAndMask(String reply) {
        String[] pair = new String[2];
        String macLine = reply.split(" \\(bia")[0];
        String ipLine = reply.split(" \\(bia")[1];
        macLine= macLine.split("address is ")[1];
        pair[0] = macLine;
        String[] ipLines = ipLine.split("Internet address is ");
        if (ipLines.length > 1) {
            pair[1] = ipLines[1].split("/")[1].substring(0, 2);
        }



        return pair;
    }

    public String parseHostname(String reply) {

        String hostname = reply.split("hostname")[1].split("!")[0].trim();
        return hostname;
    }

    public List<RouteTableRecord> parseRouteTable(String reply) {
        List<RouteTableRecord> routeTable = new ArrayList<>();
        if (reply.split("<response>").length < 2) {
            return routeTable;
        }
        String[] rows = reply.split("<response>")[1].split("ip route ");
        for (String row : rows) {
            String[] fields = row.split(" ");
            if(fields.length < 3) {
                continue;
            }
            if (fields[2].contains("</response")) {
                fields[2] = fields[2].split("</response")[0];
            }

            IpAddress ip = new IpAddress(fields[0], fields[1]);
            routeTable.add(new RouteTableRecord(fields[0] + "/" + ip.getPrefix(), fields[2].trim()));
        }
        return routeTable;
    }
}