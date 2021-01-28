package com.gtsr.gtsr.database;/*
package com.example.wave.spectrocare.Models;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

*/

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Abhilash on 12/5/2018.
 */

@DatabaseTable(tableName = "factors")
public class TestFactors {

    public TestFactors(){
    }

    public int getTestId() {
        return testId;
    }

    public void setTestId(int testId) {
        this.testId = testId;
    }

    @DatabaseField(generatedId = true)
    int testId;

    public boolean isFlag() {
        return flag;
    }

    public void setSno(int sno) {
        Sno = sno;
    }

    @DatabaseField(columnName = "sno")
    private int Sno;

    @DatabaseField(columnName = "flag", dataType = DataType.BOOLEAN)
    private boolean flag;

    @DatabaseField(columnName = "healthReferenceRange")
    private String healthReferenceRanges;


    @DatabaseField(columnName = "result")
    private String result;

    @DatabaseField(columnName = "testName")
    private String testName;


    @DatabaseField(columnName = "unit")
    private String unit;

    @DatabaseField(columnName = "value")
    private String value;


    @DatabaseField(columnName = "urineresults", canBeNull = false, foreign = true, foreignAutoRefresh = true)
    private UrineresultsModel urineresultsModel;


    public UrineresultsModel getUrineresultsModel() {
        return urineresultsModel;
    }

    public void setUrineresultsModel(UrineresultsModel urineresultsModel) {
        this.urineresultsModel = urineresultsModel;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public boolean getFlag() {
        return this.flag;
    }

    public void setHealthReferenceRanges(String healthReferenceRanges) {
        this.healthReferenceRanges = healthReferenceRanges;
    }

    public String getHealthReferenceRanges() {
        return this.healthReferenceRanges;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getResult() {
        return this.result;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public String getTestName() {
        return this.testName;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }


    public String getUnit() {
        return this.unit;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

  /*  public void set_id(String _id) {
        this._id = _id;
    }

    public String get_id() {
        return this._id;
    }*/
}