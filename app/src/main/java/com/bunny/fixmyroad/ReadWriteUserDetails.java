package com.bunny.fixmyroad;

public class ReadWriteUserDetails {

    public String dob, gender, mobile, name, email;

    public ReadWriteUserDetails(){};

    public ReadWriteUserDetails(String textDoB, String textGender, String textMobile,String textName, String textEmail) {
       // this.fullName = textFullName;
        this.dob = textDoB;
        this.gender = textGender;
        this.mobile = textMobile;
        this.name = textName;
        this.email = textEmail;
    }
}
