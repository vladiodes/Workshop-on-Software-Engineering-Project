package main.Service;

import main.ExternalServices.Payment.IPayment;
import main.ExternalServices.Supplying.ISupplying;

public class Configuration {
    Class<? extends IPayment> PaymentSystem;
    Class<? extends ISupplying> SupplyingSystem;
    String adminUsername;
    String adminPassword;
    String persistence_unit;
    Boolean shouldPersist;

    public Configuration() {
    }



    public String getPersistence_unit() {
        return persistence_unit;
    }

    public void setPersistence_unit(String persistence_unit) {
        this.persistence_unit = persistence_unit;
    }

    public Boolean getShouldPersist() {
        return shouldPersist;
    }

    public void setShouldPersist(Boolean shouldPersist) {
        this.shouldPersist = shouldPersist;
    }

    public Class<? extends IPayment> getPaymentSystem() {
        return PaymentSystem;
    }

    public void setPaymentSystem(Class<? extends IPayment> paymentSystem) {
        PaymentSystem = paymentSystem;
    }

    public Class<? extends ISupplying> getSupplyingSystem() {
        return SupplyingSystem;
    }

    public void setSupplyingSystem(Class<? extends ISupplying> supplyingSystem) {
        SupplyingSystem = supplyingSystem;
    }

    public String getAdminUsername() {
        return adminUsername;
    }

    public void setAdminUsername(String adminUsername) {
        this.adminUsername = adminUsername;
    }

    public String getAdminPassword() {
        return adminPassword;
    }

    public void setAdminPassword(String adminPassword) {
        this.adminPassword = adminPassword;
    }
}
