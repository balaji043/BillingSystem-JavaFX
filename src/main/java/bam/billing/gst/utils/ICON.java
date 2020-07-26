package bam.billing.gst.utils;

public enum ICON {
    ADD("add.png", "Add"),
    AS("as.ico", "ICON"),
    CIRCLED("circled.png", "Circled"),
    DELETE("delete.png", "Delete"),
    ENTER("enter.png", "Enter"),
    EXIT("exit.png", "Exit"),
    UPLOAD("import.png", "Upload"),
    INPUT("input.png", "Input"),
    KE("ke.png", "KE"),
    KRISH("KrisEnt.png", "KrishEnt"),
    LOGIN("login.png", "Login"),
    MINUS("minus.png", "Minus"),
    OUTPUT("output.png", "Output"),
    PASSWORD("password.png", "Password"),
    REFRESH("refresh.png", "Refresh Page"),
    SAVE("save.png", "Save"),
    SEARCH("search.png", "Search"),
    SETTING("setting.png", "Settings"),
    SIGNOUT("signout.png", "Sign Out"),
    STD("StdEnt.png", "StdEnt"),
    USER("user.png", "User"),
    DOWNLOAD("download.png", "Download"),
    BACK("back.png", "Go Back");

    public String filePath;
    public String toolTip;

    ICON(String filePath, String toolTip) {
        this.filePath = filePath;
        this.toolTip = toolTip;
    }

    @Override
    public String toString() {
        return "/icons/" + filePath;
    }
}
