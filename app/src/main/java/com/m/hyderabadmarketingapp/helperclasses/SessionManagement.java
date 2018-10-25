package com.m.hyderabadmarketingapp.helperclasses;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by sine90 on 9/8/2017.
 */

public class SessionManagement {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;
    int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "hyderabad_market";
    public static final String USER_TYPE="user_type";

    public static final String USERID="userid";
    public static final String NAME="name";
    public static final String DEVICETOKEN="devicetoken";
    public static final String DEVICETYPE="devicetype";
    public static final String PROFILEIMAGE="profileimage";
    public static final String FACEBOOKID="facebookid";
    public static final String GOOGLEID="googleid";
    public static final String EMAIL="email";
    public static final String GENDER="gender";
    public static final String MOBILE="mobile";
    public static final String ISLOGIN="islogin";

    public static final int AS_USER =1;
    public static final int AS_MERCHANT =2;

    public SessionManagement(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setValuetoPreference(String key, String value)
    {
        editor.putString(key,value);
        editor.commit();
    }

    public String getValueFromPreference(String key)
    {
        return pref.getString(key,null);
    }

    public void setBooleanValuetoPreference(String key, boolean value)
    {
        editor.putBoolean(key,value);
        editor.commit();
    }

    public boolean getBooleanValueFromPreference(String key)
    {
        return pref.getBoolean(key,false);
    }

    public void logoutUser(){
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();
    }

    public  int getUserType() {
          return pref.getInt(USER_TYPE,-1);
    }
    public  void setUserType(int usertype) {
        editor.putInt(USER_TYPE,usertype);
        editor.commit();
    }

}
