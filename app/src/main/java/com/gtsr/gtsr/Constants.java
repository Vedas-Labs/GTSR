package com.gtsr.gtsr;

/**
 * Created by jacobliu on 10/29/17.
 */

public class Constants {
  public static String aboutus_url = "https://www.spectrochips.com";

    public enum RegisterTypes {
        Manual("Manual"),
        Facebook("Facebook"),
        Google("Google"),
        Twitter("Twitter");

        private final String name;

        private RegisterTypes(String s) {
            name = s;
        }

        public boolean equalsName(String otherName) {
            // (otherName == null) check is not needed because name.equals(null) returns false
            return name.equals(otherName);
        }

        public String toString() {
            return this.name;
        }
    }





    public enum AppLanguages {

        English("English"),
        Traditional("Traditional"),
        Simplified("Simplified"),
        French("French"),
        Portuguese("Portuguese");


        private final String name;

        private AppLanguages(String s) {
            name = s;
        }

        public boolean equalsName(String otherName) {
            // (otherName == null) check is not needed because name.equals(null) returns false
            return name.equals(otherName);
        }

        public String toString() {
            return this.name;
        }
    }

    public enum GooglePlacesTypes {
        dentist("dentist"),
        doctor("doctor"),
        health("health"),
        veterinary_care("veterinary_care"),
        pharmacy("pharmacy"),
        hospital("hospital");

        private final String name;

        private GooglePlacesTypes(String s) {
            name = s;
        }

        public boolean equalsName(String otherName) {
            // (otherName == null) check is not needed because name.equals(null) returns false
            return name.equals(otherName);
        }

        public String toString() {
            return this.name;
        }
    }

    public enum JSONTYPES {

        no5_3518_urineTest("no5_3518_urineTest"),
        no9_3518_urineTest("no9_3518_urineTest");

        private final String name;

        private JSONTYPES(String s) {
            name = s;
        }

        public boolean equalsName(String otherName) {
            // (otherName == null) check is not needed because name.equals(null) returns false
            return name.equals(otherName);
        }

        public String toString() {
            return this.name;
        }
    }

    public enum CLIENTTYPES {

        Human("Human"),
        Pet("Pet");

        private final String name;

        private CLIENTTYPES(String s) {
            name = s;
        }

        public boolean equalsName(String otherName) {
            return name.equals(otherName);
        }
        public String toString() {
            return this.name;
        }
    }

//LEUKOCYTES
    public static enum UrineTestItems {
        LEUKOCYTES("Leukocytes"),
        Nitrite("Nitrite"),
        Urobilinogen("Urobilinogen"),
        Protein("Protein"),
        PH("PH"),
        OccultBlood("Occult Blood"),
        SpecificGravity("Specific Gravity"),
        Ketone("Ketone"),
        Bilirubin("Bilirubin"),
        Glucose("Glucose"),
        Ascorbic("Ascorbic Acid");//Ascorbic Acid

        private final String name;

        private UrineTestItems(String s) {
            name = s;
        }

        public boolean equalsName(String otherName) {
            return name.equals(otherName);
        }

        public final String toString() {
            return this.name;
        }
    }

    public enum MapTypes {

        TopRated("Top Rated"),
        OpenNow("Open Now"),
        Nearby("Nearby"),
        All("All");

        private final String name;

        private MapTypes(String s) {
            name = s;
        }

        public boolean equalsName(String otherName) {
            // (otherName == null) check is not needed because name.equals(null) returns false
            return name.equals(otherName);
        }

        public String toString() {
            return this.name;
        }
    }


    public enum RepeatTypes {

        Once("Once"),
        EveryDay("Every day"),
        EveryWeek("Every Week"),
        Every15Days("Every 15 Days"),
        Every30Days("Every 30 Days");

        private final String name;

        private RepeatTypes(String s) {
            name = s;
        }

        public boolean equalsName(String otherName) {
            // (otherName == null) check is not needed because name.equals(null) returns false
            return name.equals(otherName);
        }

        public String toString() {
            return this.name;
        }
    }

    public enum HealthTrendNames {
        overAll("Overall"),
        kidneyCapacity("Kidney Capacity"),
        liver("Liver Function"),
        diabetes("Diabetes Function"),
        urinary("Urinary Tract Infection");

        private final String name;
        private HealthTrendNames(String s) {
            name = s;
        }
        public boolean equalsName(String otherName) {
            // (otherName == null) check is not needed because name.equals(null) returns false
            return name.equals(otherName);
        }

        public String toString() {
            return this.name;
        }
    }
    public enum UrineAndBloodNames {
        urine("Urine"),
        blood("Blood");

        private final String name;
        private UrineAndBloodNames(String s) {
            name = s;
        }
        public boolean equalsName(String otherName) {
            // (otherName == null) check is not needed because name.equals(null) returns false
            return name.equals(otherName);
        }

        public String toString() {
            return this.name;
        }
    }
}
