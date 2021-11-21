package com.webapp.storage;

import com.webapp.exception.StorageException;
import com.webapp.model.Resume;

import java.util.Arrays;
import java.util.List;


public abstract class AbstractArrayStorage extends AbstractStorage<Integer> implements Storage {
    protected static final int STORAGE_LIMIT = 10000;
    protected Resume[] storage = new Resume[STORAGE_LIMIT];
    protected int size = 0;


    @Override
    protected abstract Integer getSearchKey(String uuid);

    @Override
    protected boolean IsExist(Integer searchKey) {
        return searchKey >= 0;
    }

    @Override
    public void doUpdate(Resume r, Integer searchKey) {
        storage[searchKey] = r;
    }

    @Override
    public Resume doGet(Integer searchKey) {
        return storage[searchKey];
    }

    @Override
    public void doSave(Resume r, Integer searchKey) {
        if (size == STORAGE_LIMIT) {
            throw new StorageException("Storage overflow", r.getUuid());
        } else {
            PutElement(r, searchKey);
            size++;
        }
    }

    @Override
    public void doDelete(Integer searchKey) {
        CutElement(searchKey);
        storage[size - 1] = null;
        size--;
    }

    public int size() {
        return size;
    }

    public void clear() {
        Arrays.fill(storage, 0, size, null);
        size = 0;
    }

    @Override
    public List<Resume> doCopyAll() {
        return Arrays.asList(Arrays.copyOfRange(storage, 0, size));
    }

    protected abstract void PutElement(Resume resume, int index);

    protected abstract void CutElement(int index);

}
