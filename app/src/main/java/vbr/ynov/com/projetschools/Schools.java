package vbr.ynov.com.projetschools;

/**
 * Created by valen on 05/06/2018.
 */

public class Schools {

    private double longitude;
    private double latitude;
    private String name;
    private String status;
    private String address;
    private Integer nbEleves;


    public Schools(double longitude, double latitude, String name, String status, String address, Integer nbEleves) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.name = name;
        this.status = status;
        this.address = address;
        this.nbEleves = nbEleves;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getNbEleves() {
        return nbEleves;
    }

    public void setNbEleves(Integer nbEleves) {
        this.nbEleves = nbEleves;
    }
}
