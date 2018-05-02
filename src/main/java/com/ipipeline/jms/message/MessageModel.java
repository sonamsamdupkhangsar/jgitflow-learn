package com.ipipeline.jms.message;

import java.io.Serializable;

public class MessageModel {
    private String carrierId;
    private String orderId;

    public MessageModel(String carrierId, String orderId) {
        this.carrierId = carrierId;
        this.orderId = orderId;
    }

    public MessageModel() {
    }

    public String getCarrierId() {
        return carrierId;
    }

    public void setCarrierId(String carrierId) {
        this.carrierId = carrierId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    @Override
    public String toString() {
        return "MessageModel{" +
                "carrierId='" + carrierId + '\'' +
                ", orderId='" + orderId + '\'' +
                '}';
    }
}
