package com.swg7.dalpolls;

import com.swg7.dalpolls.election.Election;
import android.text.style.TtsSpan;
import com.swg7.dalpolls.model.ElectionModel;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ElectionCreator {
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy");

    private ElectionModel model;
    private String startDate, endDate;



    public ElectionCreator() {
        model = new ElectionModel();
    }

    public ElectionCreator setName(String name) {
        model.name = name;
        return this;
    }
    public ElectionCreator setStartDate(String startDate) {
        this.startDate = startDate;
        return this;
    }

    public ElectionCreator setId(String id) {
        this.model.id = id;
        return this;
    }
    public ElectionCreator setEndDate(String endDate) {
        this.endDate = endDate;
        return this;
    }
    public ElectionCreator setPublish(Boolean value) {
        model.isPublic = value;
        return this;
    }
    public ElectionCreator setOpen(Boolean value) {
        model.isOpen = value;
        return this;
    }
    public ElectionCreator setResults(Boolean value) {
        model.areResultsVisible = value;
        return this;
    }

    public ElectionModel build() {
        model.startDate = parse(startDate);
        model.endDate = parse(endDate);
        return new ElectionModel(model);
    }

    public boolean isValid() {
        return hasValidDate() && hasValidTitle();
    }

    //          MONTH/DAY/YEAR
    public boolean hasValidDate() {
        return checkFormat(startDate) && checkFormat(endDate)
                && parse(startDate).before(parse(endDate));
    }

    private Date parse(String s) {
        try {
            return DATE_FORMAT.parse(s);
        } catch (ParseException e) {
            return null;
        }
    }

    private boolean checkFormat(String s) {
        return parse(s) != null;
    }

    public boolean hasValidTitle() {
        return (!model.name.isEmpty());
    }

}
