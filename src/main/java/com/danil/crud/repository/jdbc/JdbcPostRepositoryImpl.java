package com.danil.crud.repository.jdbc;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.danil.crud.model.Label;
import com.danil.crud.model.Post;
import com.danil.crud.model.PostStatus;
import com.danil.crud.repository.LabelRepository;
import com.danil.crud.repository.PostRepository;
import com.danil.crud.utils.RepositoryUtils;

public class JdbcPostRepositoryImpl implements PostRepository {
    LabelRepository labelRepository = new JdbcLabelRepositoryImpl();

    @Override
    public Post create(Post post) {
        final String SQL = "INSERT INTO posts (content, created, updated, status) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = RepositoryUtils.getPreparedStatement(SQL, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, post.getContent());
            statement.setLong(2, post.getCreated());
            statement.setLong(3, post.getUpdated());
            statement.setInt(4, post.getStatus().getCode());
            statement.executeUpdate();
            ResultSet keys = statement.getGeneratedKeys();
            if (keys.next()) {
                int id = keys.getInt(1);
                post.setId(id);
            }

            for (Label label : post.getLabels()) {
                if (labelRepository.getById(label.getId()) != null) {
                    labelRepository.update(label);
                } else {
                    labelRepository.create(label);
                }
                createPostLabelRelationship(post, label);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return post;
    }

    private void createPostLabelRelationship(Post post, Label label) {
        final String SQL_GET = "SELECT * FROM post_label_relationship WHERE post_id = ? AND label_id = ?";
        final String SQL_CREATE = "INSERT INTO post_label_relationship (post_id, label_id) VALUES (?, ?)";
        try (
                PreparedStatement statementGet = RepositoryUtils.getPreparedStatement(SQL_GET);
                PreparedStatement statementCreate = RepositoryUtils.getPreparedStatement(SQL_CREATE);) {
            statementGet.setInt(1, post.getId());
            statementGet.setInt(2, label.getId());
            ResultSet resultSet = statementGet.executeQuery();
            if (!resultSet.next()) {
                statementCreate.setInt(1, post.getId());
                statementCreate.setInt(2, label.getId());
                statementCreate.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Post> getAll() {
        final String SQL = "SELECT posts.id, posts.content, posts.created, posts.updated, posts.status, post_label_relationship.label_id as label_id FROM posts LEFT JOIN post_label_relationship ON posts.id = post_label_relationship.post_id LEFT JOIN labels ON post_label_relationship.label_id = labels.id WHERE posts.status != 'DELETED'";
        List<Post> result = new ArrayList<>();
        try (PreparedStatement statement = RepositoryUtils.getPreparedStatement(SQL)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Post post = getPostFromResultSet(resultSet);
                if (!result.isEmpty() && result.get(result.size() - 1).getId().equals(post.getId())) {
                    result.get(result.size() - 1).addLabel(post.getLabels().get(0));
                } else {
                    result.add(post);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    private Post getPostFromResultSet(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt(1);
        String content = resultSet.getString(2);
        long created = resultSet.getLong(3);
        long updated = resultSet.getLong(4);
        PostStatus status = PostStatus.getStatus(resultSet.getString(5));
        int labelId = resultSet.getInt(6);
        Label label = labelRepository.getById(labelId);

        Post post = new Post();
        post.setId(id);
        post.setContent(content);
        post.setCreated(created);
        post.setUpdated(updated);
        post.setStatus(status);
        post.setLabels(new ArrayList<>());
        if (label != null) {
            post.addLabel(label);
        }
        return post;
    }

    @Override
    public Post getById(Integer targetId) {
        final String SQL = "SELECT posts.id, posts.content, posts.created, posts.updated, posts.status, post_label_relationship.label_id as label_id FROM posts LEFT JOIN post_label_relationship ON posts.id = post_label_relationship.post_id LEFT JOIN labels ON post_label_relationship.label_id = labels.id WHERE posts.status != 'DELETED' AND posts.id = ?;";
        Post result = null;

        try (PreparedStatement statement = RepositoryUtils.getPreparedStatement(SQL)) {
            statement.setInt(1, targetId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Post post = getPostFromResultSet(resultSet);
                if (result == null) {
                    result = post;
                } else {
                    result.addLabel(post.getLabels().get(0));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    public Post update(Post post) {
        final String SQL = "UPDATE posts SET content = ?, created = ?, updated = ?, status = ? WHERE id = ?";
        try (PreparedStatement statement = RepositoryUtils.getPreparedStatement(SQL)) {
            statement.setString(1, post.getContent());
            statement.setLong(2, post.getCreated());
            statement.setLong(3, post.getUpdated());
            statement.setInt(4, post.getStatus().getCode());
            statement.setInt(5, post.getId());
            statement.executeUpdate();

            List<Integer> labelsToRetain = new ArrayList<>();
            for (Label label : post.getLabels()) {
                if (labelRepository.getById(label.getId()) != null) {
                    labelRepository.update(label);
                } else {
                    labelRepository.create(label);
                }
                createPostLabelRelationship(post, label);
                labelsToRetain.add(label.getId());
            }
            retainPostLabelRelationshipByLabelIds(post.getId(), labelsToRetain);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return post;
    }

    void retainPostLabelRelationshipByLabelIds(int postId, List<Integer> labelIds) {
        StringBuilder builder = new StringBuilder();
        builder.append("DELETE FROM post_label_relationship WHERE post_id = ?");
        if (!labelIds.isEmpty()) {
            builder.append(" AND label_id NOT IN(");
            for (int i = 0; i < labelIds.size() - 1; i++) {
                builder.append("?, ");
            }
            builder.append("?)");
        }
        final String SQL = builder.toString();

        try (PreparedStatement statement = RepositoryUtils.getPreparedStatement(SQL)) {
            statement.setInt(1, postId);
            for (int i = 0; i < labelIds.size(); i++) {
                statement.setInt(i + 2, labelIds.get(i));
            }
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteById(Integer id) {
        final String SQL = "UPDATE posts SET status = 'DELETED' WHERE id = ?";
        try (PreparedStatement statement = RepositoryUtils.getPreparedStatement(SQL)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setWriter(int postId, int writerId) {
        final String SQL = "UPDATE posts SET writer_id = ? WHERE id = ?";
        try (PreparedStatement statement = RepositoryUtils.getPreparedStatement(SQL)) {
            statement.setInt(1, writerId);
            statement.setInt(2, postId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Post> getByIds(List<Integer> ids) {
        List<Post> result = new ArrayList<>();
        for (int id : ids) {
            Post post = getById(id);
            if (post != null) {
                result.add(post);
            }
        }
        return result;
    }
}
