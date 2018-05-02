package com.ipipeline.jms.message;

import java.util.Objects;

public class ThreadModel {
    private String threadName;
    private String carrierName;

    public ThreadModel(String threadName, String carrierName) {
        this.threadName = threadName;
        this.carrierName = carrierName;
    }

    public String getThreadName() {
        return threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ThreadModel that = (ThreadModel) o;
        return Objects.equals(threadName, that.threadName) &&
                Objects.equals(carrierName, that.carrierName);
    }

    @Override
    public int hashCode() {

        return Objects.hash(threadName, carrierName);
    }

    public String getCarrierName() {
        return carrierName;
    }

    public void setCarrierName(String carrierName) {
        this.carrierName = carrierName;
    }

    @Override
    public String toString() {
        return "ThreadModel{" +
                "threadName='" + threadName + '\'' +
                ", carrierName='" + carrierName + '\'' +
                '}';
    }
}
