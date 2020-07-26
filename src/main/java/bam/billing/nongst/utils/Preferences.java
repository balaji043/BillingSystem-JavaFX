package bam.billing.nongst.utils;


import com.google.gson.Gson;

import java.io.*;
import java.util.Date;
import java.util.HashSet;

public class Preferences {

    private static final String FILE_PATH = "ElectronicConfig.txt";

    private String storeName;
    private String addressLineOne;
    private String addressLineTwo;
    private String gstInNumber;
    private String bankName;
    private String bankBranchName;
    private String bankAccountNumber;
    private String ifscCode;
    private String phoneNumber;
    private String dateDate;
    private String productMaxLimitPerBill;
    private String cssThemeName;
    private String logoName;
    private String billInvoiceCodePrefix;

    private HashSet<String> descriptions;
    private HashSet<String> perData;
    private HashSet<String> hsn;
    private HashSet<String> companyNames;

    // Constructor
    private Preferences() {
        storeName = "Some Name";
        addressLineOne = "JALAL KUDI STREET";
        addressLineTwo = "Chatram Trichy";
        gstInNumber = "ADADS456465412";
        bankName = "SBI Bank";
        bankBranchName = "Chatram";
        bankAccountNumber = "785461157813157895";
        ifscCode = "IFSC45657";
        phoneNumber = "84657572155";
        descriptions = new HashSet<>();
        perData = new HashSet<>();
        hsn = new HashSet<>();
        companyNames = new HashSet<>();
        perData.add("PCS");
        perData.add("DOZ");
        perData.add("PKT");
        perData.add("GRO");
        perData.add("BOX");
        perData.add("SET");
        perData.add("ROL");
        perData.add("BUN");
        Date d = new Date();
        dateDate = "" + d.getTime();
        productMaxLimitPerBill = "22";
        cssThemeName = "red";
        logoName = "StdEnt";
        billInvoiceCodePrefix = "J-";
    }

    private static void initConfig() {
        Writer writer = null;
        try {
            File file = new File(FILE_PATH);
            if (file.createNewFile()) System.out.println();
            Preferences preferences = new Preferences();
            writer = new FileWriter(FILE_PATH);
            Gson gson = new Gson();
            gson.toJson(preferences, writer);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                assert writer != null;
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static Preferences getPreferences() {
        FileReader fileReader;
        Preferences preferences = null;
        try {
            fileReader = new FileReader(FILE_PATH);
            Gson gson = new Gson();
            preferences = gson.fromJson(fileReader, Preferences.class);
        } catch (Exception e) {
            initConfig();
        } finally {
            try {
                fileReader = new FileReader(FILE_PATH);
                Gson gson = new Gson();
                preferences = gson.fromJson(fileReader, Preferences.class);
                fileReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return preferences;
    }

    public static void setPreference(Preferences preferences) {
        Writer writer = null;
        try {
            writer = new FileWriter(FILE_PATH);
            Gson gson = new Gson();
            gson.toJson(preferences, writer);
        } catch (Exception e) {
            e.printStackTrace();
            AlertMaker.showErrorMessage(e);
        } finally {
            try {
                assert writer != null;
                writer.close();
            } catch (IOException e) {
                AlertMaker.showErrorMessage(e);
                e.printStackTrace();
            }
        }
    }

    // Getters and Setters

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getAddressLineOne() {
        return addressLineOne;
    }

    public void setAddressLineOne(String addressLineOne) {
        this.addressLineOne = addressLineOne;
    }

    public String getAddressLineTwo() {
        return addressLineTwo;
    }

    public void setAddressLineTwo(String addressLineTwo) {
        this.addressLineTwo = addressLineTwo;
    }

    public String getGstInNumber() {
        return gstInNumber;
    }

    public void setGstInNumber(String gstInNumber) {
        this.gstInNumber = gstInNumber;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankBranchName() {
        return bankBranchName;
    }

    public void setBankBranchName(String bankBranchName) {
        this.bankBranchName = bankBranchName;
    }

    public String getBankAccountNumber() {
        return bankAccountNumber;
    }

    public void setBankAccountNumber(String bankAccountNumber) {
        this.bankAccountNumber = bankAccountNumber;
    }

    public String getIfscCode() {
        return ifscCode;
    }

    public void setIfscCode(String ifscCode) {
        this.ifscCode = ifscCode;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public HashSet<String> getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(HashSet<String> descriptions) {
        this.descriptions = descriptions;
    }

    public HashSet<String> getPerData() {
        return perData;
    }

    public void setPerData(HashSet<String> perData) {
        this.perData = perData;
    }

    public String getProductMaxLimitPerBill() {
        return productMaxLimitPerBill;
    }

    public void setProductMaxLimitPerBill(String productMaxLimitPerBill) {
        this.productMaxLimitPerBill = productMaxLimitPerBill;
    }

    public String getCssThemeName() {
        return cssThemeName;
    }

    public void setCssThemeName(String cssThemeName) {
        this.cssThemeName = cssThemeName;
    }

    public HashSet<String> getHsn() {
        return hsn;
    }

    public void setHsn(HashSet<String> hsn) {
        this.hsn = hsn;
    }

    public String getDateDate() {
        return dateDate;
    }

    public void setDateDate(String dateDate) {
        this.dateDate = dateDate;
    }

    public String getLogoName() {
        return logoName;
    }

    public void setLogoName(String logoName) {
        this.logoName = logoName;
    }

    public HashSet<String> getCompanyNames() {
        return companyNames;
    }

    public void setCompanyNames(HashSet<String> companyNames) {
        this.companyNames = companyNames;
    }


    public String getBillInvoiceCodePrefix() {
        return billInvoiceCodePrefix;
    }

    public void setBillInvoiceCodePrefix(String billInvoiceCodePrefix) {
        this.billInvoiceCodePrefix = billInvoiceCodePrefix;
    }
}
