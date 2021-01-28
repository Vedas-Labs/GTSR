package com.gtsr.gtsr.dataController;

import java.util.HashMap;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gtsr.gtsr.Constants;
import com.gtsr.gtsr.LanguageServerObjects;
import com.gtsr.gtsr.ServerApisInterface;
import com.gtsr.gtsr.database.DataBaseHelper;

import org.json.JSONObject;

import java.util.Iterator;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Rise on 30/11/2017.
 */


public class LanguageTextController {

    public HashMap<String, String> currentLanguageDictionary = new HashMap<String, String>();
    public HashMap<String, HashMap> allLanguageDictionary = new HashMap<>();

    public static LanguageTextController myObj;
    public Context context;
    public DataBaseHelper helper;

    public static LanguageTextController getInstance() {
        if (myObj == null) {
            myObj = new LanguageTextController();

        }
        return myObj;
    }
    public void fillCOntext(Context context1){
        context=context1;
        myObj.allLanguageDictionary = myObj.loadLanguagesMap();
        Log.e("DBStatus", "Fill Context Called");
        helper = new DataBaseHelper(context1);

    }


    public void getLanguages() {
        Log.e("executeLanguageAPI", "call");
        //Creating a retrofit object
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ServerApisInterface.home_URL)
                .addConverterFactory(GsonConverterFactory.create()) //Here we are using the GsonConverterFactory to directly convert json data to object
                .build();

        //creating the api interface
        ServerApisInterface api = retrofit.create(ServerApisInterface.class);
        Log.e("ServerApisInterface", "call");
        Call<LanguageServerObjects> call = api.getLanguages();
        Log.e("getLanguages", "call" + api.getLanguages().isExecuted());
        call.enqueue(new Callback<LanguageServerObjects>() {
            @Override
            public void onResponse(Call<LanguageServerObjects> call, Response<LanguageServerObjects> response1) {
                if (response1.code() == 200) {
                    // Do awesome stuff

                    allLanguageDictionary = response1.body().getLanguages();
                    Log.e("All", "" + allLanguageDictionary.size());
                    Log.e("French", "" + allLanguageDictionary.get("French").size());
                    Log.e("Simplified", "" + allLanguageDictionary.get(com.gtsr.gtsr.Constants.AppLanguages.Simplified.toString()).size());
                    Log.e("Traditional", "" + allLanguageDictionary.get(com.gtsr.gtsr.Constants.AppLanguages.Traditional.toString()).size());
                    String currentLanguageKey = getCurrentSystemLanguage();
                    currentLanguageDictionary = allLanguageDictionary.get(currentLanguageKey);
                    Log.e("currentLanguage", "" + currentLanguageKey);
                    Log.e("currentLanguage", "" + currentLanguageDictionary.size());
                    saveLanguagesMap(allLanguageDictionary);
                  //  EventBus.getDefault().post(new SideMenuViewController.MessageEvent("LanguageAPIResponseSuccess"));
                } else {
                }
            }

            @Override
            public void onFailure(Call<LanguageServerObjects> call, Throwable t) {
              //  EventBus.getDefault().post(new SideMenuViewController.MessageEvent("LanguageAPIResponseFailure"));
            }
        });
    }


    public void loadLanguageTexts() {

        // If no previous Dictionary Avaialbles
        if (allLanguageDictionary.size() == 0) {
            loadEnglishText();

        } else  // If  previous Dictionary Avaialbles
        {
            String currentLangaugeKey = null;

            /*// If user had prefered language select that language.
            if (UserDataController.getInstance().currentUser != null) {
                Log.e("preferLang", "" + UserDataController.getInstance().currentUser.getPreferedLanguage());
                Log.e("preferLang1", "" + allLanguageDictionary);

                currentLangaugeKey = UserDataController.getInstance().currentUser.getPreferedLanguage();
            } else // Else load system language.
            {

            }*/
            currentLangaugeKey = getCurrentSystemLanguage();

            if (LanguageTextController.getInstance().allLanguageDictionary.get(currentLangaugeKey) != null) {
                currentLanguageDictionary = LanguageTextController.getInstance().allLanguageDictionary.get(currentLangaugeKey);

            } else {

                if (LanguageTextController.getInstance().allLanguageDictionary.get(com.gtsr.gtsr.Constants.AppLanguages.English) != null) {
                    currentLanguageDictionary = LanguageTextController.getInstance().allLanguageDictionary.get(Constants.AppLanguages.English);

                } else {
                    loadEnglishText();
                }
            }

        }
        Log.e("preferLangCurrent", "" + currentLanguageDictionary);

    }

    public String getCurrentSystemLanguage() {
        String language = null;
        language = Locale.getDefault().getLanguage() + "_" + Locale.getDefault().getCountry();
        Log.e("getSystemLanguage", "" + language);

        if (language.equals("zh_CN")) {
            Log.e("CurrentLanguage", "" + language);

            return com.gtsr.gtsr.Constants.AppLanguages.Simplified.toString();
        } else if (language.equals("zh_TW")) {
            Log.e("CurrentLanguage", "" + language);

            return com.gtsr.gtsr.Constants.AppLanguages.Traditional.toString();

        } else if (language.equals("en")) {
            Log.e("Language", "call" + language);
            return com.gtsr.gtsr.Constants.AppLanguages.English.toString();

        } else if (language.equals("fr_FR")) {
            Log.e("Language", "call" + language);
            return com.gtsr.gtsr.Constants.AppLanguages.French.toString();

        } else if (language.equals("pt_PT")) {//Portuguese
            Log.e("Language", "call" + language);
            return com.gtsr.gtsr.Constants.AppLanguages.Portuguese.toString();

        } else {

            return com.gtsr.gtsr.Constants.AppLanguages.English.toString();
        }
    }


    public void loadEnglishText() {

        currentLanguageDictionary.clear();

        currentLanguageDictionary.put("languageEnglishName", "English");
        currentLanguageDictionary.put("languageNativeName", "English");

        currentLanguageDictionary.put("Urine Test", "Urine Test");
        currentLanguageDictionary.put("Simply test your urine samples with SpectrumChips. Our app will take care of the rest.",
                "Simply test your urine samples with SpectrumChips. Our app will take care of the rest.");

        currentLanguageDictionary.put("GET STARTED", "GET STARTED");
        currentLanguageDictionary.put("Blood Test", "Blood Test");

        currentLanguageDictionary.put("Test your blood samples with SpectrumChips & We provide accurate results",
                "Test your blood samples with SpectrumChips & We provide accurate results");

        currentLanguageDictionary.put("LOGIN", "LOGIN");

        currentLanguageDictionary.put("To know more your health, test your saliva samples with SpectrumChips", "To know more your health, test your saliva samples with SpectrumChips");

        currentLanguageDictionary.put("E-mail/phone", "E-mail/phone");

        currentLanguageDictionary.put("password", "password");
        currentLanguageDictionary.put("Forgot Password?", "Forgot Password?");
        currentLanguageDictionary.put("Don't have account yet?", "Don't have account yet?");
        currentLanguageDictionary.put("Or", "Or");
        currentLanguageDictionary.put("SIGN UP", "SIGN UP");
        currentLanguageDictionary.put("Please enter a valid email", "Please enter a valid email");
        currentLanguageDictionary.put("No Internet connection", "No Internet connection");
        currentLanguageDictionary.put("This email has already been registered. Please enter your password to link with this account",
                "This email has already been registered. Please enter your password to link with this account");

        currentLanguageDictionary.put("connect with", "connect with");
        currentLanguageDictionary.put("Warning", "Warning");
        currentLanguageDictionary.put("Password must contain atleast one number and one public static Stringter and minimum of 8 characters length without space.", "Password must contain atleast one number and one public static Stringter and minimum of 8 characters length without space.");

        currentLanguageDictionary.put("Please enter your phone number or email", "Please enter your phone number or email");
        currentLanguageDictionary.put("OK", "OK");
        currentLanguageDictionary.put("Please enter your password", "Please enter your password");
        currentLanguageDictionary.put("Please enter a valid phone number", "Please enter a valid phone number");
        currentLanguageDictionary.put("Set your password", "Set your password");
        currentLanguageDictionary.put("Your email is already registered to social media account. Please use Forgot Password to create new password for the existed account.", "Your email is already registered to social media account. Please use Forgot Password to create new password for the existed account.");
        currentLanguageDictionary.put("Cancel", "Cancel");
        currentLanguageDictionary.put("Invalid email", "Invalid email");
        currentLanguageDictionary.put("Can't connect to Facebook", "Can't connect to Facebook");
        currentLanguageDictionary.put("Your Facebook account is not linked with email ID . Please either link your email ID with Facebook and try again or Sign up with us.",
                "Your Facebook account is not linked with email ID . Please either link your email ID with Facebook and try again or Sign up with us.");

        currentLanguageDictionary.put("Error", "Error");
        currentLanguageDictionary.put("The email address is invalid.", "The email address is invalid.");
        currentLanguageDictionary.put("Social media login details cannot be  stored successfully",
                "Social media login details cannot be  stored successfully");
        currentLanguageDictionary.put("REGISTER", "REGISTER");
        currentLanguageDictionary.put("Confirm Password", "Confirm Password");
        currentLanguageDictionary.put("SIGN UP", "SIGN UP");
        currentLanguageDictionary.put("Password must contain at least 1 number, 1 letter, 1 special characters, and minimum of 8 characters in length without space.",
                "Password must contain at least 1 number, 1 letter, 1 special characters, and minimum of 8 characters in length without space.");
        currentLanguageDictionary.put("Please enter the same password as above", "Please enter the same password as above");
        currentLanguageDictionary.put("Please enter confirm password", "Please enter confirm password");
        currentLanguageDictionary.put("No connection", "No connection");
        currentLanguageDictionary.put("connecting", "connecting");
        currentLanguageDictionary.put("Please enter a valid password", "Please enter a valid password");
        currentLanguageDictionary.put("Enter the verification code", "Enter the verification code");
        currentLanguageDictionary.put("Create password", "Create password");
        currentLanguageDictionary.put("Resend", "Resend");
        currentLanguageDictionary.put("Enter valid password", "Enter valid password");
        currentLanguageDictionary.put("SpectroCare", "SpectroCare");
        currentLanguageDictionary.put("Verify", "Verify");
        currentLanguageDictionary.put("Enter the authentication code", "Enter the authentication code");
        currentLanguageDictionary.put("FORGOT PASSWORD", "FORGOT PASSWORD");
        currentLanguageDictionary.put("E-mail / Phone", "E-mail / Phone");
        currentLanguageDictionary.put("GET PASSWORD", "GET PASSWORD");
        currentLanguageDictionary.put("Enter Verification code", "Enter Verification code");
        currentLanguageDictionary.put("New password", "New password");
        currentLanguageDictionary.put("CHANGE PASSWORD", "CHANGE PASSWORD");
        currentLanguageDictionary.put("Recent Results", "Recent Results");
        currentLanguageDictionary.put("Test Now", "Test Now");
        currentLanguageDictionary.put("Edit Profile", "Edit Profile");
        currentLanguageDictionary.put("Remove Member", "Remove Member");
        currentLanguageDictionary.put("Age", "Age");
        currentLanguageDictionary.put("confirm", "confirm");
        currentLanguageDictionary.put("Admin account can't be removed", "Admin account can't be removed");
        currentLanguageDictionary.put("Alert", "Alert");
        currentLanguageDictionary.put("Are you sure you want to remove this member?",
                "Are you sure you want to remove this member?");
        currentLanguageDictionary.put("Yes", "Yes");
        currentLanguageDictionary.put("Please check your internet connection", "Please check your internet connection");
        currentLanguageDictionary.put("No", "No");
        currentLanguageDictionary.put("Device Information", "Device Information");
        currentLanguageDictionary.put("Languages", "Languages");
        currentLanguageDictionary.put("Contact Us", "Contact Us");
        currentLanguageDictionary.put("About Us", "About Us");
        currentLanguageDictionary.put("Change Password", "Change Password");
        currentLanguageDictionary.put("Log Out", "Log Out");
        currentLanguageDictionary.put("Admin", "Admin");
        currentLanguageDictionary.put("Members", "Members");
        currentLanguageDictionary.put("Add Member", "Add Member");
        currentLanguageDictionary.put("Do you really want to log out?", "Do you really want to log out?");
        currentLanguageDictionary.put("Name", "Name");
        currentLanguageDictionary.put("E-mail", "E-mail");
        currentLanguageDictionary.put("Birthdate", "Birthdate");
        currentLanguageDictionary.put("Gender", "Gender");
        currentLanguageDictionary.put("Female", "Female");
        currentLanguageDictionary.put("Male", "Male");
        currentLanguageDictionary.put("Height", "Height");
        currentLanguageDictionary.put("Weight", "Weight");
        currentLanguageDictionary.put("Blood Group", "Blood Group");
        currentLanguageDictionary.put("Save and continue", "Save and continue");
        currentLanguageDictionary.put("Cm", "Cm");
        currentLanguageDictionary.put("Kg", "Kg");
        currentLanguageDictionary.put("Set Profile", "Set Profile");
        currentLanguageDictionary.put("feet", "feet");
        currentLanguageDictionary.put("in", "in");
        currentLanguageDictionary.put("Lbs", "Lbs");
        currentLanguageDictionary.put("Set Height", "Set Height");
        currentLanguageDictionary.put("Set Weight", "Set Weight");
        currentLanguageDictionary.put("Photo", "Photo");
        currentLanguageDictionary.put("Please select the source of photo", "Please select the source of photo");
        currentLanguageDictionary.put("Camera", "Camera");
        currentLanguageDictionary.put("Gallery", "Gallery");
        currentLanguageDictionary.put("Camera is not available", "Camera is not available");
        currentLanguageDictionary.put("Please choose your blood group", "Please choose your blood group");
        currentLanguageDictionary.put("Please choose your weight", "Please choose your weight");
        currentLanguageDictionary.put("Please choose your height", "Please choose your height");
        currentLanguageDictionary.put("Please choose date of birth", "Please choose date of birth");
        currentLanguageDictionary.put("Please enter email", "Please enter email");
        currentLanguageDictionary.put("Please enter your name", "Please enter your name");
        currentLanguageDictionary.put("Try Again", "Try Again");
        currentLanguageDictionary.put("Relationship", "Relationship");
        currentLanguageDictionary.put("Friend", "Friend");
        currentLanguageDictionary.put("Father", "Father");
        currentLanguageDictionary.put("Mother", "Mother");
        currentLanguageDictionary.put("Son", "Son");
        currentLanguageDictionary.put("Daughter", "Daughter");
        currentLanguageDictionary.put("Husband", "Husband");
        currentLanguageDictionary.put("Wife", "Wife");
        currentLanguageDictionary.put("Brother", "Brother");
        currentLanguageDictionary.put("Sister", "Sister");
        currentLanguageDictionary.put("Sibling", "Sibling");
        currentLanguageDictionary.put("Grand Father", "Grand Father");
        currentLanguageDictionary.put("Grand Mother", "Grand Mother");
        currentLanguageDictionary.put("Grandson", "Grandson");
        currentLanguageDictionary.put("Grand Daughter", "Grand Daughter");
        currentLanguageDictionary.put("Uncle", "Uncle");
        currentLanguageDictionary.put("Aunt", "Aunt");
        currentLanguageDictionary.put("Nephew", "Nephew");
        currentLanguageDictionary.put("Niece", "Niece");
        currentLanguageDictionary.put("Cousin", "Cousin");
        currentLanguageDictionary.put("Stepfather", "Stepfather");
        currentLanguageDictionary.put("Stepmother", "Stepmother");
        currentLanguageDictionary.put("Sister-in-law", "Sister-in-law");
        currentLanguageDictionary.put("Others", "Others");
        currentLanguageDictionary.put("Brother-in-law", "Brother-in-law");
        currentLanguageDictionary.put("Please choose the relationship", "Please choose the relationship");
        currentLanguageDictionary.put("Waiting for devices", "Waiting for devices");
        currentLanguageDictionary.put("Looking for FAMILIA", "Looking for FAMILIA");
        currentLanguageDictionary.put("FAMILIA", "FAMILIA");
        currentLanguageDictionary.put("Current  WiFi :", "Current  WiFi :");
        currentLanguageDictionary.put("FAMILIA has been connected", "FAMILIA has been connected");
        currentLanguageDictionary.put("Looking for SpectrumChips", "Looking for SpectrumChips");
        currentLanguageDictionary.put("Current  WiFi ", "Current  WiFi ");
        currentLanguageDictionary.put("Next", "Next");
        currentLanguageDictionary.put("SpectrumChips has been connected", "SpectrumChips has been connected");
        currentLanguageDictionary.put("Edit Device", "Edit Device");
        currentLanguageDictionary.put("How to connect to device", "How to connect to device");
        currentLanguageDictionary.put("Phone settings > Wifi  settings > Select the wifi from device",
                "Phone settings > Wifi  settings > Select the wifi from device");
        currentLanguageDictionary.put("Settings", "Settings");
        currentLanguageDictionary.put("Device Name", "Device Name");
        currentLanguageDictionary.put("Device ID", "Device ID");
        currentLanguageDictionary.put("Battery", "Battery");
        currentLanguageDictionary.put("Firmware Updates", "Firmware Updates");
        currentLanguageDictionary.put("Current Version:", "Current Version:");
        currentLanguageDictionary.put("Latest Version:", "Latest Version:");
        currentLanguageDictionary.put("Update", "Update");
        currentLanguageDictionary.put("Do you really want to update the firmware?",
                "Do you really want to update the firmware?");
        currentLanguageDictionary.put("Message", "Message");
        currentLanguageDictionary.put("Submit", "Submit");
        currentLanguageDictionary.put("Your message has been successfully sent. We will contact you very soon",
                "Your message has been successfully sent. We will contact you very soon");
        currentLanguageDictionary.put("Success", "Success");
        currentLanguageDictionary.put("Ok", "Ok");
        currentLanguageDictionary.put("Please select a test", "Please select a test");
        currentLanguageDictionary.put("is not currently available", "is not currently available");
        currentLanguageDictionary.put("Blood Test is not  currently available", "Blood Test is not  currently available");
        currentLanguageDictionary.put("Tear Test is not currently  available", "Tear Test is not currently  available");
        currentLanguageDictionary.put("Saliva Test is not  currently available", "Saliva Test is not  currently available");
        currentLanguageDictionary.put("Pregnancy Test is not currently  available",
                "Pregnancy Test is not currently  available");
        currentLanguageDictionary.put("Sperm Test is not  currently available", "Sperm Test is not  currently available");
        currentLanguageDictionary.put("Water Test is not  currently available", "Water Test is not  currently available");
        currentLanguageDictionary.put("START", "START");
        currentLanguageDictionary.put("Instruction", "Instruction");
        currentLanguageDictionary.put("Step 1", "Step 1");
        currentLanguageDictionary.put("Immerse the strip in urine for 1 to 2  seconds and lay the strip flat on a clean, dry and non-absorbent surface.",
                "Immerse the strip in urine for 1 to 2  seconds and lay the strip flat on a clean, dry and non-absorbent surface.");
        currentLanguageDictionary.put("Put the strip into the strip holder.", "Put the strip into the strip holder.");
        currentLanguageDictionary.put("SKIP", "SKIP");
        currentLanguageDictionary.put("Step 2", "Step 2");
        currentLanguageDictionary.put("Wait for 120 seconds for reaction to Complete",
                "Wait for 120 seconds for reaction to Complete");
        currentLanguageDictionary.put("if you already waited for 120 seconds", "if you already waited for 120 seconds");
        currentLanguageDictionary.put("Analyzing", "Analyzing");
        currentLanguageDictionary.put("Abort", "Abort");
        currentLanguageDictionary.put("Do you really want to stop analyzing?", "Do you really want to stop analyzing?");
        currentLanguageDictionary.put("Test Results", "Test Results");
        currentLanguageDictionary.put("Occult Blood", "Occult Blood");
        currentLanguageDictionary.put("Bilirubin", "Bilirubin");
        currentLanguageDictionary.put("Urobilinogen", "Urobilinogen");
        currentLanguageDictionary.put("Ketone", "Ketone");
        currentLanguageDictionary.put("Protein", "Protein");
        currentLanguageDictionary.put("Glucose", "Glucose");
        currentLanguageDictionary.put("PH", "PH");
        currentLanguageDictionary.put("Specific Gravity", "Specific Gravity");
        currentLanguageDictionary.put("Nitrite", "Nitrite");
        currentLanguageDictionary.put("LEUKOCYTES", "LEUKOCYTES");
        currentLanguageDictionary.put("Normal", "Normal");
        currentLanguageDictionary.put("Abnormal", "Abnormal");
        currentLanguageDictionary.put("Data is not available", "Data is not available");
        currentLanguageDictionary.put("Old Password", "Old Password");
        currentLanguageDictionary.put("Done", "Done");
        currentLanguageDictionary.put("Your password has been changed", "Your password has been changed");
        currentLanguageDictionary.put("New password must be different from the old one",
                "New password must be different from the old one");
        currentLanguageDictionary.put("Please enter a new password", "Please enter a new password");
        currentLanguageDictionary.put("Would you like to change the language to",
                "Would you like to change the language to");
        currentLanguageDictionary.put("ok", "ok");
        currentLanguageDictionary.put("Arabic", "Arabic");
        currentLanguageDictionary.put("Chinese (Traditional)", "Chinese (Traditional)");
        currentLanguageDictionary.put("Spanish", "Spanish");
        currentLanguageDictionary.put("Chinese (simplified)", "Chinese (simplified)");
        currentLanguageDictionary.put("English (India)", "English (India)");
        currentLanguageDictionary.put("English (UnitedStates)", "English (UnitedStates)");
        currentLanguageDictionary.put("French", "French");
        currentLanguageDictionary.put("German", "German");
        currentLanguageDictionary.put("Greek", "Greek");
        currentLanguageDictionary.put("Japanese", "Japanese");
        currentLanguageDictionary.put("Portuguese", "Portuguese");
        currentLanguageDictionary.put("Russian", "Russian");
        currentLanguageDictionary.put("My Device", "My Device");
        currentLanguageDictionary.put("Added Devices", "Added Devices");
        currentLanguageDictionary.put("NEXT", "NEXT");
        currentLanguageDictionary.put("Looking for available devices", "Looking for available devices");
        currentLanguageDictionary.put("Searchingâ€¦", "Searchingâ€¦");
        currentLanguageDictionary.put("Searching…", "Searching…");
        currentLanguageDictionary.put("Connect to Network", "Connect to Network");
        currentLanguageDictionary.put("Connect", "Connect");
        currentLanguageDictionary.put("Please Enter the Device Password", "Please Enter the Device Password");
        currentLanguageDictionary.put("Add Device", "Add Device");
        currentLanguageDictionary.put("Available Devices", "Available Devices");
        currentLanguageDictionary.put("Invalid credentials", "Invalid credentials");
        currentLanguageDictionary.put("No data found with this email or phone", "No data found with this email or phone");
        currentLanguageDictionary.put("The data has been stored successfully", "The data has been stored successfully");
        currentLanguageDictionary.put("Your personal information has been successfully updated.",
                "Your personal information has been successfully updated.");
        currentLanguageDictionary.put("Your message  has been sent successfully.",
                "Your message  has been sent successfully.");
        currentLanguageDictionary.put("Your registration was successful!", "Your registration was successful!");
        currentLanguageDictionary.put("The phone/email is already registered.", "The phone/email is already registered.");
        currentLanguageDictionary.put("You have already logged from your social-media account",
                "You have already logged from your social-media account");
        currentLanguageDictionary.put("Your account has been successfully verified",
                "Your account has been successfully verified");
        currentLanguageDictionary.put("Your OTP got expired", "Your OTP got expired");
        currentLanguageDictionary.put("Invalid OTP", "Invalid OTP");
        currentLanguageDictionary.put("The username or password is incorrect", "The username or password is incorrect");
        currentLanguageDictionary.put("No data found. Please register with us", "No data found. Please register with us");
        currentLanguageDictionary.put("You have not yet set your password. Please set your password",
                "You have not yet set your password. Please set your password");
        currentLanguageDictionary.put("No data found with this username", "No data found with this username");
        currentLanguageDictionary.put("Please verify your account", "Please verify your account");
        currentLanguageDictionary.put("You have already set this password, please enter a new password",
                "You have already set this password, please enter a new password");
        currentLanguageDictionary.put("The new password has been set successfully, please login again",
                "The new password has been set successfully, please login again");
        currentLanguageDictionary.put("The password you entered is incorrect",
                "The password you entered is incorrect");
        currentLanguageDictionary.put("Your personal information has been successfully stored.",
                "Your personal information has been successfully stored.");
        currentLanguageDictionary.put("Your personal information has been successfully updated",
                "Your personal information has been successfully updated");
        currentLanguageDictionary.put("Your relationship information has been successfully stored.",
                "Your relationship information has been successfully stored.");
        currentLanguageDictionary.put("Your relationship information has been successfully updated.",
                "Your relationship information has been successfully updated.");
        currentLanguageDictionary.put("Your relationship information has been successfully deleted.",
                "Your relationship information has been successfully deleted.");
        currentLanguageDictionary.put("Your device details has been successfully stored.",
                "Your device details has been successfully stored.");
        currentLanguageDictionary.put("Your device details has been successfully updated.",
                "Your device details has been successfully updated.");
        currentLanguageDictionary.put("Your device details has been deleted successfully.",
                "Your device details has been deleted successfully.");
        currentLanguageDictionary.put("Your test results  has been successfully stored.",
                "Your test results  has been successfully stored.");
        currentLanguageDictionary.put("Your test results  has been successfully deleted.",
                "Your test results  has been successfully deleted.");
        currentLanguageDictionary.put("SpectrumChips", "SpectrumChips");
        currentLanguageDictionary.put("Link Accounts", "Link Accounts");
        currentLanguageDictionary.put("This Mail has already Spectrocare account.Enter your Spectrocare password to link with this account",
                "This Mail has already Spectrocare account.Enter your Spectrocare password to link with this account");
        /////////////////////////////////////////////////////
        currentLanguageDictionary.put("This email has already been registered.Please enter your password to link with this account", "This email has already been registered.Please enter your password to link with this account");
        currentLanguageDictionary.put("Occult blood means microscopic blood and should be negative in your urine.The causes of positive test result can be associated with kidney infections or kidney stones.Sometimes, the test is a false positive which may be caused by dehydration and excessive exercise.Please consult with your doctors for more information.", "Occult blood means microscopic blood and should be negative in your urine.The causes of positive test result can be associated with kidney infections or kidney stones.Sometimes, the test is a false positive which may be caused by dehydration and excessive exercise.Please consult with your doctors for more information.");
        currentLanguageDictionary.put("Bilirubin is a waste product that is produced by the liver from body normal circulation, and should be found negative in your urine.The presence of bilirubin in urine is an early indicator of liver disease and can occur before clinical symptoms appear.Please consult with your doctors for more information.", "Bilirubin is a waste product that is produced by the liver from body normal circulation, and should be found negative in your urine.The presence of bilirubin in urine is an early indicator of liver disease and can occur before clinical symptoms appear.Please consult with your doctors for more information.");
        currentLanguageDictionary.put("Urobilinogen is formed from the reduction of bilirubin.Normal amount can be found in pet 's urine, but positive test results may indicate liver diseases such as viral hepatitis, cirrhosis, while little amount or absence of urobilinogen is associated with hepatic obstruction. Please consult with your doctors for more information.", "Urobilinogen is formed from the reduction of bilirubin.Normal amount can be found in pet 's urine, but positive test results may indicate liver diseases such as viral hepatitis, cirrhosis, while little amount or absence of urobilinogen is associated with hepatic obstruction. Please consult with your doctors for more information.");
        currentLanguageDictionary.put("Ketones are intermediate products of fat metabolism and should be negative in your urine.High ketone levels in urine may indicate diabetes and can be a medical emergency.Please consult with your doctors for more information.", "Ketones are intermediate products of fat metabolism and should be negative in your urine.High ketone levels in urine may indicate diabetes and can be a medical emergency.Please consult with your doctors for more information.");

        currentLanguageDictionary.put("Proteins are substances that are normally found in blood, and should be negative in your urine.A large amount of protein in urine may indicate kidney disease,while a small amount is normal.Please consult with your doctors for more information.","Proteins are substances that are normally found in blood, and should be negative in your urine.A large amount of protein in urine may indicate kidney disease,while a small amount is normal.Please consult with your doctors for more information.");
        currentLanguageDictionary.put("Glucose is a type of sugar that should be negative in your urine.Positive test result indicates a high glucose level in your blood and may be a sign of diabetes.Please consult with your doctors for more information.", "Glucose is a type of sugar that should be negative in your urine.Positive test result indicates a high glucose level in your blood and may be a sign of diabetes.Please consult with your doctors for more information.");
        currentLanguageDictionary.put("PH is a reading of how acidic or alkaline your urine is.PH of 5.0 to 8.0 is normal range.PH 6.5 is considered neutral.A number less than 5 may be a sign of diabetes, while a number greater than 8 may be caused by urinary tract infection(UTI) or kidney infection.Please consult with your doctors for more information.", "PH is a reading of how acidic or alkaline your urine is.PH of 5.0 to 8.0 is normal range.PH 6.5 is considered neutral.A number less than 5 may be a sign of diabetes, while a number greater than 8 may be caused by urinary tract infection(UTI) or kidney infection.Please consult with your doctors for more information.");
        currentLanguageDictionary.put("Specific gravity is a measure of urine concentration, which normal range is 1.005 - 1.030.Several medical conditions can cause the urine SG to be abnormal: dehydration, diabetes, adrenal gland disease, thyroid disease, or kidney disease.Please consult with your doctors for more information.", "Specific gravity is a measure of urine concentration, which normal range is 1.005 - 1.030.Several medical conditions can cause the urine SG to be abnormal: dehydration, diabetes, adrenal gland disease, thyroid disease, or kidney disease.Please consult with your doctors for more information.");
        currentLanguageDictionary.put("Nitrite is a substance produced by certain bacteria and should be negative in your urine.A positive test result can be a sign of bacterial urinary tract infection(UTI).The test result should be considered along with the leukocyte esterase.Please consult with your doctors for more information.", "Nitrite is a substance produced by certain bacteria and should be negative in your urine.A positive test result can be a sign of bacterial urinary tract infection(UTI).The test result should be considered along with the leukocyte esterase.Please consult with your doctors for more information.");
        currentLanguageDictionary.put("Leukocyte esterase is an enzyme present in most white blood cells(WBCs) and should be negative in your urine.A positive test result indicates the increase of WBCs in urine which may be caused by the inflammation in the urinary tract or kidneys.Results can be considered along with Nitrite.Please consult with your doctors for more information.", "Leukocyte esterase is an enzyme present in most white blood cells(WBCs) and should be negative in your urine.A positive test result indicates the increase of WBCs in urine which may be caused by the inflammation in the urinary tract or kidneys.Results can be considered along with Nitrite.Please consult with your doctors for more information.");
        currentLanguageDictionary.put("No data found with this email", "No data found with this email");
        currentLanguageDictionary.put("Me", "Me");
        currentLanguageDictionary.put("Set date of birth", "Set date of birth");
        currentLanguageDictionary.put("Delete Test Results", "Delete Test Results");
        currentLanguageDictionary.put("Do you want to delete test on?", "Do you want to delete test on?");
        currentLanguageDictionary.put("Delete", "Delete");

        currentLanguageDictionary.put("January", "January");
        currentLanguageDictionary.put("February", "February");
        currentLanguageDictionary.put("March", "March");
        currentLanguageDictionary.put("April", "April");
        currentLanguageDictionary.put("May", "May");
        currentLanguageDictionary.put("June", "June");
        currentLanguageDictionary.put("July", "July");
        currentLanguageDictionary.put("August", "August");
        currentLanguageDictionary.put("September", "September");
        currentLanguageDictionary.put("October", "October");
        currentLanguageDictionary.put("November", "November");
        currentLanguageDictionary.put("December", "December");

        currentLanguageDictionary.put("Sunday", "Sunday");
        currentLanguageDictionary.put("Monday", "Monday");
        currentLanguageDictionary.put("Tuesday", "Tuesday");
        currentLanguageDictionary.put("Wednesday", "Wednesday");
        currentLanguageDictionary.put("Thursday", "Thursday");
        currentLanguageDictionary.put("Friday", "Friday");
        currentLanguageDictionary.put("Saturday", "Saturday");
        currentLanguageDictionary.put("Sun", "Sun");
        currentLanguageDictionary.put("Mon", "Mon");
        currentLanguageDictionary.put("Tue", "Tue");
        currentLanguageDictionary.put("Wed", "Wed");
        currentLanguageDictionary.put("Thu", "Thu");
        currentLanguageDictionary.put("Fri", "Fri");
        currentLanguageDictionary.put("Sat", "Sat");
        currentLanguageDictionary.put("AM", "AM");
        currentLanguageDictionary.put("PM", "PM");
        currentLanguageDictionary.put("Updating", "Updating");
        currentLanguageDictionary.put("Firmware is updated successfully", "Firmware is updated successfully");
        currentLanguageDictionary.put("Past Results", "Past Results");
        currentLanguageDictionary.put("MONTH", "MONTH");
        currentLanguageDictionary.put("WEEK", "WEEK");
        currentLanguageDictionary.put("YEAR", "YEAR");
        currentLanguageDictionary.put("DAY", "DAY");
        currentLanguageDictionary.put("Currently Not Available", "Currently Not Available");
        currentLanguageDictionary.put("Please enter your OTP", "Please enter your OTP");
        currentLanguageDictionary.put("Enter your OTP", "Enter your OTP");
        ////////////////////////
        currentLanguageDictionary.put("Family Doctor", "Family Doctor");
        currentLanguageDictionary.put("Find Hospitals", "Find Hospitals");
        currentLanguageDictionary.put("Phone", "Phone");
        currentLanguageDictionary.put("Specialization", "Specialization");
        currentLanguageDictionary.put("Address", "Address");
        currentLanguageDictionary.put("Pick a location", "Pick a location");
        currentLanguageDictionary.put("Save", "Save");
        currentLanguageDictionary.put("Allergist", "Allergist");
        currentLanguageDictionary.put("Anasthesiologist", "Anasthesiologist");
        currentLanguageDictionary.put("Andrologist", "Andrologist");
        currentLanguageDictionary.put("Cardiologist", "Cardiologist");
        currentLanguageDictionary.put("Cardiac Electrophysiologist", "Cardiac Electrophysiologist");
        currentLanguageDictionary.put("Dermatologist", "Dermatologist");
        currentLanguageDictionary.put("Emergency Medicine / Emergency (ER) Doctors", "Emergency Medicine / Emergency (ER) Doctors");
        currentLanguageDictionary.put("Endocrinologist", "Endocrinologist");
        currentLanguageDictionary.put("Epidemiologist", "Epidemiologist");
        currentLanguageDictionary.put("Family Medicine Physician", "Family Medicine Physician");
        currentLanguageDictionary.put("Gastroenterologist", "Gastroenterologist");
        currentLanguageDictionary.put("Geriatrician", "Geriatrician");
        currentLanguageDictionary.put("Hyperbar Physician", "Hyperbar Physician");
        currentLanguageDictionary.put("Hematologist", "Hematologist");
        currentLanguageDictionary.put("Hepatologist", "Hepatologist");
        currentLanguageDictionary.put("Immunologist", "Immunologist");
        currentLanguageDictionary.put("Infectious Disease Specialist", "Infectious Disease Specialist");
        currentLanguageDictionary.put("Intensivist", "Intensivist");
        currentLanguageDictionary.put("Internal Medicine Specialist", "Internal Medicine Specialist");
        currentLanguageDictionary.put("Maxillofacial Surgeon / Oral Surgeon", "Maxillofacial Surgeon / Oral Surgeon");
        currentLanguageDictionary.put("Medical Geneticist", "Medical Geneticist");
        currentLanguageDictionary.put("Neonatologist", "Neonatologist");
        currentLanguageDictionary.put("Nephrologist", "Nephrologist");
        currentLanguageDictionary.put("Neurologist", "Neurologist");
        currentLanguageDictionary.put("Neurosurgeon", "Neurosurgeon");
        currentLanguageDictionary.put("Nuclear Medicine Specialist", "Nuclear Medicine Specialist");
        currentLanguageDictionary.put("Obstetrician/Gynecologist (OB/GYN)", "Obstetrician/Gynecologist (OB/GYN)");
        currentLanguageDictionary.put("Occupational Medicine Specialist", "Occupational Medicine Specialist");
        currentLanguageDictionary.put("Oncologist", "Oncologist");
        currentLanguageDictionary.put("Ophthalmologist", "Ophthalmologist");
        currentLanguageDictionary.put("Orthopedic Surgeon / Orthopedist", "Orthopedic Surgeon / Orthopedist");
        currentLanguageDictionary.put("Otolaryngologist (also ENT Specialist)", "Otolaryngologist (also ENT Specialist)");
        currentLanguageDictionary.put("Parasitologist", "Parasitologist");
        currentLanguageDictionary.put("Pathologist", "Pathologist");
        currentLanguageDictionary.put("Perinatologist", "Perinatologist");
        currentLanguageDictionary.put("Periodontist", "Periodontist");
        currentLanguageDictionary.put("Pediatrician", "Pediatrician");
        currentLanguageDictionary.put("Physiatrist", "Physiatrist");
        currentLanguageDictionary.put("Plastic Surgeon", "Plastic Surgeon");
        currentLanguageDictionary.put("Psychiatrist", "Psychiatrist");
        currentLanguageDictionary.put("Pulmonologist", "Pulmonologist");
        currentLanguageDictionary.put("Radiologist", "Radiologist");
        currentLanguageDictionary.put("Rheumatologist", "Rheumatologist");
        currentLanguageDictionary.put("Sleep Doctor / Sleep Disorders Specialist", "Sleep Doctor / Sleep Disorders Specialist");
        currentLanguageDictionary.put("Spinal Cord Injury Specialist", "Spinal Cord Injury Specialist");
        currentLanguageDictionary.put("Sports Medicine Specialist", "Sports Medicine Specialist");
        currentLanguageDictionary.put("Surgeon", "Surgeon");
        currentLanguageDictionary.put("Thoracic Surgeon", "Thoracic Surgeon");
        currentLanguageDictionary.put("Urologist", "Urologist");
        currentLanguageDictionary.put("Vascular Surgeon", "Vascular Surgeon");
        currentLanguageDictionary.put("Veterinarian", "Veterinarian");
        currentLanguageDictionary.put("Audiologist", "Audiologist");
        currentLanguageDictionary.put("Chiropractor", "Chiropractor");
        currentLanguageDictionary.put("Diagnostician", "Diagnostician");
        currentLanguageDictionary.put("Microbiologist", "Microbiologist");
        currentLanguageDictionary.put("Palliative care specialist", "Palliative care specialist");
        currentLanguageDictionary.put("Pharmacist", "Pharmacist");
        currentLanguageDictionary.put("Physiotherapist", "Physiotherapist");
        currentLanguageDictionary.put("Podiatrist / Chiropodist", "Podiatrist / Chiropodist");
        currentLanguageDictionary.put("Please pick a location", "Please pick a location");
        currentLanguageDictionary.put("Please choose doctor's specialization", "Please choose doctor's specialization");
        currentLanguageDictionary.put("Please enter doctor's phone number", "Please enter doctor's phone number");
        currentLanguageDictionary.put("Location", "Location");
        currentLanguageDictionary.put("Directions", "Directions");
        currentLanguageDictionary.put("Distance", "Distance");
        currentLanguageDictionary.put("Search a place", "Search a place");
        currentLanguageDictionary.put("Action sheet", "Action sheet");
        currentLanguageDictionary.put("Select month and year", "Select month and year");
        currentLanguageDictionary.put("Please give us your feedback", "Please give us your feedback");
        currentLanguageDictionary.put("Do you really want to share the report to your doctor?", "Do you really want to share the report to your doctor?");
        currentLanguageDictionary.put("There is no doctor added yet. Would you like to add a doctor now?", "There is no doctor added yet. Would you like to add a doctor now?");
        currentLanguageDictionary.put("Your report has been successfully shared to your doctor", "Your report has been successfully shared to your doctor");
        currentLanguageDictionary.put("Your doctor has been rejected your request. Please contact with your doctor.", "Your doctor has been rejected your request. Please contact with your doctor.");
        currentLanguageDictionary.put("Your doctor has not viewed your request. Please contact with your doctor.", "Your doctor has not viewed your request. Please contact with your doctor.");
        currentLanguageDictionary.put("No any test results are available to share. Please test and try again.", "No any test results are available to share. Please test and try again.");

        currentLanguageDictionary.put("Test Record", "Test Record");
        currentLanguageDictionary.put("Click here to check recent test results and history", "Click here to check recent test results and history");
        currentLanguageDictionary.put("Website", "Website");
        currentLanguageDictionary.put("Click here to visit our official website", "Click here to visit our official website");
        currentLanguageDictionary.put("Test", "Test");
        currentLanguageDictionary.put("Click here to start a new test", "Click here to start a new test");
        currentLanguageDictionary.put("Call doctor", "Call doctor");
        currentLanguageDictionary.put("Settings", "Settings");
        currentLanguageDictionary.put("Click here to set up account, add members, family doctor, find nearby hospitals or check device general information", "Click here to set up account, add members, family doctor, find nearby hospitals or check device general information");
        currentLanguageDictionary.put("Personal Profile", "Personal Profile");
        currentLanguageDictionary.put("Click here to edit or remove the account", "Click here to edit or remove the account");


        currentLanguageDictionary.put("Export", "Export");
        currentLanguageDictionary.put("Configuring", "Configuring");
        currentLanguageDictionary.put("Instructions", "Instructions");
        currentLanguageDictionary.put("Continue With Testing", "Continue With Testing");
        currentLanguageDictionary.put("Insert the strip holder into spectro device", "Insert the strip holder into spectro device");
        currentLanguageDictionary.put("No data available", "No data available");
        currentLanguageDictionary.put("Request Failed", "Request Failed");
        currentLanguageDictionary.put("Retry", "Retry");
        currentLanguageDictionary.put("Kindly follow instructions for better test results", "Kindly follow instructions for better test results");
        currentLanguageDictionary.put("Rejected", "Rejected");
        currentLanguageDictionary.put("Please select an option", "Please select an option");
        currentLanguageDictionary.put("Meters", "Meters");
        currentLanguageDictionary.put("Are you taken test with fasting ?", "Are you taken test with fasting ?");
        currentLanguageDictionary.put("SpectroCare Test Report", "SpectroCare Test Report");
        currentLanguageDictionary.put("Date", "Date");
        currentLanguageDictionary.put("Time", "Time");
        currentLanguageDictionary.put("Blood Type", "Blood Type");
        currentLanguageDictionary.put("BMI", "BMI");
        currentLanguageDictionary.put("Fasting", "Fasting");
        currentLanguageDictionary.put("Urine Test Report", "Urine Test Report");
        currentLanguageDictionary.put("Test Item", "Test Item");
        currentLanguageDictionary.put("Value", "Value");
        currentLanguageDictionary.put("Result", "Result");
        currentLanguageDictionary.put("Flag", "Flag");
        currentLanguageDictionary.put("Health Reference Ranges", "Health Reference Ranges");
        currentLanguageDictionary.put("Mail to doctor", "Mail to doctor");
        currentLanguageDictionary.put("Do you want send Mail to:", "Do you want send Mail to:");
        currentLanguageDictionary.put("day", "day");
        currentLanguageDictionary.put("hour", "hour");
        currentLanguageDictionary.put("mins", "mins");
        currentLanguageDictionary.put("secs", "secs");
        currentLanguageDictionary.put("Km", "Km");
        currentLanguageDictionary.put("Unable to fetch nearby places", "Unable to fetch nearby places");
        currentLanguageDictionary.put("Pending", "Pending");
        currentLanguageDictionary.put("Testing Failed", "Testing Failed");
        currentLanguageDictionary.put("Getting Info", "Getting Info");

        //3.0 texts
        currentLanguageDictionary.put("Report has been successfully shared", "Report has been successfully shared");

        currentLanguageDictionary.put("You have been successfully logged out", "You have been successfully logged out");
        currentLanguageDictionary.put("Please pass a valid email address", "Please pass a valid email address");
        currentLanguageDictionary.put("Your test reservation information has been successfully stored", "Your test reservation information has been successfully stored");
        currentLanguageDictionary.put("User dont have account", "User dont have account");

        currentLanguageDictionary.put("Your test reservation information has been successfully updated.", "Your test reservation information has been successfully updated.");
        currentLanguageDictionary.put("Your testreserv not updated.", "Your testreserv not updated.");
        currentLanguageDictionary.put("testReservNo Not Found.", "testReservNo Not Found.");
        currentLanguageDictionary.put("HumanTestReservs", "HumanTestReservs");

        currentLanguageDictionary.put("PetTestReservs", "PetTestReservs");
        currentLanguageDictionary.put("Delete Operation Failed", "Delete Operation Failed");
        currentLanguageDictionary.put("no user found to verify", "no user found to verify");
        currentLanguageDictionary.put("HumanTestReservs", "HumanTestReservs");
        currentLanguageDictionary.put("Invalid email address", "Invalid email address");
        //
        currentLanguageDictionary.put("no content found", "no content found");
        currentLanguageDictionary.put("Languages-updates", "Languages-update");
        currentLanguageDictionary.put("Language has been updated successfully", "Language has been updated successfully");
        currentLanguageDictionary.put("You have not yet set your password. Please set your password", "You have not yet set your password. Please set your password");
        currentLanguageDictionary.put("User not verified. Please verify your email", "User not verified. Please verify your email");
        currentLanguageDictionary.put("You have not yet set your password. Please set your password", "You have not yet set your password. Please set your password");

        currentLanguageDictionary.put("User not verified. Please verify your email", "User not verified. Please verify your email");
        currentLanguageDictionary.put("The username or password is incorrect", "The username or password is incorrect");
        currentLanguageDictionary.put("no request content", "no request content");
        currentLanguageDictionary.put("Client ID already existed", "Client ID already existed");

        currentLanguageDictionary.put("You don't have Pet", "You don't have Pet");
        currentLanguageDictionary.put("The new password has been set successfully, please login again", "The new password has been set successfully, please login again");
        currentLanguageDictionary.put("You are not a authorized user to set password", "You are not a authorized user to set password");
        currentLanguageDictionary.put("No hospital found", "No hospital found");
        currentLanguageDictionary.put("No locations found", "No locations found");
        currentLanguageDictionary.put("Hospital Available", "Hospital Available");
        currentLanguageDictionary.put("Personal information is not updated", "YPersonal information is not updated");
        currentLanguageDictionary.put("Please provide your data first", "Please provide your data first");
        currentLanguageDictionary.put("Client ID already existed", "Client ID already existed");
        currentLanguageDictionary.put("Human Clients", "Human Clients");
        currentLanguageDictionary.put("You don't have Human Clients", "You don't have Human Clients");
        currentLanguageDictionary.put("No client found", "No client found");
        currentLanguageDictionary.put("Successfully deleted", "Successfully deleted");
        currentLanguageDictionary.put("Your JSON file has been successfully stored", "Your JSON file has been successfully stored");
        currentLanguageDictionary.put("File updated", "File updated");
        currentLanguageDictionary.put("User is invalid", "User is invalid");
        currentLanguageDictionary.put("User don't have account", "User don't have account");
        currentLanguageDictionary.put("files", "files");
        currentLanguageDictionary.put("no file found", "no file found");
        currentLanguageDictionary.put("successfully deleted", "successfully deleted");
        currentLanguageDictionary.put("Collect Urine Sample", "Collect Urine Sample");
        currentLanguageDictionary.put("Collect your urine sample with the test tube", "Collect your urine sample with the test tube");
        currentLanguageDictionary.put("Immerse the test strip in urine for 1 to  2 seconds and dry the test strip", "Immerse the test strip in urine for 1 to  2 seconds and dry the test strip");
        currentLanguageDictionary.put("Insert the test strip into strip holder", "Insert the test strip into strip holder");
        currentLanguageDictionary.put("Slightly push the strip holder into the device and press START. The test and analysis will start immediately.", "Slightly push the strip holder into the device and press START. The test and analysis will start immediately.");
        currentLanguageDictionary.put("Client ID", "Client ID");
        currentLanguageDictionary.put("Forgot Password", "Forgot Password");
        currentLanguageDictionary.put("Enter valid email", "Enter valid email");
        currentLanguageDictionary.put("You don't have account. Please register", "You don't have account. Please register");
        currentLanguageDictionary.put("Mr.Jacob have an appointment for urine test", "Mr.Jacob have an appointment for urine test");
        currentLanguageDictionary.put("Would you like to test now?", "Would you like to test now?");
        currentLanguageDictionary.put("Reschedule", "Reschedule");
        currentLanguageDictionary.put("Your clinic has not been registered", "Your clinic has not been registered");
        currentLanguageDictionary.put("Some errors occurred. Please try again later", "Some errors occurred. Please try again later");
        currentLanguageDictionary.put("No content found to process your request", "No content found to process your request");
        currentLanguageDictionary.put("No Data found", "No Data found");
        currentLanguageDictionary.put("Some errors occurred", "Some errors occurred");
        currentLanguageDictionary.put("The new password has been changed successfully, please login again", "The new password has been changed successfully, please login again");
        currentLanguageDictionary.put("You are not a valid user to set password", "You are not a valid user to set password");
        currentLanguageDictionary.put("You have already set this password, please enter a different password", "You have already set this password, please enter a different password");
        currentLanguageDictionary.put("Pass a valid email address", "Pass a valid email address");
        currentLanguageDictionary.put("no content to process your request", "no content to process your request");
        currentLanguageDictionary.put("Your feedback has been submitted successfully. We will contact you soon.", "Your feedback has been submitted successfully. We will contact you soon.");
        currentLanguageDictionary.put("We have not yet provided the option to use mobile number.", "We have not yet provided the option to use mobile number.");
        currentLanguageDictionary.put("DONE", "DONE");
        currentLanguageDictionary.put("Owner Name", "Owner Name");
        currentLanguageDictionary.put("Pet Name", "Pet Name");
        currentLanguageDictionary.put("Pet type", "Pet type");
        currentLanguageDictionary.put("Dog", "Dog");
        currentLanguageDictionary.put("Cat", "Cat");
        currentLanguageDictionary.put("Breed", "Breed");
        currentLanguageDictionary.put("Neuter", "Neuter");
        currentLanguageDictionary.put("Please enter pet name", "Please enter pet name");
        currentLanguageDictionary.put("Choose Image", "Choose Image");
        currentLanguageDictionary.put("Next", "Next");
        currentLanguageDictionary.put("Select a Client", "Select a Client");
        currentLanguageDictionary.put("Test Name", "Test Name");
        currentLanguageDictionary.put("Send Notifications", "Send Notifications");
        currentLanguageDictionary.put("All", "All");
        currentLanguageDictionary.put("Open Now", "Open Now");
        currentLanguageDictionary.put("Top Rated", "Top Rated");
        currentLanguageDictionary.put("Enter your name", "Enter your name");
        currentLanguageDictionary.put("Please Enter Name", "Please Enter Name");
        currentLanguageDictionary.put("Your feedback has been successfully submitted", "Your feedback has been successfully submitted");
        currentLanguageDictionary.put("We will contact you soon", "We will contact you soon");
        currentLanguageDictionary.put("ENTER OLD PASSWORD", "ENTER OLD PASSWORD");
        currentLanguageDictionary.put("ENTER NEW PASSWORD", "ENTER NEW PASSWORD");
        currentLanguageDictionary.put("Please enter the password", "Please enter the password");
        currentLanguageDictionary.put("Please enter new password", "Please enter new password");
        currentLanguageDictionary.put("Password must be different", "Password must be different");
        currentLanguageDictionary.put("Urine Routine", "Urine Routine");
        currentLanguageDictionary.put("Blood", "Blood");
        currentLanguageDictionary.put("Pair The Device", "Pair The Device");
        currentLanguageDictionary.put("Please connect with device", "Please connect with device");
        currentLanguageDictionary.put("Open", "Open");
        currentLanguageDictionary.put("SpectroCare wants to open Google Maps", "SpectroCare wants to open Google Maps");
        currentLanguageDictionary.put("Select a Test Strip", "Select a Test Strip");
        currentLanguageDictionary.put("Sort By", "Sort By");
        currentLanguageDictionary.put("Skip", "Skip");
        currentLanguageDictionary.put("I don't get the code", "I don't get the code");
        currentLanguageDictionary.put("Resend OTP", "Resend OTP");
        currentLanguageDictionary.put("Please enter OTP", "Please enter OTP");
        currentLanguageDictionary.put("About Clinic", "About Clinic");
        currentLanguageDictionary.put("Telephone", "Telephone");
        currentLanguageDictionary.put("E-mail", "E-mail");
        currentLanguageDictionary.put("Fax", "Fax");
        currentLanguageDictionary.put("Contact Person", "Contact Person");
        currentLanguageDictionary.put("Current Location", "Current Location");
        currentLanguageDictionary.put("Pick up Location", "Pick up Location");
        currentLanguageDictionary.put("Please enter telephone number", "Please enter telephone number");
        currentLanguageDictionary.put("Please enter address", "Please enter address");
        currentLanguageDictionary.put("Human", "Human");
        currentLanguageDictionary.put("Add Client", "Add Client");
        currentLanguageDictionary.put("Edit Client", "Edit Client");
        currentLanguageDictionary.put("Add Pet", "Add Pet");
        currentLanguageDictionary.put("Edit Pet", "Edit Pet");
        currentLanguageDictionary.put("Client No", "Client No");
        currentLanguageDictionary.put("Birthday", "Birthday");
        currentLanguageDictionary.put("Note", "Note");
        currentLanguageDictionary.put("Please enter client number", "Please enter client number");
        currentLanguageDictionary.put("Please enter client name", "Please enter client name");
        currentLanguageDictionary.put("Please choose date of birth", "Please choose date of birth");
        currentLanguageDictionary.put("Please enter blood type", "Please enter blood type");
        currentLanguageDictionary.put("Request Failed. Do you want to retry?", "Request Failed. Do you want to retry?");
        currentLanguageDictionary.put("Human Health", "Human Health");
        currentLanguageDictionary.put("Animal Health", "Animal Health");
        currentLanguageDictionary.put("Food Safety", "Food Safety");
        currentLanguageDictionary.put("My Clients", "My Clients");
        currentLanguageDictionary.put("Test Reservation", "Test Reservation");
        currentLanguageDictionary.put("Find Nearby Place", "Find Nearby Place");
        currentLanguageDictionary.put("My Devices", "My Devices");
        currentLanguageDictionary.put("Sign Out", "Sign Out");
        currentLanguageDictionary.put("CONFIRM PASSWORD", "CONFIRM PASSWORD");
        currentLanguageDictionary.put("Already have an account?", "Already have an account?");
        currentLanguageDictionary.put("LOGIN here", "LOGIN here");
        currentLanguageDictionary.put("Please enter email", "Please enter email");
        currentLanguageDictionary.put("Please enter password", "Please enter password");
        currentLanguageDictionary.put("Confirm password is required", "Confirm password is required");
        currentLanguageDictionary.put("Enter password same as above", "Enter password same as above");
        currentLanguageDictionary.put("Enter your OTP", "Enter your OTP");
        currentLanguageDictionary.put("SIGN IN", "SIGN IN");
        currentLanguageDictionary.put("EMAIL", "EMAIL");
        currentLanguageDictionary.put("PASSWORD", "PASSWORD");
        currentLanguageDictionary.put("Don't have an account?", "Don't have an account?");
        currentLanguageDictionary.put("SIGN UP here", "SIGN UP here");
        currentLanguageDictionary.put("loading", "loading");
        currentLanguageDictionary.put("Pet", "Pet");
        currentLanguageDictionary.put("Select a Pet", "Select a Pet");
        currentLanguageDictionary.put("Step 3", "Step 3");
        currentLanguageDictionary.put("Blood Test is not currently available", "Blood Test is not currently available");
        currentLanguageDictionary.put("Pet", "Pet");
        currentLanguageDictionary.put("Select a Pet", "Select a Pet");
        currentLanguageDictionary.put("Step 3", "Step 3");
        currentLanguageDictionary.put("Blood Test is not currently available", "Blood Test is not currently available");
        currentLanguageDictionary.put("Select Test Item", "Select Test Item");
        currentLanguageDictionary.put("Urine", "Urine");
        currentLanguageDictionary.put("Select Date", "Select Date");
        currentLanguageDictionary.put("Select Time", "Select Time");
        currentLanguageDictionary.put("How to connect to device", "How to connect to device");
        currentLanguageDictionary.put("Are you sure you want to delete this Test Reservation?", "Are you sure you want to delete this Test Reservation?");
        currentLanguageDictionary.put("No Reservation", "No Reservation");
        currentLanguageDictionary.put("Unpair Device", "Unpair Device");
        currentLanguageDictionary.put("Waiting for results…", "Waiting for results…");
        currentLanguageDictionary.put("Download failed", "Download failed");
        currentLanguageDictionary.put("Configure File", "Configure File");
        currentLanguageDictionary.put("Downloading files", "Downloading files");
        currentLanguageDictionary.put("Test Diary", "Test Diary");
        currentLanguageDictionary.put("No Clients", "No Clients");
        currentLanguageDictionary.put("No Pets", "No Pets");
        currentLanguageDictionary.put("Step 3", "Step 3");
        currentLanguageDictionary.put("No data found. Please register with us", "No data found. Please register with us");
        currentLanguageDictionary.put("Client Name", "Client Name");
        currentLanguageDictionary.put("My Account", "My Account");
        currentLanguageDictionary.put("Are you sure you want to delete this Client?", "Are you sure you want to delete this Client?");
        currentLanguageDictionary.put("Are you sure you want to delete this Pet?", "Are you sure you want to delete this Pet?");
        currentLanguageDictionary.put("Select an option", "Select an option");
        currentLanguageDictionary.put("Added Time", "Added Time");
        currentLanguageDictionary.put("Please select a client ", "Please select a client ");
        currentLanguageDictionary.put("Please select a test strip", "Please select a test strip");
        currentLanguageDictionary.put("Select File", "Select File");
        currentLanguageDictionary.put("Would you like to configure the", "Would you like to configure the");
        currentLanguageDictionary.put("into SpectroDevice", "into SpectroDevice");
        currentLanguageDictionary.put("Would you like to configure the 'XXXXXXXX' into the device?", "Would you like to configure the 'XXXXXXXX' into the device?");
        currentLanguageDictionary.put("Select Test Item", "Select Test Item");
        currentLanguageDictionary.put("Select a Pet", "Select a Pet");
        currentLanguageDictionary.put("Please select a pet", "Please select a pet");
        currentLanguageDictionary.put("Save and Notify", "Save and Notify");

        currentLanguageDictionary.put("Select Test Item", "Select Test Item");
        currentLanguageDictionary.put("Urine", "Urine");
        currentLanguageDictionary.put("Select Date", "Select Date");
        currentLanguageDictionary.put("Select Time", "Select Time");
        currentLanguageDictionary.put("How to connect to device", "How to connect to device");
        currentLanguageDictionary.put("Are you sure you want to delete this Test Reservation?", "Are you sure you want to delete this Test Reservation?");
        currentLanguageDictionary.put("No Reservation", "No Reservation");
        currentLanguageDictionary.put("Unpair Device", "Unpair Device");
        currentLanguageDictionary.put("Waiting for results…", "Waiting for results…");
        currentLanguageDictionary.put("Download failed!!!", "Download failed!!!");
        currentLanguageDictionary.put("Configure File", "Configure File");
        currentLanguageDictionary.put("Downloading files", "Downloading files");
        currentLanguageDictionary.put("Test Diary", "Test Diary");
        currentLanguageDictionary.put("No Clients", "No Clients");
        currentLanguageDictionary.put("No Pets", "No Pets");
        currentLanguageDictionary.put("Step 3", "Step 3");
        currentLanguageDictionary.put("No data found. Please register with us", "No data found. Please register with us");
        currentLanguageDictionary.put("Client Name", "Client Name");
        currentLanguageDictionary.put("My Account", "My Account");
        currentLanguageDictionary.put("Are you sure you want to delete this Client?", "Are you sure you want to delete this Client?");
        currentLanguageDictionary.put("Are you sure you want to delete this Pet?", "Are you sure you want to delete this Pet?");
        currentLanguageDictionary.put("Select an option", "Select an option");
        currentLanguageDictionary.put("Added Time", "Added Time");
        currentLanguageDictionary.put("Please select a client ", "Please select a client ");
        currentLanguageDictionary.put("Please select a test strip", "Please select a test strip");
        currentLanguageDictionary.put("Select File", "Select File");
        currentLanguageDictionary.put("Would you like to configure the", "Would you like to configure the");
        currentLanguageDictionary.put("into SpectroDevice", "into SpectroDevice");
        currentLanguageDictionary.put("Would you like to configure the 'XXXXXXXX' into the device?", "Would you like to configure the 'XXXXXXXX' into the device?");
        currentLanguageDictionary.put("Select Test Item", "Select Test Item");
        currentLanguageDictionary.put("Select a Pet", "Select a Pet");
        currentLanguageDictionary.put("Please select a pet", "Please select a pet");
        currentLanguageDictionary.put("Save and Notify", "Save and Notify");

        currentLanguageDictionary.put("Latest Updates", "Latest Updates");
        currentLanguageDictionary.put("Expired", "Expired");
        currentLanguageDictionary.put("Upcoming", "Upcoming");
        currentLanguageDictionary.put("Rescheduled", "Rescheduled");

        currentLanguageDictionary.put("Do you want to abort the test?", "Do you want to abort the test?");
        currentLanguageDictionary.put("Status", "Status");
        currentLanguageDictionary.put("Close", "Close");
        currentLanguageDictionary.put("Type Client ID or Client Name", "Type Client ID or Client Name");
        currentLanguageDictionary.put("Type Pet ID or Pet Name", "Type Pet ID or Pet Name");
        currentLanguageDictionary.put("Remove", "Remove");
        currentLanguageDictionary.put("No strip holder was detected. Please try again", "No strip holder was detected. Please try again");
        currentLanguageDictionary.put("Reject", "Reject");
        currentLanguageDictionary.put("Please Enter Pet Number", "Please Enter Pet Number");
        currentLanguageDictionary.put("Enter your phone number", "Enter your phone number");
        currentLanguageDictionary.put("Enter your contact person", "Enter your contact person");
        currentLanguageDictionary.put("Collect urine sample with test tube. Immerse the test strip in urine for 1 to 2 seconds , and dry the test strip.", "Collect urine sample with test tube. Immerse the test strip in urine for 1 to 2 seconds , and dry the test strip.");
        currentLanguageDictionary.put("Put Test Strip into Holder", "Put Test Strip into Holder");
        currentLanguageDictionary.put("Put the  test strip into the test strip holder.", "Put the  test strip into the test strip holder.");
        currentLanguageDictionary.put("Insert Test Strip Holder", "Insert Test Strip Holder");
        currentLanguageDictionary.put("ABORT", "ABORT");

        currentLanguageDictionary.put("Select a Strip","Select a Strip");

        currentLanguageDictionary.put("Select a client ", "Select a client ");
        currentLanguageDictionary.put("Collect Test Sample", "Collect Test Sample");
        currentLanguageDictionary.put("Get Test Results", "Get Test Results");
        currentLanguageDictionary.put("Rapid Test for your client ", "Rapid Test for your client ");
        currentLanguageDictionary.put("Perform rapid test for your clients and track their health conditions.", "Perform rapid test for your clients and track their health conditions.");
        currentLanguageDictionary.put("Easy Test Flow ", "Easy Test Flow ");
        currentLanguageDictionary.put("Easy step to perform the test and receive test data within 5 mins", "Easy step to perform the test and receive test data within 5 mins");
        currentLanguageDictionary.put("Share Report", "Share Report");
        currentLanguageDictionary.put("Share and save your clients test reports to local file for furture tracking", "Share and save your clients test reports to local file for furture tracking");
        currentLanguageDictionary.put("Client Management ", "Client Management ");
        currentLanguageDictionary.put("Create and manage your own client list and track their test records anytime. ", "Create and manage your own client list and track their test records anytime. ");
        currentLanguageDictionary.put("DONE", "DONE");
        currentLanguageDictionary.put("Would you like to change the language 'CURRENT_LANGUAGE' to 'SELECTED_LANGUAGE_ENGLISH_NAME SELECTED_LANGUAGE_NATIVE_NAME'?", "Would you like to change the language 'CURRENT_LANGUAGE' to 'SELECTED_LANGUAGE_ENGLISH_NAME SELECTED_LANGUAGE_NATIVE_NAME'?");

        currentLanguageDictionary.put("CLIENT_NAME (ID: ID_NUMBER ) have an appointment with you at TIME for Pet Urine Test ", "CLIENT_NAME (ID: ID_NUMBER ) have an appointment with you at TIME for Pet Urine Test ");
        currentLanguageDictionary.put("CLIENT_NAME (ID: ID_NUMBER ) have an appointment with you at TIME for Human Urine Test! ", "CLIENT_NAME (ID: ID_NUMBER ) have an appointment with you at TIME for Human Urine Test! ");

        currentLanguageDictionary.put("Password and confirm password does not match", "Password and confirm password does not match");

        currentLanguageDictionary.put("Find Nearby Clinics", "Find Nearby Clinics");
        currentLanguageDictionary.put("Please select valid time", "Please select valid time");
        currentLanguageDictionary.put("Password must be at least 8 characters in length.", "Password must be at least 8 characters in length.");
        currentLanguageDictionary.put("You already have an account with us.", "You already have an account with us.");
        currentLanguageDictionary.put("please enable location services for this app.", "please enable location services for this app.");
        currentLanguageDictionary.put("Please enter age", "Please enter age");
        currentLanguageDictionary.put("UNIT", "UNIT");
        currentLanguageDictionary.put("Report Date", "Report Date");
        currentLanguageDictionary.put("Test Time", "Test Time");
        currentLanguageDictionary.put("REFERENCE RANGE", "REFERENCE RANGE");
        currentLanguageDictionary.put("S.NO", "S.NO");
        currentLanguageDictionary.put("Location Services Disabled", "Location Services Disabled");

        currentLanguageDictionary.put("Aborting", "Aborting");
        currentLanguageDictionary.put("Pet  ID", "Pet  ID");
        currentLanguageDictionary.put("Connection Failed", "Connection Failed");
        currentLanguageDictionary.put("Select age", "Select age");
        currentLanguageDictionary.put("Call to Doctor", "Call to Doctor");
        currentLanguageDictionary.put("Request Failed. Are you want to retry", "Request Failed. Are you want to retry");

        currentLanguageDictionary.put(" Kindly choose suitable JSON file for your device", " Kindly choose suitable JSON file for your device");
        currentLanguageDictionary.put("Pickup From Files", "Pickup From Files");
        currentLanguageDictionary.put("Please select correct JSON file", "Please select correct JSON file");

        }

    private void saveLanguagesMap(HashMap<String, HashMap> inputMap) {
        Log.e("inputMap", "" + inputMap.toString());
        SharedPreferences pSharedPref = context.getSharedPreferences("MyVariables", Context.MODE_PRIVATE);
        if (pSharedPref != null) {
            JSONObject jsonObject = new JSONObject(inputMap);
            String jsonString = jsonObject.toString();
            Log.e("jsonString", "" + jsonString.toString());
            SharedPreferences.Editor editor = pSharedPref.edit();
            editor.remove("My_map").commit();
            editor.putString("My_map", jsonString);
            editor.commit();
        }
    }

    public HashMap<String, HashMap> loadLanguagesMap() {
        HashMap<String, HashMap> outputMap = new HashMap<String, HashMap>();
        SharedPreferences pSharedPref = context.getSharedPreferences("MyVariables", Context.MODE_PRIVATE);
        Log.e("loadpSharedPref", "" + pSharedPref.toString());
        try {
            if (pSharedPref != null) {
                String jsonString = pSharedPref.getString("My_map", (new JSONObject()).toString());
                JSONObject jsonObject = new JSONObject(jsonString);
                Iterator<String> keysItr = jsonObject.keys();

                HashMap<String, HashMap> retMap = new Gson().fromJson(
                        jsonString, new TypeToken<HashMap<String, HashMap>>() {
                        }.getType()
                );

                Log.e("loadjsonObjectGJSOn", "" + retMap.size());
                Log.e("loadjsonObjectGJSOn1", "" + retMap.get(com.gtsr.gtsr.Constants.AppLanguages.English.toString()));


                outputMap = retMap;

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return outputMap;
    }
}



