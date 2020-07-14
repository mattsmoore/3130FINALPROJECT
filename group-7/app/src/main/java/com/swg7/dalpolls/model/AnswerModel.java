package com.swg7.dalpolls.model;

import java.io.Serializable;

public class AnswerModel implements Serializable {
    public String name;
    public String id;

    public AnswerModel(){

    }

    public AnswerModel(String name, String id){
        this.name = name;
        this.id = id;
    }
}
