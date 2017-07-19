package com.example.jisung.herh;

/**
 * Created by 여누끄 on 2017-07-18.
 */

public class User_Infor {
    private String time;
    private String peopleNum;
    private String error;
    private String phoneNum;
    private String name;

    public User_Infor(String name, String peopleNum,  String phoneNum, String time){
        this.name = name;
        this.peopleNum = peopleNum;
        this.phoneNum = phoneNum;
        this.time = time;
    }

    public void set_Time(String time){
        this.time = time;
    }
    public void set_PhoneNum(String peopleNum){
        this.phoneNum = peopleNum;
    }
    public void set_Name(String name){
        this.name = name;
    }

    public void set_PeopleNum(String peopleNum){
        this.peopleNum = peopleNum;
    }

    public String get_Time(){
        return this.time;
    }
    public String get_PhoneNum(){
        return this.phoneNum;
    }
    public String get_Name(){
        return this.name;
    }
    public String get_PeopleNum(){
        return this.peopleNum;
    }


}
