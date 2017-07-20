package com.example.jisung.herh;

/**
 * Created by jisung on 2017-07-20.
 */

public class reserver {
    private String time;
    private String name;
    private String tel;
    private int num;
    private int error;
    private int allow;

    public reserver(String time, String name,String tel, int num, int error, int allow) {
        this.time = time;
        this.name = name;
        this.tel = tel;
        this.num = num;
        this.error = error;
        this.allow = allow;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
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

    public int getAllow() {
        return allow;
    }

    public void setAllow(int allow) {
        this.allow = allow;
    }
}
