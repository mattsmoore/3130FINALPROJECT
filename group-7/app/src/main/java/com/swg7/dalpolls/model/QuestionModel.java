package com.swg7.dalpolls.model;

import java.io.Serializable;

public class QuestionModel implements Serializable {
    public String name;
    public String id;

    public QuestionModel(){

    }

    public QuestionModel(String name, String id){
        this.name = name;
        this.id = id;
    }
}
