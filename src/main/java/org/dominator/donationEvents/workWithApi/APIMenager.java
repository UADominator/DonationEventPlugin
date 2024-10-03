package org.dominator.donationEvents.workWithApi;

public class APIMenager {
    public String DyakaAPIkey;
    public String DonatelloAPIkey;
    public String MonoAPIkey;
    public String  keyType;

    public APIMenager(String APIkey) {
        this(APIkey, "0");
    }

    public APIMenager(String APIkey, String keyType) {
        this.setAPIkey(APIkey, keyType);
    }



    public String getKeyType() {
        return keyType;
    }

    public void setAPIkey(String APIkey, String keyType) {
        this.keyType=keyType;
        switch (this.keyType){
            case "1": this.DyakaAPIkey = APIkey;
            case "2": this.DonatelloAPIkey = APIkey;
            case "3": this.MonoAPIkey = APIkey;
            default: return;
        }
    }

    public String getAPIkey(String keyType) {
        switch (keyType){
            case "1": return DyakaAPIkey;
            case "2":  return DonatelloAPIkey;
            case "3": return MonoAPIkey;
            default: return "";
        }
    }

    public String getAPIkey() {
        switch (this.keyType){
            case "1": return DyakaAPIkey;
            case "2":  return DonatelloAPIkey;
            case "3": return MonoAPIkey;
            default: return "";
        }
    }
}
