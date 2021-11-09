package com.webapp.storage;

import com.webapp.storage.serializer.DataStreamSerializer;
import com.webapp.storage.serializer.ObjectStreamSerializer;

public class DataPathStorageTest extends AbstractStorageTest
{

    public DataPathStorageTest() {
        super(new PathStorage(STORAGE_DIR.getAbsolutePath(), new DataStreamSerializer()));
    }
}