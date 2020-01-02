package com.nagainfomob.app.model.ProfileUpdate;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by joy on 14/11/17.
 */

public class UserData {
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("organization")
    @Expose
    private String organization;
    @SerializedName("org_description")
    @Expose
    private String orgDescription;
    @SerializedName("org_phone")
    @Expose
    private String orgPhone;
    @SerializedName("address1")
    @Expose
    private String address1;
    @SerializedName("address2")
    @Expose
    private String address2;
    @SerializedName("country")
    @Expose
    private String country;
    @SerializedName("company_logo")
    @Expose
    private String companyLogo;

    /**
     * No args constructor for use in serialization
     *
     */
    public UserData() {
    }

    /**
     *
     * @param orgPhone
     * @param organization
     * @param email
     * @param name
     * @param orgDescription
     * @param companyLogo
     * @param address1
     * @param address2
     * @param country
     */
    public UserData(String name, String email, String organization, String orgDescription, String orgPhone,
                    String address1, String address2, String country, String companyLogo) {
        super();
        this.name = name;
        this.email = email;
        this.organization = organization;
        this.orgDescription = orgDescription;
        this.orgPhone = orgPhone;
        this.address1 = address1;
        this.address2 = address2;
        this.country = country;
        this.companyLogo = companyLogo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getOrgDescription() {
        return orgDescription;
    }

    public void setOrgDescription(String orgDescription) {
        this.orgDescription = orgDescription;
    }

    public String getOrgPhone() {
        return orgPhone;
    }

    public void setOrgPhone(String orgPhone) {
        this.orgPhone = orgPhone;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCompanyLogo() {
        return companyLogo;
    }

    public void setCompanyLogo(String companyLogo) {
        this.companyLogo = companyLogo;
    }
}
