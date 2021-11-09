package com.webapp.storage;

import com.webapp.exception.ExistStorageException;
import com.webapp.exception.NotExistStorageException;
import com.webapp.exception.StorageException;
import com.webapp.model.*;
import org.junit.Assert;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.time.Month;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public abstract class AbstractStorageTest {
    protected static final File STORAGE_DIR = new File("C:\\Users\\Муса\\IdeaProjects\\basejava\\storage");
    protected final Storage storage;
    private static final String UUID_1 = "uuid1";
    private static final String UUID_2 = "uuid2";
    private static final String UUID_3 = "uuid3";
    private static final String UUID_4 = "uuid4";
    private static final Resume R1;
    private static final Resume R2;
    private static final Resume R3;
    private static final Resume R4;

    static {
        R1 = new Resume(UUID_1, "Name1");
        R2 = new Resume(UUID_2, "Name2");
        R3 = new Resume(UUID_3, "Name3");
        R4 = new Resume(UUID_4, "Name4");
        R1.addContact(ContactType.MAIL, "a1@mail.ru");
        R1.addContact(ContactType.PHONE, "212-85-06");
        R1.addSection(SectionType.PERSONAL, new TextSection("Personal Data"));
        R1.addSection(SectionType.OBJECTIVE, new TextSection("Objective 1"));
        R1.addSection(SectionType.ACHIEVEMENT, new ListSection("Achivement 1", "Achivement 2", "Achivement 3"));
        R1.addSection(SectionType.QUALIFICATIONS, new ListSection("Java", "SQL", "Perl"));
        R1.addSection(SectionType.EXPERIENCE, new OrganizationSection(
                new Organization("Org 1", "http://www.org1.ru",
                        new Organization.Position(2005, Month.APRIL, "position 1", "content 1"),
                        new Organization.Position(2001, Month.DECEMBER, "position 2", "content 2")
                )));
        R1.addSection(SectionType.EDUCATION, new OrganizationSection(
                new Organization("Uni", "http://www.tusur.ru",
                        new Organization.Position(1991, Month.JUNE, 1998, Month.JULY, "student", "IT faculty"),
                        new Organization.Position(2021, Month.JUNE, 2021, Month.NOVEMBER, "student", "Java Profeccional")
                ),
                new Organization("IT Academy", "http://www.itacademy.ru")
        ));
        R2.addContact(ContactType.MAIL, "NumberTwo@rambler.ru");
        R2.addContact(ContactType.SKYPE, "NumberTwo");
        R1.addSection(SectionType.EXPERIENCE, new OrganizationSection(
                new Organization("NewOrder", "http://www.orderNow.ru",
                        new Organization.Position(2015, Month.APRIL, "first 1", "desc 1"))));
    }

    protected AbstractStorageTest(Storage storage) {
        this.storage = storage;
    }

    @Before
    public void setUp() throws Exception {
        storage.clear();
        storage.save(R1);
        storage.save(R2);
        storage.save(R3);
    }

    @Test
    public void clear() throws Exception {
        storage.clear();
        assertSize(0);
    }

    @Test(expected = NotExistStorageException.class)
    public void updateNotExist() throws Exception {
        storage.update(new Resume("dummy"));
    }

    @Test
    public void update() throws Exception {
        Resume newResume = new Resume(UUID_1, "NewName");
        storage.update(newResume);
        assertTrue(newResume.equals( storage.get(UUID_1)));
    }

    @Test
    public void size() throws Exception {
        assertSize(3);
    }

    @Test
    public void get() throws Exception {
        assertGet(R1);
        assertGet(R2);
        assertGet(R3);
    }

    @Test(expected = NotExistStorageException.class)
    public void getNotExist() throws Exception {
        storage.get("dummy");
    }

    @Test
    public void getAll() throws Exception {
        List<Resume> list = storage.getAllSorted();
        assertEquals(3, list.size());
        assertEquals(list, Arrays.asList(R1, R2, R3));
    }

    @Test
    public void save() throws Exception {
        storage.save(R4);
        assertSize(4);
        assertGet(R4);
    }

    @Test(expected = ExistStorageException.class)
    public void saveExist() throws Exception {
        storage.save(R1);
    }


    @Test(expected = NotExistStorageException.class)
    public void delete() throws Exception {
        storage.delete(UUID_1);
        assertSize(2);
        storage.get(UUID_1);
    }

    @Test(expected = NotExistStorageException.class)
    public void deleteNotExist() throws Exception {
        storage.delete("dummy");
    }

    private void assertGet(Resume resume) {
        assertEquals(resume, storage.get(resume.getUuid()));
    }

    private void assertSize(int size) {
        assertEquals(size, storage.size());
    }
}