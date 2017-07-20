package com.example.jisung.herh;

/**
 * Created by jisung on 2017-07-20.
 */

public class reserver {
    private String time;
    private String name;
    private String num;
    private String error;
    private int allow;

    public reserver(String time, String name, String num, String error, int allow) {
        this.time = time;
        this.name = name;
        this.num = num;
        this.error = error;
        this.allow = allow;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public int getAllow() {
        return allow;
    }

    public void setAllow(int allow) {
        this.allow = allow;
    }
}
