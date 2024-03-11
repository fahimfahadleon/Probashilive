package com.probashiincltd.probashilive.models;

import androidx.annotation.NonNull;

public class IpModel {
    //{"status":"success",
    // "country":"Bangladesh",
    // "countryCode":"BD",
    // "region":"D",
    // "regionName":"Khulna Division",
    // "city":"Bhātpāra Abhaynagar",
    // "zip":"",
    // "lat":23.0147,
    // "lon":89.4394,
    // "timezone":"Asia/Dhaka",
    // "isp":"Banglalink Digital Communications Ltd",
    // "org":"Banglalink Digital Communications Ltd",
    // "as":"AS45245 Banglalink Digital Communications Ltd",
    // "query":"202.86.219.213"}
    String country;
    String countryCode;
    String regionName;
    String city;
    String timezone;
    String lat;
    String lon;
    String query;

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }


    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    @NonNull
    @Override
    public String toString() {
        return "IpModel{" +
                "country='" + country + '\'' +
                ", countryCode='" + countryCode + '\'' +
                ", regionName='" + regionName + '\'' +
                ", city='" + city + '\'' +
                ", timezone='" + timezone + '\'' +
                ", lat='" + lat + '\'' +
                ", lon='" + lon + '\'' +
                ", query='" + query + '\'' +
                '}';
    }
}
