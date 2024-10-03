package org.dominator.donationEvents.workWithApi;

public class APIMenager {
    public String APIkey;
    public int keyType = 1;

    public void setAPIkey(String APIkey) {
        this.APIkey = APIkey;
    }

    public String getAPIkey() {
        return APIkey;
    }

    public APIMenager(String APIkey) {
        this(APIkey, 1);
    }

    public APIMenager(String APIkey, int keyType) {
        this.APIkey = APIkey;
        this.keyType = keyType;
    }
}
