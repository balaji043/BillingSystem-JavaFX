package bam.billing.gst.utils;

import com.google.gson.Gson;

import java.io.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Preferences {

    private static final Logger LOGGER = Logger.getLogger(Preferences.class.getName());
    private static final String FILE_PATH = "KrishnaConfig.txt";

    private String name;
    private String address1;
    private String address2;
    private String gstin;
    private String bank;
    private String branch;
    private String acc;
    private String ifsc;
    private String phone;
    private String date;
    private String limit;
    private String cssThemeName;
    private String billInvoiceCodePrefix;


    private Set<String> descriptions;
    private Set<String> perData;
    private Set<String> hsn;
    private Set<String> companyNames;


    // Constructor
    private Preferences() {
        name = "Krishna Enterprises";
        address1 = "JALAL KUDI STREET";
        address2 = "Chatram Trichy";
        gstin = "ADADS456465412";
        bank = "SBI Bank";
        branch = "Chatram";
        acc = "785461157813157895";
        ifsc = "IFSC45657";
        phone = "84657572155";
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
        date = "" + d.getTime();
        limit = "22";
        cssThemeName = "green";
        billInvoiceCodePrefix = "J";
    }

    private static void initConfig() {
        Writer writer = null;
        try {
            File file = new File(FILE_PATH);
            if (file.createNewFile()) LOGGER.log(Level.INFO, FILE_PATH + " CREATED.");
            Preferences preferences = new Preferences();
            writer = new FileWriter(FILE_PATH);
            Gson gson = new Gson();
            gson.toJson(preferences, writer);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
        } finally {
            try {
                assert writer != null;
                writer.close();
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, e.getMessage());
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
                LOGGER.log(Level.SEVERE, e.getMessage());
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
            LOGGER.log(Level.SEVERE, e.getMessage());
        } finally {
            try {
                assert writer != null;
                writer.close();
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, e.getMessage());
            }
        }
    }

    // Getters and Setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getGstin() {
        return gstin;
    }

    public void setGstin(String gstin) {
        this.gstin = gstin;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getAcc() {
        return acc;
    }

    public void setAcc(String acc) {
        this.acc = acc;
    }

    public String getIfsc() {
        return ifsc;
    }

    public void setIfsc(String ifsc) {
        this.ifsc = ifsc;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Set<String> getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(Set<String> descriptions) {
        this.descriptions = descriptions;
    }

    public Set<String> getPerData() {
        return perData;
    }

    public void setPerData(Set<String> perData) {
        this.perData = perData;
    }

    public String getLimit() {
        return limit;
    }

    public void setLimit(String limit) {
        this.limit = limit;
    }

    public String getCssThemeName() {
        return cssThemeName;
    }

    public void setCssThemeName(String cssThemeName) {
        this.cssThemeName = cssThemeName;
    }

    public Set<String> getHsn() {
        return hsn;
    }

    public void setHsn(Set<String> hsn) {
        this.hsn = hsn;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Set<String> getCompanyNames() {
        return companyNames;
    }

    public void setCompanyNames(Set<String> companyNames) {
        this.companyNames = companyNames;
    }

    public String getBillInvoiceCodePrefix() {
        return billInvoiceCodePrefix;
    }

    public void setBillInvoiceCodePrefix(String billInvoiceCodePrefix) {
        this.billInvoiceCodePrefix = billInvoiceCodePrefix;
    }
}
