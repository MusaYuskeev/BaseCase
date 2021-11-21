package com.webapp.storage;

import com.webapp.exception.StorageException;
import com.webapp.model.Resume;
import com.webapp.storage.serializer.StreamSerializer;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PathStorage extends AbstractStorage<Path> {
    private final Path directory;
    private StreamSerializer serializer;

    public PathStorage(String dir, StreamSerializer serializer) {
        Objects.requireNonNull(dir, "directory must not be null");
        directory = Paths.get(dir);
        this.serializer = serializer;
        if (!Files.isDirectory(directory) || !Files.isWritable(directory)) {
            throw new IllegalArgumentException(directory + " is not a directory or is not writable");
        }
    }

    @Override
    protected Path getSearchKey(String uuid) {
        return directory.resolve(uuid);
    }

    @Override
    protected boolean IsExist(Path path) {
        return Files.isRegularFile(path);
    }

    @Override
    protected void doUpdate(Resume r, Path path) {
        try {
            serializer.doWrite(r, new BufferedOutputStream(Files.newOutputStream(path)));
        } catch (IOException e) {
            throw new StorageException("Path write error", r.getUuid(), e);
        }

    }

    @Override
    protected void doSave(Resume r, Path path) {
        try {
            Files.createFile(path);
        } catch (IOException e) {
            throw new StorageException("Couldn't create file " + path, path.getFileName().toString(), e);
        }
        doUpdate(r, path);
    }

    @Override
    protected void doDelete(Path path) {
        try {
            Files.delete(path);
        } catch (IOException e) {
            throw new StorageException("Path delete error", path.getFileName().toString(), e);
        }
    }

    @Override
    protected Resume doGet(Path path) {
        try {
            return serializer.doRead(new BufferedInputStream(Files.newInputStream(path)));
        } catch (IOException e) {
            throw new StorageException("Path read error", path.getFileName().toString(), e);
        }
    }

    @Override
    protected List<Resume> doCopyAll() {
        return getFilesList().map(this::doGet).collect(Collectors.toList());
    }

    @Override
    public void clear() {
        getFilesList().forEach(this::doDelete);

    }

    @Override
    public int size() {
        return (int) getFilesList().count();

    }

    private Stream<Path> getFilesList() {
        try {
            return Files.list(directory);
        } catch (IOException e) {
            throw new StorageException("Error get size", null, e);
        }
    }
}
