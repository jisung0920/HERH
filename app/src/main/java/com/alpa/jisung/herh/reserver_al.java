package com.alpa.jisung.herh;

/**
 * Created by jisung on 2017-07-21.
 */

public class reserver_al extends reserver {
    private String date;
    public reserver_al(String time, String name, String tel, int num, int error, int allow, String date) {
        super(time, name, tel, num, error, allow);
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
