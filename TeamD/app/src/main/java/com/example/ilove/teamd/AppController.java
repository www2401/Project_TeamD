package com.example.ilove.teamd;

/**
 * Created by hyewonkim on 2017. 8. 10..
 */

public class AppController {

    public BluetoothChatService mChatService = null;
    private static AppController instance = new AppController();
    public static AppController getinstance() {return instance;};

}
