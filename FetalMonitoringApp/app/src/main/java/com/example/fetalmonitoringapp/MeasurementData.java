package com.example.fetalmonitoringapp;

import android.app.Application;
import java.util.ArrayList;
import java.util.List;

public class MeasurementData extends Application {

    private List<String> movementValues = new ArrayList<>();;
    private List<String> heartrateValues = new ArrayList<>();;
    private List<CharSequence> dateValues = new ArrayList<>();;

    public List<String> getMovementValues() {
        return movementValues;
    }

    public List<String> getHeartrateValues() {
        return heartrateValues;
    }

    public List<CharSequence> getDateValues() {
        return dateValues;
    }

    public void setMovementValues(List<String> movementValues) {
        this.movementValues = movementValues;
    }

    public void setHeartrateValues(List<String> heartrateValues) {
        this.heartrateValues = heartrateValues;
    }

    public void setDateValues(List<CharSequence> dateValues) {
        this.dateValues = dateValues;
    }

}
