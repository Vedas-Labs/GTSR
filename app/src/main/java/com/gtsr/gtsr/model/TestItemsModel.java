package com.gtsr.gtsr.model;

import java.util.ArrayList;

public class TestItemsModel {
    String testID;
    String stripNo;
    String testName;
    ArrayList<TestParametersResponseModel> parameters;
    String collectionNotes;
    String configFile;
    String specimenType;
    String specimenQuantity;

    public String getCollectionNotes() {
        return collectionNotes;
    }

    public void setCollectionNotes(String collectionNotes) {
        this.collectionNotes = collectionNotes;
    }

    public String getConfigFile() {
        return configFile;
    }

    public void setConfigFile(String configFile) {
        this.configFile = configFile;
    }

    public String getSpecimenType() {
        return specimenType;
    }

    public void setSpecimenType(String specimenType) {
        this.specimenType = specimenType;
    }

    public String getSpecimenQuantity() {
        return specimenQuantity;
    }

    public void setSpecimenQuantity(String specimenQuantity) {
        this.specimenQuantity = specimenQuantity;
    }

    public String getTestID() {
        return testID;
    }

    public void setTestID(String testID) {
        this.testID = testID;
    }

    public String getStripNo() {
        return stripNo;
    }

    public void setStripNo(String stripNo) {
        this.stripNo = stripNo;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public ArrayList<TestParametersResponseModel> getParameters() {
        return parameters;
    }

    public void setParameters(ArrayList<TestParametersResponseModel> parameters) {
        this.parameters = parameters;
    }
}
