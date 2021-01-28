package com.gtsr.gtsr.database;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;


public class UrineresultsModel {
    public UrineresultsModel() {

    }

    @DatabaseField(generatedId = true)
    int id;


    @DatabaseField(columnName = "testid")
    String test_id;

    @DatabaseField(columnName = "isFasting")
    String isFasting;

    @DatabaseField(columnName = "relationType")
    String relationtype;

    @DatabaseField(columnName = "testedTime")
    String testedTime;

    @DatabaseField(columnName = "userName")
    String userName;

    @DatabaseField
    private String testReportNumber;


    @DatabaseField(columnName = "testtype")
    private String testType;


    @DatabaseField(columnName = "longitude")
    private String longitude;

    @DatabaseField(columnName = "latitude")
    private String latitude;


    @ForeignCollectionField
    private ForeignCollection<TestFactors> testFactorses;

    public String getIsFasting() {
        return isFasting;
    }

    public void setIsFasting(String isFasting) {
        this.isFasting = isFasting;
    }


    public ForeignCollection<TestFactors> getTestFactorses() {
        return testFactorses;
    }

    public void setTestFactorses(ForeignCollection<TestFactors> testFactorses) {
        this.testFactorses = testFactorses;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTestReportNumber() {
        return testReportNumber;
    }

    public void setTestReportNumber(String testReportNumber) {
        this.testReportNumber = testReportNumber;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTest_id() {
        return test_id;
    }

    public void setTest_id(String test_id) {
        this.test_id = test_id;
    }

    public String getRelationtype() {
        return relationtype;
    }

    public void setRelationtype(String relationtype) {
        this.relationtype = relationtype;
    }

    public String getTestedTime() {
        return testedTime;
    }

    public void setTestedTime(String testedTime) {
        this.testedTime = testedTime;
    }


    public String getTestType() {
        return testType;
    }

    public void setTestType(String testType) {
        this.testType = testType;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

 }
