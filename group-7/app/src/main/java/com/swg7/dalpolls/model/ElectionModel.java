package com.swg7.dalpolls.model;

import android.icu.util.DateInterval;

import java.io.Serializable;
import java.util.Date;

public class ElectionModel implements Serializable {
    public String name;
    public String id;
    public Date startDate;
    public Date endDate;
    public boolean isFinal;
    public boolean isPublic;
    public boolean isOpen;
    public boolean areResultsVisible;
    public boolean isEditing;

    public ElectionModel(){
    }

    public ElectionModel(ElectionModel copy){
        this.name = copy.name;
        this.id = copy.id;
        this.startDate = copy.startDate;
        this.endDate = copy.endDate;
        this.isFinal = copy.isFinal;
        this.isPublic = copy.isPublic;
        this.isOpen = copy.isOpen;
        this.areResultsVisible = copy.areResultsVisible;
        this.isEditing = copy.isEditing;
    }
}
