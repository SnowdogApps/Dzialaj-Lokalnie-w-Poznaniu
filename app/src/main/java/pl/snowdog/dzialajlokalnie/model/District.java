package pl.snowdog.dzialajlokalnie.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "Districts")
public class District extends Model {

    @Column(unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    private int districtID;

    @Column
    private String name;

    @Column
    private String description;

    @Column
    private double lon;

    @Column
    private double lat;

    @Column
    private int cityID;

    @Column
    private String coordinates;

    private Polygon polygon;

    public District() {
        super();

    }

    public District(int districtID, String name, String description, double lon, double lat, int cityID) {
        super();
        this.districtID = districtID;
        this.name = name;
        this.description = description;
        this.lon = lon;
        this.lat = lat;
        this.cityID = cityID;
    }

    @Override
    public String toString() {
        return "District{" +
                "districtID=" + districtID +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", lon=" + lon +
                ", lat=" + lat +
                ", coordinates=" + coordinates +
                ", cityID=" + cityID +
                '}';
    }

    public int getDistrictID() {
        return districtID;
    }

    public void setDistrictID(int districtID) {
        this.districtID = districtID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public int getCityID() {
        return cityID;
    }

    public void setCityID(int cityID) {
        this.cityID = cityID;
    }

    public Polygon getPolygon() {
        return polygon;
    }

    public void setPolygon(Polygon polygon) {
        this.polygon = polygon;
    }

    public String getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(String coordinates) {
        this.coordinates = coordinates;

    }

    public void createPolygonFromCoordinates() {
        this.polygon = new Polygon(coordinates);
    }
}