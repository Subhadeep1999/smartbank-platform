package com.smartbank.customer.dto;

import com.smartbank.customer.entity.CustomerStatus;

public class CustomerSearchCriteria {

    private CustomerStatus status;
    private String name;
    private String cifId;
    private String email;

    public String getCifId() {
        return cifId;
    }

    public void setCifId(String cifId) {
        this.cifId = cifId;
    }

    public CustomerStatus getStatus() {
        return status;
    }

    public void setStatus(CustomerStatus status) {
        this.status = status;
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
}
