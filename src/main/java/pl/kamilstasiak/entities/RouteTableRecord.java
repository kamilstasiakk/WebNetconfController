package pl.kamilstasiak.entities;

/**
 * Created by Kamil on 2017-01-01.
 */
public class RouteTableRecord {
    String prefix;
    String nextHop;

    public RouteTableRecord(String prefix, String nextHop) {
        this.prefix = prefix;
        this.nextHop = nextHop;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getNextHop() {
        return nextHop;
    }

    public void setNextHop(String nextHop) {
        this.nextHop = nextHop;
    }
}
