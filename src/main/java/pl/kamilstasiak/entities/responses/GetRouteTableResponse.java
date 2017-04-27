package pl.kamilstasiak.entities.responses;

import pl.kamilstasiak.entities.RouteTableRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kamil on 2017-01-01.
 */
public class GetRouteTableResponse {
    List<RouteTableRecord> routeTable;

    public GetRouteTableResponse() {
        routeTable = new ArrayList<>();
    }

    public List<RouteTableRecord> getRouteTable() {
        return routeTable;
    }

    public void setRouteTable(List<RouteTableRecord> routeTable) {
        this.routeTable = routeTable;
    }

    public void addRecord(RouteTableRecord record) {
        this.routeTable.add(record);
    }
}
