package com.danil.crud.repository.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.danil.crud.model.Post;
import com.danil.crud.model.Writer;
import com.danil.crud.model.WriterStatus;
import com.danil.crud.repository.PostRepository;
import com.danil.crud.repository.WriterRepository;
import com.danil.crud.utils.RepositoryUtils;

public class JdbcWriterRepositoryImpl implements WriterRepository {
    Connection writerConnection = null;
    PostRepository postRepository = new JdbcPostRepositoryImpl();

    public JdbcWriterRepositoryImpl() {
        try {
            Class.forName(RepositoryUtils.JDBC_DRIVER); // Legacy, no longer needed
            writerConnection = DriverManager.getConnection(
                    RepositoryUtils.DATABASE_URL,
                    RepositoryUtils.USER,
                    RepositoryUtils.PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Writer create(Writer writer) {
        final String SQL = "INSERT INTO writers (first_name, last_name, status) VALUES (?, ?, ?)";
        try (PreparedStatement statement = writerConnection.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, writer.getFirstName());
            statement.setString(2, writer.getLastName());
            statement.setInt(3, writer.getStatus().getCode());
            statement.executeUpdate();
            ResultSet keys = statement.getGeneratedKeys();
            if (keys.next()) {
                int id = keys.getInt(1);
                writer.setId(id);
            }

            for (Post post : writer.getPosts()) {
                if (postRepository.getById(post.getId()) != null) {
                    postRepository.update(post);
                } else {
                    postRepository.create(post);
                }
                postRepository.setWriter(post.getId(), writer.getId());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return writer;
    }

    @Override
    public List<Writer> getAll() {
        final String SQL = "SELECT id, first_name, last_name, status FROM writers WHERE status != 'DELETED'";
        List<Writer> result = new ArrayList<>();

        try (Statement statement = writerConnection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(SQL);
            while (resultSet.next()) {
                int id = resultSet.getInt(1);
                String firstName = resultSet.getString(2);
                String lastName = resultSet.getString(3);
                WriterStatus status = WriterStatus.getStatus(resultSet.getString(4));

                Writer writer = new Writer();
                writer.setId(id);
                writer.setFirstName(firstName);
                writer.setLastName(lastName);
                writer.setStatus(status);
                writer.setPosts(getPosts(id));

                result.add(writer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    private List<Post> getPosts(int writerId) {
        List<Integer> postIds = new ArrayList<>();
        final String SQL = "SELECT id FROM posts WHERE status != 'DELETED' AND writer_id = ?";
        try (PreparedStatement statement = writerConnection.prepareStatement(SQL)) {
            statement.setInt(1, writerId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt(1);
                postIds.add(id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return postRepository.getByIds(postIds);
    }

    @Override
    public Writer getById(Integer targetId) {
        Writer writer = null;
        final String SQL = "SELECT id, first_name, last_name, status FROM writers WHERE id = ? AND status != 'DELETED'";

        try (PreparedStatement statement = writerConnection.prepareStatement(SQL)) {
            statement.setInt(1, targetId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int id = resultSet.getInt(1);
                String firstName = resultSet.getString(2);
                String lastName = resultSet.getString(3);
                WriterStatus status = WriterStatus.getStatus(resultSet.getString(4));

                writer = new Writer();
                writer.setId(id);
                writer.setFirstName(firstName);
                writer.setLastName(lastName);
                writer.setStatus(status);
                writer.setPosts(getPosts(id));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return writer;
    }

    @Override
    public Writer update(Writer writer) {
        final String SQL = "UPDATE writers SET first_name = ?, last_name = ?, status = ? WHERE id = ?";

        try (PreparedStatement statement = writerConnection.prepareStatement(SQL)) {
            statement.setString(1, writer.getFirstName());
            statement.setString(2, writer.getLastName());
            statement.setInt(3, writer.getStatus().getCode());
            statement.setInt(4, writer.getId());
            statement.executeUpdate();

            for (Post post : writer.getPosts()) {
                if (postRepository.getById(post.getId()) != null) {
                    postRepository.update(post);
                } else {
                    postRepository.create(post);
                }
                postRepository.setWriter(post.getId(), writer.getId());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return writer;
    }

    @Override
    public void deleteById(Integer id) {
        final String SQL = "UPDATE writers SET status = 'DELETED' WHERE id = ?";
        try (PreparedStatement statement = writerConnection.prepareStatement(SQL)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
