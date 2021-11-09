package com.webapp.storage;

import com.webapp.model.Resume;

/**
 * Array based storage for Resumes
 */
public class ArrayStorage extends AbstractArrayStorage {

    @Override
    protected Integer getSearchKey(String uuid) {
        for (int i = 0; i < size; i++) {
            if (uuid.equals(storage[i].getUuid())) {
                return i;
            }
        }
        return -1;
    }

    @Override
    protected void PutElement(Resume resume, int index) {
        storage[size] = resume;
    }

    @Override
    protected void CutElement(int index) {
        storage[index] = storage[size - 1];
    }


}
