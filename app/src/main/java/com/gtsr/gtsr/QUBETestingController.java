package com.gtsr.gtsr;

import android.content.Context;
import android.util.Log;

import com.spectrochips.spectrumsdk.DeviceConnectionModule.Commands;
import com.spectrochips.spectrumsdk.DeviceConnectionModule.DataPoint;
import com.spectrochips.spectrumsdk.DeviceConnectionModule.PolynomialRegression;
import com.spectrochips.spectrumsdk.FRAMEWORK.SCConnectionHelper;
import com.spectrochips.spectrumsdk.FRAMEWORK.SCTestAnalysis;
import com.spectrochips.spectrumsdk.FRAMEWORK.TestFactors;
import com.spectrochips.spectrumsdk.MODELS.ConcentrationControl;
import com.spectrochips.spectrumsdk.MODELS.IntensityChart;
import com.spectrochips.spectrumsdk.MODELS.RCTableData;
import com.spectrochips.spectrumsdk.MODELS.ReflectanceChart;
import com.spectrochips.spectrumsdk.MODELS.SpectorDeviceDataStruct;
import com.spectrochips.spectrumsdk.MODELS.SpectroDeviceDataController;
import com.spectrochips.spectrumsdk.MODELS.Steps;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

public class QUBETestingController {
    private static QUBETestingController ourInstance;
    public SpectorDeviceDataStruct spectroDeviceObject;
    public ArrayList<Steps> motorSteps;
    int stripNumber = -1;
    public String requestCommand = "";
    private String receivedDataString = "";
    QUBETestDataInterface qubeTestDataInterface;
    private ArrayList<Float> pixelXAxis = new ArrayList();
    private ArrayList<Float> wavelengthXAxis = new ArrayList();
    private ArrayList<ReflectanceChart> reflectenceChartsArray = new ArrayList();
    private ArrayList<ConcentrationControl> concentrationArray = new ArrayList();
    private ArrayList<Float> darkSpectrumIntensityArray = new ArrayList();
    private ArrayList<Float> standardWhiteIntensityArray = new ArrayList();
    public ArrayList<IntensityChart> intensityChartsArray = new ArrayList();
    private ArrayList<Float> intensityArray;
    private ArrayList<String> hexaDecimalArray;

    private String darkSpectrumTitle = "Dark Spectrum";
    private String standardWhiteTitle = "Standard White (Reference)";
    private boolean isForDarkSpectrum = false;
    private boolean isFromWhiteSpectrum = false;
    private boolean isCalibration = false;
    private boolean isEjectType = false;
    private boolean isFromStripTryOut = false;

    public static QUBETestingController getInstance() {
        if (ourInstance == null) {
            ourInstance = new QUBETestingController();
            ourInstance.hexaDecimalArray = new ArrayList();
        }
        return ourInstance;
    }

    public void fillContext(Context context1) {
        intensityArray = new ArrayList<>();
        spectroDeviceObject = new SpectorDeviceDataStruct();
        loadDefaultSpectrodeviceObject("CUBE_Covid19.json");
        loadInterface();
    }

    private void loadDefaultSpectrodeviceObject(String fileName) {
        SpectroDeviceDataController.getInstance().loadJsonFromUrl(fileName);
        if (SpectroDeviceDataController.getInstance().spectroDeviceObject != null) {
            spectroDeviceObject = SpectroDeviceDataController.getInstance().spectroDeviceObject;
            motorSteps = spectroDeviceObject.getStripControl().getSteps();
            for (int i = 0; i < motorSteps.size(); ++i) {
                Log.e("stepssize", "call" + motorSteps.size());
                Log.e("qubemotorStepstestname", "call" + ((Steps) motorSteps.get(i)).getTestName() + ((Steps) motorSteps.get(i)).getStripIndex());
            }
        }

    }

    public void activatenotifications(QUBETestDataInterface jsonFileInterface1) {
        if (qubeTestDataInterface != null) {
            qubeTestDataInterface = null;
        }
        qubeTestDataInterface = jsonFileInterface1;
    }

    public void setDeviceSettings(JSONObject object) {
        SpectorDeviceDataStruct obj = SpectroDeviceDataController.getInstance().getObjectFromFile(object);
        if (obj != null) {
            Log.e("localspectroobject", "call" + obj.getStripControl().getDistancePerStepInMM());
            spectroDeviceObject = obj;
            motorSteps = spectroDeviceObject.getStripControl().getSteps();
        }

    }

    public void getDeviceSettings(String testName, String category, String date) {
        SpectroDeviceDataController.getInstance().loadJsonFromUrl(testName);
        if (SpectroDeviceDataController.getInstance().spectroDeviceObject != null) {
            spectroDeviceObject = SpectroDeviceDataController.getInstance().spectroDeviceObject;
            motorSteps = spectroDeviceObject.getStripControl().getSteps();
            Log.e("loadDefaultSpect", "call" + spectroDeviceObject.getStripControl().getDistanceFromHolderEdgeTo1STStripInMM());
            Log.e("loadDefaultSpect", "call" + motorSteps.size());
        }

    }

    public void loadInterface() {
        syncDeviceData(new SCTestAnalysis.TestDataInterface() {
            public void gettingData(byte[] data) {
                String text = decodeUTF8(data);
                Log.e("gettingData", "call" + text);
                socketDidReceiveMessage(text, requestCommand);
            }
        });
    }

    //calling response data from SCTest analysis class
    public void syncDeviceData(SCTestAnalysis.TestDataInterface testDataInterface1) {
        if (SCTestAnalysis.getInstance().testDataInterface != null) {
            SCTestAnalysis.getInstance().testDataInterface = null;
        }
        SCTestAnalysis.getInstance().testDataInterface = testDataInterface1;
    }

    private void getDarkSpectrum() {
        clearPreviousTestResulsArray();
        loadPixelArray();
        reprocessWavelength();
        prepareChartsDataForIntensity();
        isForDarkSpectrum = true;
        getIntensity();
    }

    private void loadPixelArray() {
        pixelXAxis = new ArrayList();
        pixelXAxis.clear();
        if (spectroDeviceObject.getImageSensor().getROI() != null) {
            int[] roiArray = spectroDeviceObject.getImageSensor().getROI();
            int pixelCount = roiArray[1];
            Log.e("pixelcount", "call" + pixelCount);

            for (int i = 1; i <= pixelCount; ++i) {
                pixelXAxis.add((float) i);
            }

            Log.e("pixelcountarray", "call" + pixelXAxis.toString());
        }

    }

    private void reprocessWavelength() {
        for (int i = 0; i < pixelXAxis.size(); ++i) {
            wavelengthXAxis.add(pixelXAxis.get(i));
        }

        Log.e("reprocessWavelength", "call" + wavelengthXAxis.toString());
        wavelengthXAxis.clear();
        if (spectroDeviceObject.getWavelengthCalibration() != null) {
            double[] resultArray = spectroDeviceObject.getWavelengthCalibration().getCoefficients();
            DataPoint[] theData = new DataPoint[0];
            PolynomialRegression poly = new PolynomialRegression(theData, spectroDeviceObject.getWavelengthCalibration().getNoOfCoefficients());
            poly.fillMatrix();

            for (int index = 0; index < pixelXAxis.size(); ++index) {
                Double d = poly.predictY(resultArray, (double) (Float) pixelXAxis.get(index)) * 100.0D / 100.0D;
                wavelengthXAxis.add(d.floatValue());
            }
        }

    }

    private void prepareChartsDataForIntensity() {
        intensityArray.clear();
        intensityChartsArray.clear();
        if (spectroDeviceObject.getRCTable() != null) {
            Iterator var1 = spectroDeviceObject.getRCTable().iterator();

            while (var1.hasNext()) {
                RCTableData objRc = (RCTableData) var1.next();
                IntensityChart objIntensity = new IntensityChart();
                objIntensity.setTestName(objRc.getTestItem());
                Log.e("ssss", "" + objIntensity.getTestName());
                objIntensity.setPixelMode(true);
                objIntensity.setOriginalMode(true);
                objIntensity.setAutoMode(true);
                objIntensity.setxAxisArray(pixelXAxis);
                objIntensity.setyAxisArray((ArrayList) null);
                objIntensity.setSubstratedArray((ArrayList) null);
                objIntensity.setWavelengthArray(wavelengthXAxis);
                objIntensity.setCriticalWavelength(objRc.getCriticalwavelength());
                intensityChartsArray.add(objIntensity);
            }
        }

        IntensityChart objSWIntensity = new IntensityChart();
        objSWIntensity.setTestName(standardWhiteTitle);
        objSWIntensity.setPixelMode(true);
        objSWIntensity.setOriginalMode(true);
        objSWIntensity.setAutoMode(true);
        objSWIntensity.setxAxisArray(pixelXAxis);
        objSWIntensity.setyAxisArray((ArrayList) null);
        objSWIntensity.setSubstratedArray((ArrayList) null);
        objSWIntensity.setWavelengthArray(wavelengthXAxis);
        objSWIntensity.setCriticalWavelength(0.0D);
        intensityChartsArray.add(objSWIntensity);

        IntensityChart objDarkIntensity = new IntensityChart();
        objDarkIntensity.setTestName(darkSpectrumTitle);
        objDarkIntensity.setPixelMode(true);
        objDarkIntensity.setOriginalMode(true);
        objDarkIntensity.setAutoMode(true);
        objDarkIntensity.setxAxisArray(pixelXAxis);
        objDarkIntensity.setyAxisArray((ArrayList) null);
        objDarkIntensity.setSubstratedArray((ArrayList) null);
        objDarkIntensity.setWavelengthArray(wavelengthXAxis);
        objDarkIntensity.setCriticalWavelength(0.0D);
        intensityChartsArray.add(objDarkIntensity);
    }

    public void clearPreviousTestResulsArray() {
        intensityChartsArray.clear();
        reflectenceChartsArray.clear();
        concentrationArray.clear();
        stripNumber = -1;
        setDefaultValues();
    }
    private void setDefaultValues(){
        isForDarkSpectrum=false;
        isFromWhiteSpectrum=false;
        isEjectType=false;
        isCalibration=false;
    }

    private void performMotorStepsFunction() {
        Log.e("stripnumberandsize", "call" + stripNumber + "cal" + intensityChartsArray.size());
        if (stripNumber < motorSteps.size()) {
            motorStepsControl(motorSteps.get(stripNumber));
        }
    }

    private void motorStepsControl(Steps motorObject) {
        String direction = Commands.MOVE_STRIP_COUNTER_CLOCKWISE_TAG;
        if (motorObject.getDirection().equals("CW")) {
            direction = Commands.MOVE_STRIP_CLOCKWISE_TAG;
        }
        SCConnectionHelper.getInstance().prepareCommandForMotorMove(motorObject.getNoOfSteps(), direction);
    }

    public void ejectStripCommand() {
        final String ejectCommand = "$MRS900#";
        Log.e("ejectStripCommand", "call" + ejectCommand);
        if (SCConnectionHelper.getInstance().isConnected) {
            isEjectType = true;
            ledControl(false);
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    SCTestAnalysis.getInstance().sendString(ejectCommand);
                }
            }, 1000);
        }
    }

    private void getIntensity() {
        (new Timer()).schedule(new TimerTask() {
            public void run() {
                requestCommand = Commands.INTESITY_VALUES_TAG;
                SCTestAnalysis.getInstance().sendString(requestCommand);
            }
        }, 1000L);
    }

    private void ledControl(boolean isOn) {
        SCConnectionHelper.getInstance().prepareCommandForUV(isOn);
    }

    public void socketDidReceiveMessage(String response, String request) {
        Log.e("datacount", "call" + response + "request" + request);
        if (qubeTestDataInterface != null) {
            qubeTestDataInterface.getRequestAndResponse(response);
        }

        receivedDataString = receivedDataString + response;
        if (receivedDataString.toUpperCase().startsWith("^ERR#") || receivedDataString.toUpperCase().startsWith("$ERR#")) {
            Log.e("^ERR#DataRecieved", "call");
            dataRecieved(receivedDataString, request);
            receivedDataString = "";
        }

        if (receivedDataString.toUpperCase().startsWith("$OK#") || receivedDataString.toUpperCase().startsWith("$OK!") || receivedDataString.toUpperCase().startsWith("^OK#")) {
            Log.e("$OK# Data Recieved", "call");
            dataRecieved(receivedDataString, request);
            receivedDataString = "";
        }

        if (receivedDataString.toUpperCase().startsWith("$#")) {
            receivedDataString = "";
        }

        if (receivedDataString.toUpperCase().startsWith("^POS#") || receivedDataString.toUpperCase().startsWith("$POS#")) {
            dataRecieved(receivedDataString, request);
            receivedDataString = "";
        }

        if (receivedDataString.toUpperCase().startsWith("^STP#") || receivedDataString.toUpperCase().startsWith("$STP#")) {
            dataRecieved(receivedDataString, request);
            receivedDataString = "";
        }

        if (receivedDataString.toUpperCase().startsWith("^288$") && receivedDataString.toUpperCase().endsWith("^EOF#")) {
            Log.e("receivedDataStringcall", "call" + receivedDataString);
            intensityDataRecieved(receivedDataString, request);
            receivedDataString = "";
        }

    }

    private void intensityDataRecieved(String responseData, String request) {
        Log.e("intensityDataRecieved", "call" + request + responseData.length());
        if (processIntensityValues(responseData)) {
            if (!isForDarkSpectrum && !isCalibration) {
                stripNumber = stripNumber + 1;
                performMotorStepsFunction();
            } else if (isFromWhiteSpectrum) {
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        ejectStripCommand();
                    }
                }, 1000);
            } else {
                isForDarkSpectrum = false;
                Log.e("gettingdark", "called");// getting darkspectrum completed , so for white spectrum need to on led
                isCalibration = true;
                if (qubeTestDataInterface != null) {
                    qubeTestDataInterface.gettingDarkSpectrum(true, darkSpectrumIntensityArray);
                    syncDone();
                }
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        requestCommand = Commands.UV_TURN_ON;
                        ledControl(true);
                    }
                }, 1000);
            }
        } else {
            requestCommand = "";
            (new Timer()).schedule(new TimerTask() {
                public void run() {
                    getIntensity();
                }
            }, 1000L);
        }

    }

    private void dataRecieved(String responseData, String request) {
        Log.e("dataRecieved", "call" + request + responseData);
        processResponseData(request, responseData);
    }

    private void processResponseData(String command, String response) {
        Log.e("DeviceData", "CalledResponse" + command + ":" + response);
        if (response.contains("OK")) {
            if (command.equals(Commands.UV_TURN_ON)) {
                if (isCalibration) {
                    Log.e("isCalibrationUV_TURN_ON", "call");
                    // getting whitespectrum need to perform motorsteps upt0 75 * 11 steps for all 10 test items.
                    (new Timer()).schedule(new TimerTask() {
                        public void run() {
                            SCConnectionHelper.getInstance().prepareCommandForMotorMove(825, "MLS");
                        }
                    }, 1000L);
                } else {
                    Log.e("UV_TURN_ON", "call");
                    (new Timer()).schedule(new TimerTask() {
                        public void run() {
                            //step 3 for getting T intencity
                            stripNumber = 0;
                            motorStepsControl(motorSteps.get(stripNumber));
                        }
                    }, 1000L);
                }
            } else if (command.equals(Commands.UV_TURN_OFF)) {
                if (isCalibration) {
                    Log.e("isCalibrationTURN_OFF", "call");
                } else {
                    if (qubeTestDataInterface != null) {
                        qubeTestDataInterface.onSuccessForTestComplete(null, "test Completed", intensityChartsArray);
                    }
                }
            }
        } else if (response.contains("POS")) {
            (new Timer()).schedule(new TimerTask() {
                public void run() {
                    ledControl(true);
                }
            }, 1000L);
        } else if (response.contains("STP")) {
            Log.e("isCalibration", "call" + isCalibration);
            if (isCalibration) {   // for calibration function
                Log.e("stpcalibration", "call");
                if (isEjectType) {
                    setDefaultValues();
                    if (qubeTestDataInterface != null) {
                        qubeTestDataInterface.isSyncingCompleted(true, standardWhiteIntensityArray);
                        syncDone();
                    }
                } else {
                    (new Timer()).schedule(new TimerTask() {
                        public void run() {
                            getIntensity();
                        }
                    }, 1000L);
                }
            } else { //for motorsteps for testing
                if(isFromStripTryOut){
                    isFromStripTryOut=false;
                    if (qubeTestDataInterface != null) {
                        qubeTestDataInterface.onMovingToStripTrayOut("");
                    }
                    Log.e("isFromStripTryOut", "call" + isFromStripTryOut);
                }else if (stripNumber == -1) {
                    Log.e("waitfor20min", "call");
                    (new Timer()).schedule(new TimerTask() {
                        public void run() {
                            requestCommand = Commands.UV_TURN_ON;
                            ledControl(true);
                        }
                    }, 20 * (60*1000));
                } else if (stripNumber != motorSteps.size() - 1) {
                    Log.e("Strip Number:", "" + stripNumber);
                    (new Timer()).schedule(new TimerTask() {
                        public void run() {
                            requestCommand = "";
                            getIntensity();
                        }
                    }, 1000L);
                } else{ // if testing completed
                    Log.e("stripnumber0called", "call");
                    stripNumber = -1;
                    requestCommand = Commands.UV_TURN_OFF;
                    ledControl(false);
                }
            }
        } else if (response.contains("ERR")) {
            Log.e("ERRdetected", "call");
        }
    }

    private boolean processIntensityValues(String response) {
        Log.e("processIntensityValues", "calling" + response.length());
        hexaDecimalArray = new ArrayList();
        intensityArray = new ArrayList();
        new ArrayList();
        String[] intensity = null;
        intensity = response.split(",");
        ArrayList<String> stringList = new ArrayList(Arrays.asList(intensity));
        stringList.remove(0);
        stringList.remove(stringList.size() - 1);
        ArrayList<String> intensityArray1 = stringList;
        if (stringList.size() != pixelXAxis.size()) {
            Log.e("intensityDatamismatched", "call" + pixelXAxis.size());
            return false;
        } else {
            intensityArray.clear();
            intensityArray = new ArrayList(stringList.size());

            int position;
            for (position = 0; position < intensityArray1.size(); ++position) {
                Float number = Float.valueOf((String) intensityArray1.get(position));
                intensityArray.add(number);
            }

            if (isForDarkSpectrum) {
                darkSpectrumIntensityArray = intensityArray;
                if (getPositionForTilte(darkSpectrumTitle) != -1) {
                    position = getPositionForTilte(darkSpectrumTitle);
                    IntensityChart object = (IntensityChart) intensityChartsArray.get(position);
                    object.setyAxisArray(darkSpectrumIntensityArray);
                    intensityChartsArray.set(position, object);
                }
            } else if (isCalibration) {
                standardWhiteIntensityArray = intensityArray;
                if (getPositionForTilte(standardWhiteTitle) != -1) {
                    int position1 = getPositionForTilte(standardWhiteTitle);
                    IntensityChart object = intensityChartsArray.get(position1);
                    object.setyAxisArray(standardWhiteIntensityArray);
                    intensityChartsArray.set(position1, object);
                    isFromWhiteSpectrum = true;
                    Log.e("forwhitespectrum", "call" + object.getyAxisArray().toString());
                }
            } else {
                setIntensityArrayForTestItem();
                Log.e("lastpostion", "call" + ((Steps) motorSteps.get(stripNumber)).getTestName());
                if (((Steps) motorSteps.get(stripNumber)).getTestName().contains("Eject")) {
                    Log.e("mrs850", "call");
                }
                if (stripNumber == motorSteps.size() - 1) {
                    Log.e("testCompleted methods", "call");
                    for (int i = 0; i < intensityChartsArray.size(); i++) {
                        IntensityChart obj = intensityChartsArray.get(i);
                        Log.e("finatest", "call" + obj.getTestName() + obj.getyAxisArray().toString());
                    }
                }
            }
            return true;
        }
    }

    public void clearCache() {
        receivedDataString = "";
    }

    private void syncDone() {
        Log.e("syncDone", "call");
        clearCache();
    }

    private void setIntensityArrayForTestItem() {
        Steps currentObject = (Steps) motorSteps.get(stripNumber);
        Log.e("setIntensityForTestItem", "call" + currentObject.getStandardWhiteIndex());
        Log.e("setIntensitydark", "call" + darkSpectrumIntensityArray.toString());
        Log.e("IntIntensity", "call" + intensityArray.toString());

        int i;
        IntensityChart object;
        if (currentObject.getStandardWhiteIndex() == 0) {
            for (i = 0; i < intensityChartsArray.size(); ++i) {
                object = (IntensityChart) intensityChartsArray.get(i);
                if (object.getTestName().equals(currentObject.getTestName())) {
                    object.setyAxisArray(intensityArray);
                    object.setSubstratedArray(getSubstratedArray(intensityArray, darkSpectrumIntensityArray));
                    intensityChartsArray.set(i, object);
                    Log.e("darkIntensityArray", "call" + darkSpectrumIntensityArray.toString());

                }
            }
        } else {
            standardWhiteIntensityArray = intensityArray;
            i = getPositionForTilte(standardWhiteTitle);
            object = (IntensityChart) intensityChartsArray.get(i);
            object.setyAxisArray(standardWhiteIntensityArray);
            object.setSubstratedArray(getSubstratedArray(standardWhiteIntensityArray, darkSpectrumIntensityArray));
            intensityChartsArray.set(i, object);
            Log.e("WhiteIntensityArray", "call" + standardWhiteIntensityArray.toString());
        }

        if (stripNumber == motorSteps.size() - 1) {
            Log.e("testingended", "call");
        }
    }

    private ArrayList<Float> getSubstratedArray(ArrayList<Float> spectrumIntensityArray, ArrayList<Float> darkSpectrumIntensityArray) {
        ArrayList<Float> substratedArray = new ArrayList();

        for (int i = 0; i < spectrumIntensityArray.size(); ++i) {
            substratedArray.add((Float) spectrumIntensityArray.get(i) - (Float) darkSpectrumIntensityArray.get(i));
        }

        Log.e("substratedArray", "call" + substratedArray.toString());
        return substratedArray;
    }

    private int getPositionForTilte(String title) {
        for (int i = 0; i < intensityChartsArray.size(); ++i) {
            IntensityChart object = (IntensityChart) intensityChartsArray.get(i);
            if (object.getTestName().equals(title)) {
                return i;
            }
        }
        return -1;
    }

    private String bytesToHexString(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];

        for (int j = 0; j < bytes.length; ++j) {
            int v = bytes[j] & 255;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 15];
        }

        return new String(hexChars);
    }

    private float hex2decimal(String s) {
        String digits = "0123456789ABCDEF";
        s = s.toUpperCase();
        int val = 0;

        for (int i = 0; i < s.length(); ++i) {
            char c = s.charAt(i);
            int d = digits.indexOf(c);
            val = 16 * val + d;
        }

        return (float) val;
    }

    private Charset UTF8_CHARSET = Charset.forName("UTF-8");
    private final char[] hexArray = "0123456789ABCDEF".toCharArray();

    private String decodeUTF8(byte[] bytes) {
        return new String(bytes, UTF8_CHARSET);
    }

    private byte[] encodeUTF8(String string) {
        return string.getBytes(UTF8_CHARSET);
    }

    public void syncQUBEDeviceData(QUBETestDataInterface testDataInterface1) {
        if (qubeTestDataInterface != null) {
            qubeTestDataInterface = null;
        }

        qubeTestDataInterface = testDataInterface1;
    }

    public void gettingDarkSpectrum() {
        isForDarkSpectrum = true;
        Log.e("getstatus", "call" + SCConnectionHelper.getInstance().isConnected);
        if (SCConnectionHelper.getInstance().isConnected) {
            getDarkSpectrum();
        }

    }

    public interface QUBETestDataInterface {
        void gettingData(byte[] var1);

        void onSuccessForTestComplete(ArrayList<TestFactors> var1, String var2, ArrayList<IntensityChart> var3);

        void getRequestAndResponse(String var1);

        void onFailureForTesting(String var1);

        void onMovingToStripTrayOut(String var1);

        void isSyncingCompleted(boolean val, ArrayList<Float> standardWhiteIntensityArray);

        void gettingDarkSpectrum(boolean val, ArrayList<Float> standardWhiteIntensityArray);
    }
    //step1
    public void performStripTrayOutCommand() {
        isFromStripTryOut=true;
        SCConnectionHelper.getInstance().prepareCommandForMotorMove(120, "MLS");//for CCW-use MLS
    }
    //step2
    public void startTesting() {
        stripNumber = -1;
        SCConnectionHelper.getInstance().prepareCommandForMotorMove(250, "MRS");//for CW -use MRS
    }

    // for calibration
    public void loadPreviousDarkAndSWArrays(ArrayList<Float> darkArray, ArrayList<Float> swArray) {
        darkSpectrumIntensityArray = darkArray;
        standardWhiteIntensityArray = swArray;
        loadPixelAndWaveLengthArrays(darkSpectrumIntensityArray, standardWhiteIntensityArray);
        Log.e("defalultdark", "call" + darkArray.toString());
        Log.e("defalultwhite", "call" + swArray.toString());

    }

    public void loadPixelAndWaveLengthArrays(ArrayList<Float> dark, ArrayList<Float> sw) {
        clearPreviousTestResulsArray();
        loadPixelArray();
        reprocessWavelength();
        prepareBeforeChartsDataForIntensity(dark, sw);
    }

    private void prepareBeforeChartsDataForIntensity(ArrayList<Float> dark, ArrayList<Float> sw) {
        intensityArray.clear();
        intensityChartsArray.clear();
        if (spectroDeviceObject.getRCTable() != null) {
            for (RCTableData objRc : spectroDeviceObject.getRCTable()) {
                IntensityChart objIntensity = new IntensityChart();
                objIntensity.setTestName(objRc.getTestItem());
                Log.e("ssss", "" + objIntensity.getTestName());
                objIntensity.setPixelMode(true);
                objIntensity.setOriginalMode(true);
                objIntensity.setAutoMode(true);
                objIntensity.setxAxisArray(pixelXAxis);
                objIntensity.setyAxisArray(null);
                objIntensity.setSubstratedArray(null);
                objIntensity.setWavelengthArray(wavelengthXAxis);
                objIntensity.setCriticalWavelength(objRc.getCriticalwavelength());
                intensityChartsArray.add(objIntensity);

            }
        }
        // If needed to show dark spectrum and Standard White spectrum Then use below methods

        IntensityChart objSWIntensity = new IntensityChart();
        objSWIntensity.setTestName(standardWhiteTitle);
        objSWIntensity.setPixelMode(true);
        objSWIntensity.setOriginalMode(true);
        objSWIntensity.setAutoMode(true);
        objSWIntensity.setxAxisArray(pixelXAxis);
        objSWIntensity.setyAxisArray(sw);
        objSWIntensity.setSubstratedArray(null);
        objSWIntensity.setWavelengthArray(wavelengthXAxis);
        objSWIntensity.setCriticalWavelength(0.0);
        intensityChartsArray.add(objSWIntensity);

        IntensityChart objDarkIntensity = new IntensityChart();
        objDarkIntensity.setTestName(darkSpectrumTitle);
        objDarkIntensity.setPixelMode(true);
        objDarkIntensity.setOriginalMode(true);
        objDarkIntensity.setAutoMode(true);
        objDarkIntensity.setxAxisArray(pixelXAxis);
        objDarkIntensity.setyAxisArray(dark);
        objDarkIntensity.setSubstratedArray(null);
        objDarkIntensity.setWavelengthArray(wavelengthXAxis);
        objDarkIntensity.setCriticalWavelength(0.0);
        intensityChartsArray.add(objDarkIntensity);
        Log.e("ssss", "" + intensityChartsArray.size());

    }
}
