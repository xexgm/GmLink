package com.gm.link.common.utils;

import com.google.gson.Gson;

/**
 * @Author: xexgm
 */
public class JsonUtil {
    private static final Gson gson = new Gson();

    /**
     * 提供json转对象功能
     * @param json
     * @param clazz
     * @return
     * @param <T>
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        return gson.fromJson(json, clazz);
    }

    /**
     * 提供对象转json功能
     * @param object
     * @return
     */
    public static String toJson(Object object) {
        return gson.toJson(object);
    }
}
