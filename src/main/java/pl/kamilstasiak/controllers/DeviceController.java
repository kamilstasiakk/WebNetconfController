package pl.kamilstasiak.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import pl.kamilstasiak.drivers.CiscoRouter;
import pl.kamilstasiak.drivers.IpAddress;
import pl.kamilstasiak.drivers.NetconfException;
import pl.kamilstasiak.entities.Device;
import pl.kamilstasiak.entities.Interface;
import pl.kamilstasiak.entities.RouteTableRecord;
import pl.kamilstasiak.entities.requests.*;
import pl.kamilstasiak.entities.responses.GetHostnameResponse;
import pl.kamilstasiak.entities.responses.GetInterfacesResponse;
import pl.kamilstasiak.entities.responses.GetRouteTableResponse;
import pl.kamilstasiak.repositories.DeviceRepository;

import javax.xml.parsers.ParserConfigurationException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Created by Kamil on 2016-10-29.
 */
@Controller
@RequestMapping("devices")
public class DeviceController {

    private DeviceRepository deviceRepository;
    private List<CiscoRouter> routersList = null;

    @Autowired
    public DeviceController(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
        this.routersList = new ArrayList<CiscoRouter>();
        for (Device d : deviceRepository.findAll()) {
            try {
                routersList.add(new CiscoRouter(d.getIpAddress(),d.getUserName(),d.getPassword(),null,d.getPortNumber()));
            } catch (NetconfException | ParserConfigurationException e) {
                e.printStackTrace();
            }
        }
    }

    //Managing device repository
   @RequestMapping(value = "/add", method = RequestMethod.POST)
    public ResponseEntity<String> addNewDevice(@RequestBody AddDeviceRequest addDeviceRequest) {
        Device device = new Device(addDeviceRequest.getIpAddress(),addDeviceRequest.getName(),addDeviceRequest.getPortNumber(),addDeviceRequest.getUserName(),addDeviceRequest.getPassword());
        deviceRepository.save(device);

       if(routersList == null) {
           routersList = new ArrayList<CiscoRouter>();
       }
       try {
           routersList.add(new CiscoRouter(device.getIpAddress(),device.getUserName(),device.getPassword(),null,device.getPortNumber()));
       } catch (NetconfException | ParserConfigurationException e) {
           e.printStackTrace();
       }
       return new ResponseEntity<String>("Device added", HttpStatus.CREATED);
   }
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public ResponseEntity<String> deleteDevice(@RequestBody DeleteDeviceRequest deleteDeviceRequest) {
        Device device = new Device(deleteDeviceRequest.getIpAddress(),deleteDeviceRequest.getName(),deleteDeviceRequest.getPortNumber(),deleteDeviceRequest.getUserName(),deleteDeviceRequest.getPassword());
        deviceRepository.delete(device);

        Predicate<CiscoRouter> routerPredicate = r-> r.getHostName().equals(device.getIpAddress()) ;
        routersList.removeIf(routerPredicate);

        return new ResponseEntity<String>("Device deleted", HttpStatus.OK);
    }

    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public ResponseEntity<String> editDevice(@RequestBody EditDeviceRequest editDeviceRequest) {
        Device oldDevice = new Device(editDeviceRequest.getIpAddress(),editDeviceRequest.getName(),editDeviceRequest.getPortNumber(),editDeviceRequest.getUserName(),editDeviceRequest.getPassword());
        deviceRepository.delete(oldDevice);

        Device newDevice = new Device(editDeviceRequest.getIpAddress(),editDeviceRequest.getNewName(),editDeviceRequest.getNewPortNumber(),editDeviceRequest.getNewUserName(),editDeviceRequest.getNewPassword());
        deviceRepository.save(newDevice);

        for (int routerIndex = 0; routerIndex < routersList.size(); routerIndex++) {
            if (routersList.get(routerIndex).getHostName().equals(newDevice.getIpAddress())) {
                routersList.get(routerIndex).setUserName(newDevice.getUserName());
                routersList.get(routerIndex).setPassword(newDevice.getPassword());
                routersList.get(routerIndex).setPort(newDevice.getPortNumber());
                break;
            }
        }

        return new ResponseEntity<String> ("Device eddited", HttpStatus.OK);

    }
    @RequestMapping(value = "/all", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody List<Device> getDevicesInJSON() {

        return deviceRepository.findAll();

    }

    //device configuration

    @RequestMapping(value = "/configure/hostname", method = RequestMethod.POST)
    public ResponseEntity<String> configureHostname(@RequestBody ConfigureHostnameRequest configureHostnameRequest) {
        String ip = configureHostnameRequest.getIpAddress();
        for (int routerIndex = 0; routerIndex < routersList.size(); routerIndex++) {
            if (routersList.get(routerIndex).getHostName().equals(ip)) {
                routersList.get(routerIndex).setRouterHostname(configureHostnameRequest.getNewHostname());
                //TODO - usunac to po dodaniu apply

                routersList.get(routerIndex).changeRouterConfiguration();
                return new ResponseEntity<String>("Hostname changed", HttpStatus.OK);
            }
        }
        return new ResponseEntity<String>("no such device", HttpStatus.NOT_FOUND);
    }

    @RequestMapping(value = "/configuration/hostname/{ip}", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    GetHostnameResponse getHostname(@PathVariable String ip) {
        GetHostnameResponse response = new GetHostnameResponse();
        for (CiscoRouter ciscoRouter : routersList) {
            if(ciscoRouter.getHostName().equals(ip)) {
                response.setHostname(ciscoRouter.getRouterHostname());
            }
        }
        return response;
    }

    @RequestMapping(value = "/configuration/interfaces/{ip}", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    GetInterfacesResponse getInterfaces(@PathVariable String ip) {
        GetInterfacesResponse response = new GetInterfacesResponse();
        for (CiscoRouter ciscoRouter : routersList) {
            if(ciscoRouter.getHostName().equals(ip)) {
                ciscoRouter.updateInterfaceInformation();
                for (pl.kamilstasiak.drivers.Interface interfc : ciscoRouter.getInterfaces()) {
                    response.addInterface(new Interface(interfc.getIpAddress(), interfc.getNetMask(),
                            interfc.getName(), interfc.getStatus(), interfc.getProtocol(),
                            interfc.getAddressSource(), interfc.getMacAddress()));
                }

            }
        }
        return response;
    }

    @RequestMapping(value = "/configuration/routeTable/{ip}", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    GetRouteTableResponse getRouteTable(@PathVariable String ip) {
        GetRouteTableResponse response = new GetRouteTableResponse();
        for (CiscoRouter ciscoRouter : routersList) {
            if(ciscoRouter.getHostName().equals(ip)) {
                for (pl.kamilstasiak.drivers.RouteTableRecord record : ciscoRouter.getRouteTable()) {
                    response.addRecord(new RouteTableRecord(record.getPrefix(), record.getNextHop()));
                }
            }
        }
        return response;
    }

    @RequestMapping(value = "/configure/addRoute", method = RequestMethod.POST)
    public ResponseEntity<String> addRouteRecord(@RequestBody AddRouteRecordRequest addRouteRecordRequest) {
        String ip = addRouteRecordRequest.getIpAddress();
        for (int routerIndex = 0; routerIndex < routersList.size(); routerIndex++) {
            if (routersList.get(routerIndex).getHostName().equals(ip)) {
                routersList.get(routerIndex).setStaticRoutingEntryViaInterface(
                        new IpAddress(addRouteRecordRequest.getDestNetworkAddress(),addRouteRecordRequest.getNetmask()),
                        addRouteRecordRequest.getNextHop());
                //TODO - usunac to po dodaniu apply

                routersList.get(routerIndex).changeRouterConfiguration();
                return new ResponseEntity<String>("Route added", HttpStatus.OK);
            }
        }
        return new ResponseEntity<String>("no such device", HttpStatus.NOT_FOUND);
    }

    @RequestMapping(value = "/configure/deleteRoute", method = RequestMethod.POST)
    public ResponseEntity<String> deleteRouteRecord(@RequestBody DeleteRouteRecordRequest deleteRouteRecordRequest) {
        String ip = deleteRouteRecordRequest.getIpAddress();
        for (int routerIndex = 0; routerIndex < routersList.size(); routerIndex++) {
            if (routersList.get(routerIndex).getHostName().equals(ip)) {
                routersList.get(routerIndex).deleteStaticRoutingEntryViaInterface(
                        IpAddress.parseIpAddress(deleteRouteRecordRequest.getPrefix()),
                        deleteRouteRecordRequest.getOutgoingInterface());

                routersList.get(routerIndex).changeRouterConfiguration();
                return new ResponseEntity<String>("Route deleted", HttpStatus.OK);
            }
        }
        return new ResponseEntity<String>("no such device", HttpStatus.NOT_FOUND);
    }

    @RequestMapping(value = "/configure/interface", method = RequestMethod.POST)
    public ResponseEntity<String> configureInterface(@RequestBody ConfigureInterface configureInterface) {
        String ip = configureInterface.getIpAddress();
        for (int routerIndex = 0; routerIndex < routersList.size(); routerIndex++) {
            if (routersList.get(routerIndex).getHostName().equals(ip)) {
                // zmiana ip i/lub shutdown
                routersList.get(routerIndex).updateInterfaceInformation();
                routersList.get(routerIndex).setInterfaceIpAddress(
                        configureInterface.getName(), configureInterface.getInterfaceIpAddress(),
                        configureInterface.getNetmask()
                );
                routersList.get(routerIndex).setInterfaceStatus(
                        configureInterface.getName(),configureInterface.getStatus());
                routersList.get(routerIndex).changeRouterConfiguration();
                return new ResponseEntity<String>("Success", HttpStatus.OK);
            }
        }
        return new ResponseEntity<String>("no such device", HttpStatus.NOT_FOUND);
    }

    @RequestMapping(value = "/configure/clearInterfaceAddress", method = RequestMethod.POST)
    public ResponseEntity<String> clearInterfaceAddress(@RequestBody ClearInterfaceIpAddressRequest clearInterfaceIpAddressRequest) {
        String ip = clearInterfaceIpAddressRequest.getIpAddress();
        for (int routerIndex = 0; routerIndex < routersList.size(); routerIndex++) {
            if (routersList.get(routerIndex).getHostName().equals(ip)) {

                routersList.get(routerIndex).clearInterfaceAddress(
                        clearInterfaceIpAddressRequest.getName(),
                        clearInterfaceIpAddressRequest.getInterfaceIpAddress(),
                        clearInterfaceIpAddressRequest.getNetmask()
                );
                routersList.get(routerIndex).setInterfaceStatus(
                        clearInterfaceIpAddressRequest.getName(),"down");
                routersList.get(routerIndex).changeRouterConfiguration();
                return new ResponseEntity<String>("Success", HttpStatus.OK);
            }
        }
        return new ResponseEntity<String>("no such device", HttpStatus.NOT_FOUND);
    }

}
