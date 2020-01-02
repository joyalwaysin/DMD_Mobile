package com.nagainfomob.app.model.Payment;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by joy on 02/05/18.
 */

public class PaymentData {
    @SerializedName("mobile")
    @Expose
    private String mobile;
    @SerializedName("amount")
    @Expose
    private String amount;
    @SerializedName("plan_id")
    @Expose
    private String planId;
    @SerializedName("payment_for")
    @Expose
    private String paymentFor;

    /**
     * No args constructor for use in serialization
     *
     */
    public PaymentData() {
    }

    /**
     *
     * @param mobile
     * @param amount
     * @param paymentFor
     * @param planId
     */
    public PaymentData(String mobile, String amount, String planId, String paymentFor) {
        super();
        this.mobile = mobile;
        this.amount = amount;
        this.planId = planId;
        this.paymentFor = paymentFor;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    public String getPaymentFor() {
        return paymentFor;
    }

    public void setPaymentFor(String paymentFor) {
        this.paymentFor = paymentFor;
    }
}
