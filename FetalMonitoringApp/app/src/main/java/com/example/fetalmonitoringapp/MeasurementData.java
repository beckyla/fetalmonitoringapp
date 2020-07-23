package com.example.fetalmonitoringapp;

import android.app.Application;
import java.util.ArrayList;
import java.util.List;

public class MeasurementData extends Application {

    private List<Integer> movementValues = new ArrayList<>();;
    private List<Integer> heartrateValues = new ArrayList<>();;
    private List<CharSequence> dateValues = new ArrayList<>();;

    public List<Integer> getMovementValues() {
        return movementValues;
    }

    public List<Integer> getHeartrateValues() {
        return heartrateValues;
    }

    public List<CharSequence> getDateValues() {
        return dateValues;
    }

    public void setMovementValues(List<Integer> movementValues) {
        this.movementValues = movementValues;
    }

    public void setHeartrateValues(List<Integer> heartrateValues) {
        this.heartrateValues = heartrateValues;
    }

    public void setDateValues(List<CharSequence> dateValues) {
        this.dateValues = dateValues;
    }

}
