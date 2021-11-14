package com.webapp.storage;

import com.webapp.exception.NotExistStorageException;
import com.webapp.model.ContactType;
import com.webapp.model.Resume;
import com.webapp.model.Section;
import com.webapp.model.SectionType;
import com.webapp.sql.ConnectionFactory;
import com.webapp.sql.SqlExecutor;
import com.webapp.sql.SqlHelper;
import com.webapp.sql.SqlTransaction;
import com.webapp.util.JsonParser;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SqlStorage implements Storage {
    public final SqlHelper sqlHelper;

    public SqlStorage(String dbUrl, String dbUser, String dbPassword) {

        sqlHelper = new SqlHelper(new ConnectionFactory() {
            @Override
            public Connection getConnection() throws SQLException {
                return DriverManager.getConnection(dbUrl, dbUser, dbPassword);
            }
        });
    }

    @Override
    public void clear() {
        sqlHelper.execute("DELETE FROM resume r");
    }

    @Override
    public void update(Resume r) {
        sqlHelper.transactionalExecute(new SqlTransaction<Object>() {
            @Override
            public Object execute(Connection conn) throws SQLException {
                try (PreparedStatement ps = conn.prepareStatement("UPDATE resume SET full_name=? WHERE uuid=?")) {
                    ps.setString(1, r.getFullName());
                    ps.setString(2, r.getUuid());
                    if (ps.executeUpdate() != 1) {
                        throw new NotExistStorageException(r.getUuid());
                    }
                    deleteContacts(conn, r);
                    deleteSections(conn, r);
                    insertContacts(conn, r);
                    insertSections(conn, r);
                    return null;
                }
            }
        });
    }


    @Override
    public void save(Resume r) {
        sqlHelper.transactionalExecute(new SqlTransaction<Object>() {
            @Override
            public Object execute(Connection conn) throws SQLException {
                try (PreparedStatement ps = conn.prepareStatement("INSERT INTO resume (uuid, full_name) VALUES (?,?)")) {
                    ps.setString(1, r.getUuid());
                    ps.setString(2, r.getFullName());
                    ps.execute();
                }
                insertContacts(conn, r);
                insertSections(conn, r);
                return null;
            }
        });
    }


    @Override
    public Resume get(String uuid) {
        return sqlHelper.transactionalExecute(new SqlTransaction<Resume>() {
            @Override
            public Resume execute(Connection conn) throws SQLException {
                Resume resume;
                try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM resume WHERE uuid= ?")) {
                    ps.setString(1, uuid);
                    ResultSet rs = ps.executeQuery();
                    if (!rs.next()) {
                        throw new NotExistStorageException(uuid);
                    }
                    resume = new Resume(uuid, rs.getString("full_name"));
                }
                try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM contact WHERE resume_uuid= ?")) {
                    ps.setString(1, uuid);
                    ResultSet rs = ps.executeQuery();
                    while (rs.next()) {
                        addContact(rs, resume);
                    }
                }
                try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM section WHERE resume_uuid= ?")) {
                    ps.setString(1, uuid);
                    ResultSet rs = ps.executeQuery();
                    while (rs.next()) {
                        addSection(rs, resume);
                    }
                }
                return resume;
            }
        });
    }

    @Override
    public void delete(String uuid) {
        sqlHelper.execute("DELETE FROM resume WHERE uuid=?", new SqlExecutor<Object>() {
            @Override
            public Object execute(PreparedStatement ps) throws SQLException {
                ps.setString(1, uuid);
                if (ps.executeUpdate() == 0) {
                    throw new NotExistStorageException(uuid);
                }
                return null;
            }
        });
    }

    @Override
    public List<Resume> getAllSorted() {
        return sqlHelper.transactionalExecute(new SqlTransaction<List<Resume>>() {
            @Override
            public List<Resume> execute(Connection conn) throws SQLException {
                Map<String, Resume> map = new LinkedHashMap<>();
                try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM resume r  ORDER BY full_name, uuid")) {
                    ResultSet rs = ps.executeQuery();
                    while (rs.next()) {
                        String uuid = rs.getString("uuid");
                        map.put(uuid, new Resume(uuid, rs.getString("full_name")));
                    }
                }

                try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM contact ")) {
                    ResultSet rs = ps.executeQuery();
                    while (rs.next()) {
                        Resume r = map.get(rs.getString("resume_uuid"));
                        addContact(rs, r);
                    }
                }
                try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM section ")) {
                    ResultSet rs = ps.executeQuery();
                    while (rs.next()) {
                        Resume r = map.get(rs.getString("resume_uuid"));
                        addSection(rs, r);
                    }
                }
                return new ArrayList<>(map.values());
            }
        });
    }

    @Override
    public int size() {
        return sqlHelper.execute("SELECT count(*) FROM resume r ", new SqlExecutor<Integer>() {
            @Override
            public Integer execute(PreparedStatement ps) throws SQLException {
                ResultSet rs = ps.executeQuery();
                if (!rs.next()) {
                    return 0;
                } else {
                    return rs.getInt(1);
                }
            }
        });
    }

    private void addContact(ResultSet rs, Resume r) throws SQLException {
        String type = rs.getString("type");
        if (type != null) {
            r.addContact(ContactType.valueOf(type), rs.getString("value"));
        }
    }

    private void addSection(ResultSet rs, Resume r) throws SQLException {
        String type = rs.getString("type");
        if (type != null) {
            r.addSection(SectionType.valueOf(type), JsonParser.read(rs.getString("content"), Section.class));
        }
    }

    private void deleteContacts(Connection conn, Resume r) throws SQLException {
        deleteParts(conn, r, "contact");

    }

    private void deleteSections(Connection conn, Resume r) throws SQLException {
        deleteParts(conn, r, "section");

    }

    private void deleteParts(Connection conn, Resume r, String partName) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM " + partName + " WHERE resume_uuid=?")) {
            ps.setString(1, r.getUuid());
            ps.execute();
        }
    }


    private void insertContacts(Connection conn, Resume r) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("INSERT INTO contact (resume_uuid, type, value) VALUES (?,?,?)")) {
            for (Map.Entry<ContactType, String> e : r.getContacts().entrySet()) {
                ps.setString(1, r.getUuid());
                ps.setString(2, e.getKey().name());
                ps.setString(3, e.getValue());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private void insertSections(Connection conn, Resume r) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("INSERT INTO section (resume_uuid, type, content) VALUES (?,?,?)")) {
            for (Map.Entry<SectionType, Section> entry : r.getSections().entrySet()) {
                ps.setString(1, r.getUuid());
                ps.setString(2, entry.getKey().toString());
                String content = JsonParser.write(entry.getValue(), Section.class);
                ps.setString(3, content);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }
}
