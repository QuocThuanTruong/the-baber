package com.qtt.thebarber.Model;

import com.qtt.thebarber.Common.Common;

public class MyToken {
    private String token;
    private Common.TOKEN_TYPE tokenType;
    private String userPhone;

    public MyToken() {
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public Common.TOKEN_TYPE getTokenType() {
        return tokenType;
    }

    public void setTokenType(Common.TOKEN_TYPE tokenType) {
        this.tokenType = tokenType;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
