package pl.kamilstasiak.entities;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Created by Kamil on 2016-10-29.
 */
@Entity
@Table(name = "devices")
public class Device {

    @Id
    @NotNull
    private String ipAddress;

    @NotNull
    private String name;

    @NotNull
    private int portNumber;

    @NotNull
    private String userName;

    @NotNull
    private String password;

    public Device() {
    }

    public Device(String ipAddress, String name, int portNumber, String userName, String password) {
        this.ipAddress = ipAddress;
        this.name = name;
        this.portNumber = portNumber;
        this.userName = userName;
        this.password = password;
    }

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

    public int getPortNumber() {
        return portNumber;
    }

    public void setPortNumber(int portNumber) {
        this.portNumber = portNumber;
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
}

