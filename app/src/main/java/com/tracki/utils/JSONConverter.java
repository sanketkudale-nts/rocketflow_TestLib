package com.tracki.utils;

import com.google.gson.Gson;

/**
 * Created by saurabh on 11/25/2015.
 */
public class JSONConverter<T> {


    public String objectToJson(T t)
    {
        Gson gson =new Gson();
        return gson.toJson(t);
    }

    public T jsonToObject(String data, Class<T> t)
    {
        Gson gson =new Gson();
        T data1 = gson.fromJson(data, t);
        System.out.println(data1);
        return data1;
    }
}
