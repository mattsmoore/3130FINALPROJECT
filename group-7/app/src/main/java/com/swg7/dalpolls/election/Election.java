package com.swg7.dalpolls.election;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class Election implements Serializable {

    public String name;
    public  String id;
    public List<IQuestion> listQ;
    public boolean isFinal;
    public boolean isPublic;
    public boolean isOpen;
    public boolean areResultsVisible;


    public Election(){
        name = null;
        id = null;
        listQ = new ArrayList<IQuestion>();
        isFinal = false;
        isPublic = false;
        isOpen = false;
        areResultsVisible = false;
    }
    public Election(String n, boolean isFinal, boolean isPublic, boolean isOpen, boolean areResultsVisible){
        this.name = n;
        this.isFinal = isFinal;
        this.isPublic = isPublic;
        this.isOpen = isOpen;
        this.areResultsVisible = areResultsVisible;
    }

    public List<IQuestion> getQuestions(){
        return listQ;
    }

    public void setQuestions(List<IQuestion> questions) {
        listQ = questions;
        //TODO: add NQ2 to the list
    }
    public void setName(String n){name = n;}

    public boolean isEmpty() {
        if(listQ.isEmpty())return true;
        else return false;
    }
    public boolean canFinalize() {
        //has questions
        if(! listQ.isEmpty() && ! name.isEmpty()) return true;
        else return false;
    }
    public boolean canPublish() {
        //must have questions and name
        if(isFinal()) return true;
        else return false;
    }
    public boolean canOpen() {
        //must be published
        if(isPublic())return true;
        else return false;
    }
    public boolean canMakeResultVisible() {
        //must be public and closed
        if( isPublic() && ! isOpen() ) return true;
        else return false;
    }
    public boolean canPreview() {
        //needs questions, can be previewed at any staged once it has questions
        if( ! listQ.isEmpty()) return true;
        else return false;
    }

    public void publish() {isPublic = true;}
    public void unPublish() {isPublic = false;}
    public void open() {
        isOpen = true;
    }
    public void close() {
        isOpen = false;
    }
    public void showResults() {
        areResultsVisible = true;
    }
    public void hideResults() {
        areResultsVisible = false;
    }

    public boolean isFinal() {return isFinal;}
    public boolean isPublic() {return isPublic;}
    public boolean isOpen() {return isOpen;}
    public boolean areResultsVisible() {return areResultsVisible;}

    //Copies name and questions to new election clone
    public Election clone() {
        Election c = new Election();
        c.name = this.name;
        c.setQuestions(new ArrayList<IQuestion>(this.listQ));
        return c;
    }
}
