package com.heji.server.data.converts;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.bytebuddy.dynamic.scaffold.MethodGraph;
import org.springframework.stereotype.Component;

import javax.persistence.AttributeConverter;
import java.util.LinkedList;
import java.util.List;
@Component
public class PathConverter implements AttributeConverter<List<String>, String> {
    Gson gson;

    public PathConverter(Gson gson) {
        this.gson = gson;
    }

    @Override
    public String convertToDatabaseColumn(List<String> strings) {
        return gson.toJson(strings);
    }

    @Override
    public List<String> convertToEntityAttribute(String s) {
        return gson.fromJson(s,new TypeToken<LinkedList<String>>(){}.getType());
    }
}
