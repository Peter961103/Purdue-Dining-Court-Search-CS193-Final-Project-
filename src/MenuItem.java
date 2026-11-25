public class MenuItem {
    private final String diningCourt;
    private final String meal;
    private final String station;
    private final String name;

    public MenuItem (String diningCourt, String meal, String station, String name) {
        this.diningCourt = diningCourt;
        this.meal = meal;
        this.station = station;
        this.name = name;
    }

    public String getDiningCourt() {
        return diningCourt;
    }

    public String getMeal() {
        return meal;
    }

    public String getStation() {
        return station;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "[" + diningCourt + " | " + meal + " | " + station + "] " + name;
    }
}