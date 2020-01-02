package com.nagainfomob.app.model;

/**
 * Created by Joy on 01/06/2017.
 */
public class CompanyModel {

    private int id;
    private String user_id;
    private String comp_name;
    private String comp_desc;
    private String comp_phone_number;
    private String comp_logo_url;
    private String comp_addr1;
    private String comp_addr2;
    private String comp_country_code;
    private String comp_country_name;

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

    public String getComp_name() {
        return comp_name;
    }
    public void setComp_name(String comp_name) {
        this.comp_name = comp_name;
    }

    public String getComp_desc() {
        return comp_desc;
    }
    public void setComp_desc(String comp_desc) {
        this.comp_desc = comp_desc;
    }

    public String getComp_phone_number() {
        return comp_phone_number;
    }
    public void setComp_phone_number(String comp_phone_number) {
        this.comp_phone_number = comp_phone_number;
    }

    public String getComp_logo_url() {
        return comp_logo_url;
    }
    public void setComp_logo_url(String comp_logo_url) {
        this.comp_logo_url = comp_logo_url;
    }

    public String getComp_addr1() {
        return comp_addr1;
    }
    public void setComp_addr1(String comp_addr1) {
        this.comp_addr1 = comp_addr1;
    }

    public String getComp_addr2() {
        return comp_addr2;
    }
    public void setComp_addr2(String comp_addr2) {
        this.comp_addr2 = comp_addr2;
    }

    public String getComp_country_code() {
        return comp_country_code;
    }
    public void setComp_country_code(String comp_country_code) {
        this.comp_country_code = comp_country_code;
    }

    public String getComp_country_name() {
        return comp_country_name;
    }
    public void setComp_country_name(String comp_country_name) {
        this.comp_country_name = comp_country_name;
    }

}