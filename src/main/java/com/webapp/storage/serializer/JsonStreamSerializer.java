package com.webapp.storage.serializer;

import com.webapp.model.*;
import com.webapp.util.JsonParser;
import com.webapp.util.XmlParser;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class JsonStreamSerializer implements StreamSerializer{
    private XmlParser xmlParser;


    @Override
    public void doWrite(Resume r, OutputStream os) throws IOException {
        try (Writer w = new OutputStreamWriter(os, StandardCharsets.UTF_8)){
            JsonParser.write(r,w);
        }
    }

    @Override
    public Resume doRead(InputStream is) throws IOException {
        try (Reader r = new InputStreamReader(is, StandardCharsets.UTF_8)){
            return JsonParser.read(r, Resume.class);
        }
    }
}
