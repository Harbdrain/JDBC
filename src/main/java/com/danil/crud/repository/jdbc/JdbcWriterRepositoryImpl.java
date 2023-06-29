package com.danil.crud.repository.jdbc;

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
    PostRepository postRepository = new JdbcPostRepositoryImpl();

    @Override
    public Writer create(Writer writer) {
        final String SQL = "INSERT INTO writers (first_name, last_name, status) VALUES (?, ?, ?)";
        try (PreparedStatement statement = RepositoryUtils.getPreparedStatement(SQL, Statement.RETURN_GENERATED_KEYS)) {
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
        final String SQL = "SELECT writers.id, writers.first_name, writers.last_name, writers.status, posts.id as post_id FROM writers LEFT JOIN posts ON writers.id = posts.writer_id WHERE writers.status != 'DELETED'";
        List<Writer> result = new ArrayList<>();
        try (PreparedStatement statement = RepositoryUtils.getPreparedStatement(SQL)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Writer writer = getWriterFromResultSet(resultSet);
                if (!result.isEmpty() && !writer.getPosts().isEmpty() &&result.get(result.size() - 1).getId().equals(writer.getId())) {
                    result.get(result.size() - 1).addPost(writer.getPosts().get(0));
                } else {
                    result.add(writer);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    private Writer getWriterFromResultSet(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt(1);
        String firstName = resultSet.getString(2);
        String lastName = resultSet.getString(3);
        WriterStatus status = WriterStatus.getStatus(resultSet.getString(4));
        int postId = resultSet.getInt(5);
        Post post = postRepository.getById(postId);

        Writer writer = new Writer();
        writer.setId(id);
        writer.setFirstName(firstName);
        writer.setLastName(lastName);
        writer.setStatus(status);
        writer.setPosts(new ArrayList<>());
        if (post != null) {
            writer.addPost(post);
        }

        return writer;
    }

    @Override
    public Writer getById(Integer targetId) {
        Writer result = null;
        final String SQL = "SELECT writers.id, writers.first_name, writers.last_name, writers.status, posts.id as post_id FROM writers LEFT JOIN posts ON writers.id = posts.writer_id WHERE writers.status != 'DELETED' AND writers.id = ?;";

        try (PreparedStatement statement = RepositoryUtils.getPreparedStatement(SQL)) {
            statement.setInt(1, targetId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Writer writer = getWriterFromResultSet(resultSet);
                if (result == null) {
                    result = writer;
                } else if (!result.getPosts().isEmpty()){
                    result.addPost(writer.getPosts().get(0));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    public Writer update(Writer writer) {
        final String SQL = "UPDATE writers SET first_name = ?, last_name = ?, status = ? WHERE id = ?";

        try (PreparedStatement statement = RepositoryUtils.getPreparedStatement(SQL)) {
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
        try (PreparedStatement statement = RepositoryUtils.getPreparedStatement(SQL)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
