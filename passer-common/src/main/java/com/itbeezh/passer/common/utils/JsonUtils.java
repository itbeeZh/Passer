package com.itbeezh.passer.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import sun.nio.cs.StandardCharsets;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Created by szwb004 on 2017-08-02.
 */
public class JsonUtils {

    /**
     * 将json转换为XML
     * @param json
     * @param charsets
     * @return
     */
    public static String jsonToXml(String json, String charsets){

        try {
            StringBuffer buffer = new StringBuffer();
            buffer.append("<?xml version=\"1.0\" encoding=\""+charsets+"\"?>");
            jsonToXmlstr(JSON.parseObject(json), buffer);
            return buffer.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private static String jsonToXmlstr(JSONObject jObj, StringBuffer buffer) {
        Set<Entry<String, Object>> se = jObj.entrySet();
        for (Iterator<Entry<String, Object>> it = se.iterator(); it.hasNext();) {
            Entry<String, Object> en = it.next();
            if(en.getValue() == null){
                buffer.append("<" + en.getKey() + ">");
                buffer.append("</" + en.getKey() + ">");
            }
            else if (en.getValue().getClass().getName().equals(
                    "com.alibaba.fastjson.JSONObject")) {
                buffer.append("<" + en.getKey() + ">");
                JSONObject jo = jObj.getJSONObject(en.getKey());
                jsonToXmlstr(jo, buffer);
                buffer.append("</" + en.getKey() + ">");
            } else if (en.getValue().getClass().getName().equals(
                    "com.alibaba.fastjson.JSONArray")) {
                JSONArray jarray = jObj.getJSONArray(en.getKey());
                for (int i = 0; i < jarray.size(); i++) {
                    buffer.append("<" + en.getKey() + ">");
                    JSONObject jsonobject = jarray.getJSONObject(i);
                    jsonToXmlstr(jsonobject, buffer);
                    buffer.append("</" + en.getKey() + ">");
                }
            } else if (en.getValue().getClass().getName().equals(
                    "java.lang.String")) {
                buffer.append("<" + en.getKey() + ">" + en.getValue());
                buffer.append("</" + en.getKey() + ">");
            }

        }
        return buffer.toString();
    }
}
