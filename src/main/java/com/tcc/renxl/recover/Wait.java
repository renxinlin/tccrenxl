package com.tcc.renxl.recover;

public class Wait {

    public static Object Instance ;
    static   {
        synchronized (EnableSingle.class){
            if(Instance == null){
                Instance = new Wait();
            }
        }
    }
}
