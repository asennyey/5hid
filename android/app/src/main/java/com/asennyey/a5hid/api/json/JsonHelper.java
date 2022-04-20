package com.asennyey.a5hid.api.json;

import android.util.JsonReader;
import android.util.JsonToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JsonHelper<T> {
    public PagedResponse<T> readPage(JsonReader reader, Parser<T> parser) throws IOException {
        PagedResponse<T> pages = new PagedResponse<T>();

        reader.beginObject();
        while(reader.hasNext()){
            String name = reader.nextName();
            System.out.println(name);
            switch(name){
                case "count":
                    pages.count = reader.nextInt();
                    break;
                case "results":
                    List<T> records = new ArrayList<>();
                    reader.beginArray();
                    while(reader.hasNext()) {
                        reader.beginObject();
                        records.add(parser.parse(reader));
                        reader.endObject();
                    }
                    reader.endArray();
                    pages.records = records;
                    break;
                case "nextPage":
                    if(reader.peek() != JsonToken.NULL)
                        pages.nextPage = reader.nextInt();
                    break;
                case "prevPage":
                    if(reader.peek() != JsonToken.NULL)
                        pages.prevPage = reader.nextInt();
                    break;
                default:
                    reader.skipValue();
            }
        }
        reader.endObject();
        return pages;
    }
}
