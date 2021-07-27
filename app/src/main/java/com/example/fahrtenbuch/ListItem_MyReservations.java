package com.example.fahrtenbuch;

public class ListItem_MyReservations {

    private Integer reservation_id ;
    private Integer car_id ;
    private String start = "";
    private String ziel = "";
    private Float entfernung;

    public ListItem_MyReservations(Integer reservation_id, String start, String ziel, Float entfernung, Integer car_id) {

        this.reservation_id = reservation_id;
        this.start = start;
        this.ziel = ziel;
        this.entfernung = entfernung;
        this.car_id = car_id;
    }

    public Integer getReservation_id() {
        return reservation_id;
    }
    public String getStart() {
        return start;
    }
    public String getZiel() {
        return ziel;
    }

    public Integer getCar_id() {
        return car_id;
    }
    public Float getEntfernung() {
        return entfernung;
    }
}
