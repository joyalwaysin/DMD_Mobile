package com.nagainfomob.app.model;

/**
 * Created by Joy on 01/06/2017.
 */

public class UserModel {

    private int id;
    private String user_id;
    private String user_name;
    private String password;
    private String country_code;
    private String mobile_no;
    private String email;
    private String user_type;
    private String sub_type;
    private String account_type;
    private String is_first_login;
    private String is_active;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getUserId() {
        return user_id;
    }
    public void setUserId(String user_id) {
        this.user_id = user_id;
    }

    public String getName() {
        return user_name;
    }
    public void setName(String name) {
        this.user_name = name;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getCountry_code() {
        return country_code;
    }
    public void setCountry_code(String ccd) {
        this.country_code = ccd;
    }

    public String getMobile_no() {
        return mobile_no;
    }
    public void setMobile_no(String mobile_no) {
        this.mobile_no = mobile_no;
    }

    public String getUserType() {
        return user_type;
    }
    public void setUserType(String user_type) {
        this.user_type = user_type;
    }

    public String getSubType() {
        return sub_type;
    }
    public void setSubType(String sub_type) {
        this.sub_type = sub_type;
    }

    public String getAccounType() {
        return account_type;
    }
    public void setAccounType(String account_type) {
        this.account_type = account_type;
    }

    public String getIs_first_login() {
        return is_first_login;
    }
    public void setIs_first_login(String is_first_login) {
        this.is_first_login = is_first_login;
    }

    public String getIs_active() {
        return is_active;
    }
    public void setIs_active(String is_active) {
        this.is_active = is_active;
    }
}
