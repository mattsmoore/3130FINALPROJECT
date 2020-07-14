package com.swg7.dalpolls.model;

import java.io.Serializable;

public class BallotModel implements Serializable {

    public String userID;
    public String answerID;

    public BallotModel(){}

    public BallotModel(String userID, String answerID){
        this.userID = userID;
        this.answerID = answerID;
    }
}
