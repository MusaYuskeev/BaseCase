package com.webapp.storage;

import com.webapp.model.Resume;

import java.util.ArrayList;
import java.util.List;


public class ListStorage extends AbstractStorage<Integer> implements Storage {
    private List<Resume> storage = new ArrayList<>();

    @Override
    protected Integer getSearchKey(String uuid) {
        for (int i = 0; i < storage.size(); i++) {
            if (storage.get(i).getUuid().equals(uuid)) {
                return i;
            }
        }
        return null;
    }

    @Override
    protected boolean IsExist(Integer searchKey) {
        return searchKey != null;
    }

    @Override
    protected void doUpdate(Resume resume, Integer searchKey) {
        storage.set(searchKey, resume);
    }

    @Override
    protected Resume doGet(Integer searchKey) {
        return storage.get(searchKey);
    }


    @Override
    public void doSave(Resume resume, Integer searchKey) {
        storage.add(resume);
    }

    @Override
    public void doDelete(Integer searchKey) {
        storage.remove((searchKey).intValue());
    }

    @Override
    public void clear() {
        storage.clear();
    }

    @Override
    public List<Resume> doCopyAll() {
        return new ArrayList<>(storage);
    }

    @Override
    public int size() {
        return storage.size();
    }


}
