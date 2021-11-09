package com.webapp.storage;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses(
        {
                ArrayStorageTest.class,
                SortedArrayStorageTest.class,
                ListStorageTest.class,
                MapUuidStorageTest.class,
                ObjectFileStorageTest.class,
                ObjectPathStorageTest.class,
                MapResumeStorageTest.class,
                XmltPathStorageTest.class,
                JsonPathStorageTest.class,
                DataPathStorageTest.class
        }
)
public class AllStorageTest {
}
