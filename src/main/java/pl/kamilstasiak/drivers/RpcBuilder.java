package pl.kamilstasiak.drivers;

import java.util.List;

/**
 * Created by Kamil on 2016-10-30.
 */
public class RpcBuilder {

    public String buildHelloMessage(String[] capabilities) {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        sb.append("<hello xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\">\n");
        sb.append("<capabilities>\n");
        for ( String capability : capabilities) {
            sb.append("<capability>");
            sb.append(capability);
            sb.append("</capability>\n");
        }
        sb.append("</capabilities>\n");
        sb.append("</hello>\n");
        sb.append("]]>]]>");
        return sb.toString();
    }

    public String buildGetXmlMessage(String command) {
        StringBuilder message = new StringBuilder();
        message.append("<?xml version=\"1.0\" encoding=\\\"UTF-8\\\"?>\n");
        message.append("<rpc message-id=\"101\" xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\">\n");
        message.append("<get>");
        message.append("<filter>");
        message.append("<oper-data-format-xml>");
        message.append("<show>");
        message.append(command);
        message.append("</show>");
        message.append("</oper-data-format-xml>");
        message.append("</filter>");
        message.append("</get>");
        message.append("</rpc>]]>]]>");
        return message.toString();
    }


    public String buildGetCmdMessage(String[] commands) {
        StringBuilder message = new StringBuilder();
        message.append("<?xml version=\"1.0\" encoding=\\\"UTF-8\\\"?>\n");
        message.append("<rpc message-id=\"4\" xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\">");
        message.append("<get>");
        message.append("<filter>");
        message.append("<oper-data-format-text-block>");
        for( String command : commands) {
            message.append("<exec>");
            message.append(command);
            message.append("</exec>");
        }
        message.append("</oper-data-format-text-block>");
        message.append("</filter>");
        message.append("</get>");
        message.append("</rpc>]]>]]>");
        return message.toString();
    }

    public String buildSetCmdMessage(List<String> commands) {
        StringBuilder message = new StringBuilder();
        message.append("<?xml version=\"1.0\" encoding=\\\"UTF-8\\\"?>\n");
        message.append("<rpc message-id=\"4\" xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\">");
        message.append("<edit-config>");
        message.append("<target>");
        message.append("<running/>");
        message.append("</target>");
        message.append("<config>");
        message.append("<cli-config-data>");
        for (String command : commands) {
            message.append("<cmd>");
            message.append(command);
            message.append("</cmd>");
        }
        message.append("</cli-config-data>");
        message.append("</config>");
        message.append("</edit-config>");
        message.append("</rpc>]]>]]>");

        return message.toString();
    }
}
