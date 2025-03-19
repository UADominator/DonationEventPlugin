package org.dominator.donationEvents.useAPI;

public class APIManager {
    private static String donatelloAPIkey;

    public APIManager(){
        donatelloAPIkey = "";
    }

    public APIManager(String APIkey, int keyType) {
        setAPIkey(APIkey, keyType);
    }

    public static void setAPIkey(String APIkey, int keyType) {
        switch (keyType){
            case 1: donatelloAPIkey = APIkey; break;
            default: break;
        }
    }

    public static String getAPIkey(int keyType) {
        return switch (keyType) {
            case 1 -> donatelloAPIkey;
            default -> "ПТН";
        };
    }

    public String getAPIkey() {
        return "ПНХ";
    }
}
