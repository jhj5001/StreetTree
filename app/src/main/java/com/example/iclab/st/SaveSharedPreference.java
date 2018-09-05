package com.example.iclab.st;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SaveSharedPreference {

    static final String PREF_USER_NAME = "username";
    static final String PREF_USER_FULL = "userfull";
    static final String PREF_LIST="userlist";
// 리스트 저장
// gson으로 변환 해서 값 저장



    static SharedPreferences getSharedPreferences(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    // 계정 정보 저장
    public static void setUserName(Context ctx, String userName, String userFull) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_USER_NAME, userName);
        editor.putString(PREF_USER_FULL, userFull);
        editor.commit();
    }

    // 저장된 id 정보 가져오기
    public static String getUserName(Context ctx) {
        return getSharedPreferences(ctx).getString(PREF_USER_NAME, "");
    }
    // 저장된 authorFullName 정보 가져오기
    public static String getUserFull(Context ctx) {
        return getSharedPreferences(ctx).getString(PREF_USER_FULL, "");
    }

    // 로그아웃
    public static void clearUserData(Context ctx) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.remove(PREF_USER_FULL);
        editor.remove(PREF_USER_NAME);
        editor.commit();
    }


    // 리스트 정보 저장
    public static void setPrefList(Context ctx, String list) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_LIST, list);
        editor.commit();
    }

    // 리스트 정보 가져오기
    public static String getPrefList(Context ctx) {
        return getSharedPreferences(ctx).getString(PREF_LIST, "");
    }

    // 리스트 정보 삭제
    public static void delPrefList(Context ctx) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.remove(PREF_LIST);
        editor.commit();
    }



}