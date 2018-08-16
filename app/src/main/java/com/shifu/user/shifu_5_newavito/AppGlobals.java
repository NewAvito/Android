package com.shifu.user.shifu_5_newavito;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.Manifest;
import android.util.Base64;

public class AppGlobals {
    final static int REGISTER = 1;
    final static int LOGIN = 2;
    final static int RESTORE = 3;


    static final int timeout = 5;
    static final String URL = "https://uploader9g.disk.yandex.net:443/upload-target/20180814T054543.540.utd.40ju8as46gc55r2temug13jf8-k9g.2870483/";

    //"https://cloud-api.yandex.net/v1/disk/resources/upload/";
    //https://oauth.yandex.ru/authorize/";
    //"http://138.197.162.167/";

    static final String strDateFormat = "yyyy-MM-dd HH:mm";

    private static final String passYandex = "2b442bdfd3fe407ba2a3bd141f94c6b3";
    private static final String idYandex = "ee52935ba2da4f239cf697d7faeef819";

    private static final String token = "AQAAAAAjLsdFAAUmhQDNV2py_UfUsW4da1PRDRo";

    public static String getAuthYandex() {
        String text = idYandex + ":" + passYandex;
        try {
            byte[] data = text.getBytes("UTF-8");
            String out = "Basic "+Base64.encodeToString(data, Base64.DEFAULT);
            out = out.replaceAll("(\\r|\\n)", "");
            return out;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getIdYandex(){
        return idYandex;
    }

    public static String getToken(){
        return "OAuth "+token;
    }
}
