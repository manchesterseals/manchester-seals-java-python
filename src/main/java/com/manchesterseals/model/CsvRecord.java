package com.manchesterseals.model;

public class CsvRecord {
    private Long unixTimestamp;
    private Double latitude;
    private Double longitude;
    private Double altitude;
    private Double ecefX;
    private Double ecefY;
    private Double ecefZ;
    private Double ecefVelocityX;
    private Double ecefVelocityY;
    private Double ecefVelocityZ;

    public CsvRecord() {
    }

    public CsvRecord(Long unixTimestamp, Double latitude, Double longitude, Double altitude, Double ecefX, Double ecefY, Double ecefZ, Double ecefVelocityX, Double ecefVelocityY, Double ecefVelocityZ) {
        this.unixTimestamp = unixTimestamp;
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
        this.ecefX = ecefX;
        this.ecefY = ecefY;
        this.ecefZ = ecefZ;
        this.ecefVelocityX = ecefVelocityX;
        this.ecefVelocityY = ecefVelocityY;
        this.ecefVelocityZ = ecefVelocityZ;
    }

    public Long getUnixTimestamp() {
        return unixTimestamp;
    }

    public void setUnixTimestamp(Long unixTimestamp) {
        this.unixTimestamp = unixTimestamp;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getAltitude() {
        return altitude;
    }

    public void setAltitude(Double altitude) {
        this.altitude = altitude;
    }

    public Double getEcefX() {
        return ecefX;
    }

    public void setEcefX(Double ecefX) {
        this.ecefX = ecefX;
    }

    public Double getEcefY() {
        return ecefY;
    }

    public void setEcefY(Double ecefY) {
        this.ecefY = ecefY;
    }

    public Double getEcefZ() {
        return ecefZ;
    }

    public void setEcefZ(Double ecefZ) {
        this.ecefZ = ecefZ;
    }

    public Double getEcefVelocityX() {
        return ecefVelocityX;
    }

    public Double getEcefVelocityY() {
        return ecefVelocityY;
    }

    public Double getEcefVelocityZ() {
        return ecefVelocityZ;
    }


    @Override
    public String toString() {
        return "CsvRecord{" +
                "unixTimestamp=" + unixTimestamp +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", altitude=" + altitude +
                ", ecefX=" + ecefX +
                ", ecefY=" + ecefY +
                ", ecefZ=" + ecefZ +
                ", ecefVelocityX=" + ecefVelocityX +
                ", ecefVelocityZ=" + ecefVelocityZ +
                ", ecefVelocityY=" + ecefVelocityY +
                '}';
    }
}
