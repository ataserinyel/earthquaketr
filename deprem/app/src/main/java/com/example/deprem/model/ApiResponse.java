package com.example.deprem.model;

import java.util.List;

public class ApiResponse {
    private boolean status;
    private List<Earthquake> result;
    public boolean isStatus() {return status;}
    public List<Earthquake> getResult() {return result;}
}
