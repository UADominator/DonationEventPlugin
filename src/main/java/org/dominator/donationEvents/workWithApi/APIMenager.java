package org.dominator.donationEvents.workWithApi;

public class APIMenager {
    public String dyakaAPIkey;
    public String donatelloAPIkey;
    public String monoAPIkey;;

    public APIMenager(){
        dyakaAPIkey = "";
        donatelloAPIkey = "";
        monoAPIkey = "";
    }

    public APIMenager(String APIkey, int keyType) {
        setAPIkey(APIkey, keyType);
    }

    public void setAPIkey(String APIkey, int keyType) {
        switch (keyType){
            case 1: dyakaAPIkey = APIkey; break;
            case 2: donatelloAPIkey = APIkey; break;
            case 3: monoAPIkey = APIkey; break;
            default: break;
        }
    }

    public String getAPIkey(int keyType) {
        return switch (keyType) {
            case 1 -> dyakaAPIkey;
            case 2 -> donatelloAPIkey;
            case 3 -> monoAPIkey;
            default -> "PIZDA";
        };
    }

    public String getAPIkey() {
        return "DAUN";
    }
}
