package com.gtsr.gtsr.model;

public class QubeResultModel {
    private String testResult;
    private String igGLevel;
    private String referenceRange;
    private String resultMessage;

    public String getTestResult() {
        return testResult;
    }

    public void setTestResult(String testResult) {
        this.testResult = testResult;
    }

    public String getIgGLevel() {
        return igGLevel;
    }

    public void setIgGLevel(String igGLevel) {
        this.igGLevel = igGLevel;
    }

    public String getReferenceRange() {
        return referenceRange;
    }

    public void setReferenceRange(String referenceRange) {
        this.referenceRange = referenceRange;
    }

    public String getResultMessage() {
        return resultMessage;
    }

    public void setResultMessage(String resultMessage) {
        this.resultMessage = resultMessage;
    }
}
