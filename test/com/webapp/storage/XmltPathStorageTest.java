package com.webapp.storage;

import com.webapp.storage.serializer.ObjectStreamSerializer;
import com.webapp.storage.serializer.XmlStreamSerializer;

public class XmltPathStorageTest extends AbstractStorageTest
{

    public XmltPathStorageTest() {
        super(new PathStorage(STORAGE_DIR.getAbsolutePath(), new XmlStreamSerializer()));
    }
}