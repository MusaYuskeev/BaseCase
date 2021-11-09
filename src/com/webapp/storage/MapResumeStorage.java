package com.webapp.storage;

import com.webapp.model.Resume;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MapResumeStorage extends AbstractStorage<Resume> implements Storage {
    private HashMap<String, Resume> storage = new HashMap<>();

    @Override
    protected Resume getSearchKey(String uuid) {
        return storage.get(uuid);
    }

    @Override
    protected boolean IsExist(Resume searchKey) {
        return searchKey != null;
    }

    @Override
    protected void doUpdate(Resume resume, Resume searchKey) {
        storage.put(resume.getUuid(), resume);
    }

    @Override
    protected Resume doGet(Resume searchKey) {
        return searchKey;
    }

    @Override
    protected List<Resume> doCopyAll() {
        return new ArrayList<>(storage.values());
    }


    @Override
    public void doSave(Resume resume, Resume searchKey) {
        storage.put(resume.getUuid(), resume);
    }

    @Override
    public void doDelete(Resume searchKey) {
        storage.remove(searchKey.getUuid());
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
