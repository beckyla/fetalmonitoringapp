package com.example.fetalmonitoringapp;

import android.app.Application;
import java.util.ArrayList;
import java.util.List;

public class MeasurementData extends Application {

    private List<String> heartrateValues = new ArrayList<>();
    private List<List<MainActivity.kickInfo>> kickInfoData = new ArrayList<>();
    private List<MainActivity.piezoInfo> piezoInfoData = new ArrayList<>();
    private List<String> rawPiezoData = new ArrayList<>();

    public List<String> getHeartrateValues() {
        return heartrateValues;
    }

    public List<List<MainActivity.kickInfo>> getKickInfoData() {
        return this.kickInfoData;
    }

    public List<MainActivity.piezoInfo> getPiezoInfoData() {
        return this.piezoInfoData;
    }

    public List<String> getRawPiezoData() { return this.rawPiezoData; }

    public void setHeartrateValues(List<String> heartrateValues) { this.heartrateValues = heartrateValues; }

    public void setKickInfoData(List<List<MainActivity.kickInfo>> kickInfoData) { this.kickInfoData = kickInfoData; }

    public void setPiezoInfoData(List<MainActivity.piezoInfo> pizeoInfoData) { this.piezoInfoData = pizeoInfoData; }

    public void setRawPiezoData(List<String> rawPiezoData) { this.rawPiezoData = rawPiezoData; }
}
