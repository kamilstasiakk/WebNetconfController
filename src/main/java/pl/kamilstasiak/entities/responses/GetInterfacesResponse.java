package pl.kamilstasiak.entities.responses;




import pl.kamilstasiak.entities.Interface;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kamil on 2017-01-01.
 */
public class GetInterfacesResponse {
    List<Interface> interfaces;

    public GetInterfacesResponse() {
        this.interfaces = new ArrayList<>();
    }

    public List<Interface> getInterfaces() {
        return interfaces;
    }

    public void setInterfaces(List<Interface> interfaces) {
        this.interfaces = interfaces;
    }
    public void addInterface(Interface interfc) {
        interfaces.add(interfc);
    }
}
