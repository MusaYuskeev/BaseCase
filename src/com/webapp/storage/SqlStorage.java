package com.webapp.storage;

import com.webapp.exception.NotExistStorageException;
import com.webapp.model.ContactType;
import com.webapp.model.Resume;
import com.webapp.sql.ConnectionFactory;
import com.webapp.sql.SqlExecutor;
import com.webapp.sql.SqlHelper;
import com.webapp.sql.SqlTransaction;

import java.sql.*;
import java.util.*;

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
                    insertContacts(conn, r);
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
                //                   insertSections(conn, r);
                return null;
            }
        });
    }


    @Override
    public Resume get(String uuid) {
        return sqlHelper.execute("SELECT * FROM resume r LEFT JOIN contact c ON r.uuid=c.resume_uuid WHERE uuid=?", new SqlExecutor<Resume>() {
            @Override
            public Resume execute(PreparedStatement ps) throws SQLException {
                ps.setString(1, uuid);
                ResultSet rs = ps.executeQuery();
                if (!rs.next()) {
                    throw new NotExistStorageException(uuid);
                }
                Resume resume = new Resume(uuid, rs.getString("full_name"));
                do {
                    addContact(rs, resume);
                } while (rs.next());
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
        return sqlHelper.execute("SELECT * FROM resume r LEFT JOIN contact c ON r.uuid=c.resume_uuid ORDER BY full_name, uuid", new SqlExecutor<List<Resume>>() {
            @Override
            public List<Resume> execute(PreparedStatement ps) throws SQLException {
                Map<String, Resume> map = new LinkedHashMap<>();
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    String uuid = rs.getString("uuid");
                    Resume r = map.get(uuid);
                    if (r == null) {
                        r = new Resume(uuid, rs.getString("full_name"));
                        map.put(uuid, r);
                    }
                    addContact(rs, r);
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

    private void deleteContacts(Connection conn, Resume r) {
        sqlHelper.execute("DELETE FROM contact WHERE resume_uuid=?", new SqlExecutor<Object>() {
            @Override
            public Object execute(PreparedStatement ps) throws SQLException {
                ps.setString(1, r.getUuid());
                ps.execute();
                return null;
            }
        });
    }

    private void addContact(ResultSet rs, Resume r) throws SQLException {
        String type = rs.getString("type");
        if (type != null) {
            r.addContact(ContactType.valueOf(type), rs.getString("value"));
        }
    }
}
