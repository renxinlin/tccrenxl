package com.tcc.renxl.recover;



public class Awake {

    public static Object Instance ;
    static   {
        synchronized (EnableSingle.class){
            if(Instance == null){
                Instance = new Wait();
            }
        }
    }
}
