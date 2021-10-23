package com.fastcode.example.addons.reporting;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

@Converter
public class JSONObjectConverter implements AttributeConverter<JSONObject, String> {

    @Override
    public String convertToDatabaseColumn(JSONObject jsonData) {
        String json;
        try {
            json = jsonData.toString();
        } catch (NullPointerException ex) {
            //extend error handling here if you want
            json = "";
        }
        return json;
    }

    @Override
    public JSONObject convertToEntityAttribute(String jsonDataAsJson) {
        JSONParser parser = new JSONParser();

        Object obj;
        try {
            obj = parser.parse(jsonDataAsJson);
            JSONObject jsonData = (JSONObject) obj;
            return jsonData;
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            //	e.printStackTrace();
            return null;
        }
    }
}
