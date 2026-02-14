package com.manchesterseals.model;

public class CountryData {
    private int id;
    private String country;

    public CountryData() {
    }

    public CountryData(int id, String country) {
        this.id = id;
        this.country = country;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Override
    public String toString() {
        return "CountryData{" +
                "id=" + id +
                ", country='" + country + '\'' +
                '}';
    }
}

