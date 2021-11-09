package com.webapp.storage;

import com.webapp.storage.serializer.ObjectStreamSerializer;

public class ObjectFileStorageTest extends AbstractStorageTest
{
    public ObjectFileStorageTest() {
        super(new FileStorage(STORAGE_DIR,new ObjectStreamSerializer()));
    }
}