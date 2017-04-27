package pl.kamilstasiak.drivers;

/**
 * Created by Kamil on 2016-10-30.
 */
import java.io.File;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;


public class NetconfDevice {

    private String hostName;
    private String userName;
    private String password;
    private String helloRpc;
    private String pemKeyFile;
    private boolean connectionOpen;
    private boolean keyBasedAuthentication;
    private Connection NetconfConn;
    private int port;
    private int timeout;
    private NetconfSession defaultSession;

    public NetconfDevice(String hostName, String userName, String password,
                         String pemKeyFile, String hello) throws NetconfException,
            ParserConfigurationException {
        this.hostName = hostName;
        this.userName = userName;
        this.password = password;
        this.pemKeyFile = pemKeyFile;
        if (pemKeyFile == null)
            keyBasedAuthentication = false;
        else
            keyBasedAuthentication = true;
        connectionOpen = false;
        helloRpc = hello;
        port = 22;
        timeout = 5000;
    }


    public NetconfDevice(String hostName, String userName, String password,
                         String pemKeyFile, int port, String hello) throws NetconfException,
            ParserConfigurationException {
        this.hostName = hostName;
        this.userName = userName;
        this.password = password;
        this.pemKeyFile = pemKeyFile;
        if (pemKeyFile == null)
            keyBasedAuthentication = false;
        else
            keyBasedAuthentication = true;
        connectionOpen = false;
        helloRpc = hello;
        this.port = port;
        timeout = 5000;
    }


    public String getHostName() {
        return hostName;
    }


    public void setHostName(String hostName) {
        this.hostName = hostName;
    }


    public String getUserName() {
        return userName;
    }


    public void setUserName(String userName) {
        this.userName = userName;
    }


    public String getPassword() {
        return password;
    }


    public void setPassword(String password) {
        this.password = password;
    }


    public String getHelloRpc() {
        return helloRpc;
    }


    public void setHelloRpc(String helloRpc) {
        this.helloRpc = helloRpc;
    }


    public String getPemKeyFile() {
        return pemKeyFile;
    }


    public void setPemKeyFile(String pemKeyFile) {
        this.pemKeyFile = pemKeyFile;
    }


    public boolean isKeyBasedAuthentication() {
        return keyBasedAuthentication;
    }


    public void setKeyBasedAuthentication(boolean keyBasedAuthentication) {
        this.keyBasedAuthentication = keyBasedAuthentication;
    }


    public int getPort() {
        return port;
    }


    public void setPort(int port) {
        this.port = port;
    }


    public int getTimeout() {
        return timeout;
    }


    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public void connect() throws NetconfException {
        if (hostName == null || userName == null || (password == null &&
                pemKeyFile == null)) {
            throw new NetconfException("Login parameters of Device can't be " +
                    "null.");
        }
        defaultSession = this.createNetconfSession();
    }

    public NetconfSession createNetconfSession() throws NetconfException {
        Session normalSession;
        NetconfSession netconfSess;
        if (!connectionOpen) {
            try {
                NetconfConn = new Connection(hostName, port);
                NetconfConn.connect(null,timeout,0);
            } catch(Exception e) {
                throw new NetconfException(e.getMessage());
            }
            boolean isAuthenticated = true;
            try {
                if (keyBasedAuthentication) {
                    File keyFile = new File(pemKeyFile);
                    isAuthenticated = NetconfConn.authenticateWithPublicKey
                            (userName, keyFile, password);
                } else {
                    isAuthenticated = NetconfConn.authenticateWithPassword
                            (userName, password);
                }
            } catch (IOException e) {
                throw new NetconfException("Authentication failed:" +
                        e.getMessage());
            }
            if (!isAuthenticated)
                throw new NetconfException("Authentication failed.");
            connectionOpen = true;
        }
        try {
            normalSession = NetconfConn.openSession();
            normalSession.startSubSystem("netconf");
            netconfSess = new NetconfSession(normalSession, helloRpc);
        } catch (IOException e) {
            throw new NetconfException("Failed to create Netconf session:" +
                    e.getMessage());
        }
        return netconfSess;
    }

    public String getSessionId() {
        if (defaultSession == null) {
            throw new IllegalStateException("Cannot get session ID, you need " +
                    "to establish a connection first.");
        }
        return this.defaultSession.getSessionId();
    }

    public String executeRPC(String rpcContent) throws SAXException, IOException {
        if (defaultSession == null) {
            this.connect();
            /*throw new IllegalStateException("Cannot execute RPC, you need to " +
                    "establish a connection first.");*/
        }
        return this.defaultSession.executeRPC(rpcContent);
    }

    public void close() {
        if (!connectionOpen) {
            return;
        }
        NetconfConn.close();
        connectionOpen = false;
    }



}