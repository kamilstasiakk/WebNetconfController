package pl.kamilstasiak.drivers;

/**
 * Created by Kamil on 2016-10-30.
 */
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.xml.parsers.DocumentBuilder;
import org.xml.sax.SAXException;


public class NetconfSession {

    private Session netconfSession;
    private String serverCapability;
    private InputStream stdout;
    private BufferedReader bufferReader;
    private String lastRpcReply;
    private DocumentBuilder builder;

    protected NetconfSession(Session netconfSession, String hello ) throws NetconfException, IOException {

        this.netconfSession = netconfSession;
        stdout = new StreamGobbler(netconfSession.getStdout());
        bufferReader = new BufferedReader(new InputStreamReader(stdout));
        sendHello(hello);
    }



    private void sendHello(String hello) throws IOException {
        String reply = getRpcReply(hello);
        serverCapability = reply;
        lastRpcReply = reply;
    }

    private String getRpcReply(String rpc) throws IOException {
        byte b[]= rpc.getBytes();
        netconfSession.getStdin().write(b);
        String rpcReply = "";
        while (true) {
            String line = "";
            line = bufferReader.readLine();
            if (line == null || line.contains("]]>]]>")) {
                rpcReply += line + "\n";
                break;
            }
            rpcReply += line + "\n";
        }
        return rpcReply;
    }

    private BufferedReader getRpcReplyRunning(String rpc) throws IOException {
        byte b[]= rpc.getBytes();
        netconfSession.getStdin().write(b);
        return bufferReader;
    }



    /**
     * Get capability of the Netconf server.
     * @return server capability
     */
    public String getServerCapability() {
        return serverCapability;
    }

    /**
     * Send an RPC(as String object) over the default Netconf session and get
     * the response as an XML object.
     * <p>
     * @param rpcContent
     *          RPC content to be sent. For example, to send an rpc
     *          &lt;rpc&gt;&lt;get-chassis-inventory/&gt;&lt;/rpc&gt;, the
     *          String to be passed can be
     *                 "&lt;get-chassis-inventory/&gt;" OR
     *                 "get-chassis-inventory" OR
     *                 "&lt;rpc&gt;&lt;get-chassis-inventory/&gt;&lt;/rpc&gt;"
     * @return RPC reply sent by Netconf server
     * @throws org.xml.sax.SAXException
     * @throws java.io.IOException
     */
    public String executeRPC(String rpcContent) throws SAXException, IOException {
        if (rpcContent == null) {
            throw new IllegalArgumentException("Null RPC");
        }
        rpcContent = rpcContent.trim();
        String rpcReply = getRpcReply(rpcContent);
        lastRpcReply = rpcReply;
        return rpcReply;
    }




    /**
     * Get the session ID of the Netconf session.
     * @return Session ID as a string.
     */
    public String getSessionId() {
        String split[] = serverCapability.split("<session-id>");
        if (split.length != 2)
            return null;
        String idSplit[] = split[1].split("</session-id>");
        if (idSplit.length != 2)
            return null;
        return idSplit[0];
    }

    /**
     * Close the Netconf session. You should always call this once you don't
     * need the session anymore.
     */
    public void close() throws IOException {
        StringBuffer rpc = new StringBuffer("");
        rpc.append("<rpc>");
        rpc.append("<close-session/>");
        rpc.append("</rpc>");
        rpc.append("]]>]]>");
        String rpcReply = getRpcReply(rpc.toString());
        lastRpcReply = rpcReply;
        netconfSession.close();
    }




    /**
     * Returns the last RPC reply sent by Netconf server.
     * @return Last RPC reply, as a string.
     */
    public String getLastRPCReply() {
        return this.lastRpcReply;
    }

}
