package com.webapp.storage;

import com.webapp.storage.serializer.JsonStreamSerializer;
import com.webapp.storage.serializer.XmlStreamSerializer;

public class JsonPathStorageTest extends AbstractStorageTest
{

    public JsonPathStorageTest() {
        super(new PathStorage(STORAGE_DIR.getAbsolutePath(), new JsonStreamSerializer()));
    }
}