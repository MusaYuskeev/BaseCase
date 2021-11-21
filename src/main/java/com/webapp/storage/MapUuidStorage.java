package com.webapp.storage;

import com.webapp.model.Resume;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MapUuidStorage extends AbstractStorage<String> implements Storage {
    private HashMap<String, Resume> storage = new HashMap<>();

    @Override
    protected String getSearchKey(String uuid) {
        return uuid;
    }

    @Override
    protected boolean IsExist(String searchKey) {
        return storage.containsKey(searchKey);
    }

    @Override
    protected void doUpdate(Resume r, String searchKey) {


        storage.put(searchKey, r);
    }

    @Override
    protected Resume doGet(String searchKey) {
        return storage.get(searchKey);
    }

    @Override
    protected List<Resume> doCopyAll() {
        return new ArrayList<>(storage.values());
    }


    @Override
    public void doSave(Resume resume, String searchKey) {
        storage.put(searchKey, resume);
    }

    @Override
    public void doDelete(String searchKey) {
        storage.remove((searchKey));
    }

    @Override
    public void clear() {
        storage.clear();
    }


    @Override
    public int size() {
        return storage.size();
    }


}
