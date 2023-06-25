package com.danil.crud.repository.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.danil.crud.model.Label;
import com.danil.crud.model.LabelStatus;
import com.danil.crud.repository.LabelRepository;
import com.danil.crud.utils.RepositoryUtils;

public class JdbcLabelRepositoryImpl implements LabelRepository {
    Connection labelConnection = null;

    public JdbcLabelRepositoryImpl() {
        try {
            Class.forName(RepositoryUtils.JDBC_DRIVER); // Legacy, no longer needed
            labelConnection = DriverManager.getConnection(
                    RepositoryUtils.DATABASE_URL,
                    RepositoryUtils.USER,
                    RepositoryUtils.PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println(e);
        }
    }

    @Override
    public Label create(Label label) {
        final String SQL_GET_BY_NAME = "SELECT * FROM labels WHERE name = ? AND status != 'DELETED'";
        final String SQL_CREATE = "INSERT INTO labels (name, status) VALUES (?, ?)";

        try (
                PreparedStatement statementCreate = labelConnection.prepareStatement(SQL_CREATE,
                        Statement.RETURN_GENERATED_KEYS);
                PreparedStatement statementGetByName = labelConnection.prepareStatement(SQL_GET_BY_NAME);) {
            statementGetByName.setString(1, label.getName());
            ResultSet resultSet = statementGetByName.executeQuery();
            if (!resultSet.next()) {
                statementCreate.setString(1, label.getName());
                statementCreate.setInt(2, label.getStatus().getCode());
                statementCreate.executeUpdate();
                ResultSet keys = statementCreate.getGeneratedKeys();
                if (keys.next()) {
                    int id = keys.getInt(1);
                    label.setId(id);
                }
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return label;
    }

    @Override
    public List<Label> getAll() {
        final String SQL = "SELECT id, name, status FROM labels WHERE status != 'DELETED'";
        List<Label> result = new ArrayList<>();
        try (Statement statement = labelConnection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(SQL);
            while (resultSet.next()) {
                int id = resultSet.getInt(1);
                String name = resultSet.getString(2);
                LabelStatus status = LabelStatus.getStatus(resultSet.getString(3));

                Label label = new Label();
                label.setId(id);
                label.setName(name);
                label.setStatus(status);
                result.add(label);
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return result;
    }

    @Override
    public Label getById(Integer targetId) {
        final String SQL = "SELECT id, name, status FROM labels WHERE id = ? AND status != 'DELETED'";
        Label result = null;

        try (PreparedStatement statement = labelConnection.prepareStatement(SQL)) {
            statement.setInt(1, targetId);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int id = resultSet.getInt(1);
                String name = resultSet.getString(2);
                LabelStatus status = LabelStatus.getStatus(resultSet.getString(3));

                result = new Label();
                result.setId(id);
                result.setName(name);
                result.setStatus(status);
            }
        } catch (SQLException e) {
            System.out.println(e);
        }

        return result;
    }

    @Override
    public Label update(Label label) {
        final String SQL = "UPDATE labels SET name = ? WHERE id = ?";
        try (PreparedStatement statement = labelConnection.prepareStatement(SQL)) {
            statement.setString(1, label.getName());
            statement.setInt(2, label.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e);
        }
        return label;
    }

    @Override
    public void deleteById(Integer id) {
        final String SQL = "UPDATE labels SET status = 'DELETED' WHERE id = ?";
        try (PreparedStatement statement = labelConnection.prepareStatement(SQL)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    @Override
    public List<Label> getByIds(List<Integer> ids) {
        List<Label> result = new ArrayList<>();
        for (int id : ids) {
            Label label = getById(id);
            if (label != null) {
                result.add(label);
            }
        }
        return result;
    }
}
