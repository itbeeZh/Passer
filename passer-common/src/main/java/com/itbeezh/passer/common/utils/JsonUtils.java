package com.itbeezh.passer.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.util.*;
import java.util.Map.Entry;

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

    public static  String xmlToJson(String xmlStr){
        SAXBuilder sbder = new SAXBuilder();
        Map<String, Object> map = new HashMap<String, Object>();
        Document document;
        try {
            StringReader reader = new StringReader(xmlStr);
            InputSource ins = new InputSource(reader);
            document = sbder.build(ins);
            //获取根节点
            Element el =  document.getRootElement();
            List<Element> eList =  el.getChildren();
            Map<String, Object> rootMap = new HashMap<String, Object>();
            //得到递归组装的map
            rootMap = xmlToMap(eList,rootMap);
            map.put(el.getName(), rootMap);
            return JSON.parseObject(JSON.toJSONString(map)).toString();
        } catch (Exception e) {
            return JSON.parseObject("{}").toString();
        }
    }

    private static Map<String, Object> xmlToMap(List<Element> eList,Map<String, Object> map){
        for (Element e : eList) {
            Map<String, Object> eMap = new HashMap<String, Object>();
            List<Element> elementList = e.getChildren();
            if(elementList!=null&&elementList.size()>0){
                eMap = xmlToMap(elementList,eMap);
                Object obj = map.get(e.getName());
                if(obj!=null){
                    List<Object> olist = new ArrayList<Object>();
                    if(obj.getClass().getName().equals("java.util.HashMap")){
                        olist.add(obj);
                        olist.add(eMap);
                    }else if(obj.getClass().getName().equals("java.util.ArrayList")){
                        olist = (List<Object>)obj;
                        olist.add(eMap);
                    }
                    map.put(e.getName(), olist);
                }else{
                    map.put(e.getName(), eMap);
                }
            }else{
                map.put(e.getName(), e.getValue());
            }
        }
        return map;
    }
}
