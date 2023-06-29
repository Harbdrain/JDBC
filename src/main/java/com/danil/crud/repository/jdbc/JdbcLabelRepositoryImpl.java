package com.danil.crud.repository.jdbc;

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
    @Override
    public Label create(Label label) {
        final String SQL = "INSERT INTO labels (name, status) VALUES (?, ?)";

        try (PreparedStatement statement = RepositoryUtils.getPreparedStatement(SQL,
                Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, label.getName());
            statement.setInt(2, label.getStatus().getCode());
            statement.executeUpdate();
            ResultSet keys = statement.getGeneratedKeys();
            if (keys.next()) {
                int id = keys.getInt(1);
                label.setId(id);
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
        try (PreparedStatement statement = RepositoryUtils.getPreparedStatement(SQL)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Label label = getLabelFromResulSet(resultSet);
                result.add(label);
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return result;
    }

    private Label getLabelFromResulSet(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt(1);
        String name = resultSet.getString(2);
        LabelStatus status = LabelStatus.getStatus(resultSet.getString(3));

        Label label = new Label();
        label.setId(id);
        label.setName(name);
        label.setStatus(status);
        return label;
    }

    @Override
    public Label getById(Integer targetId) {
        final String SQL = "SELECT id, name, status FROM labels WHERE id = ? AND status != 'DELETED'";
        Label result = null;

        try (PreparedStatement statement = RepositoryUtils.getPreparedStatement(SQL)) {
            statement.setInt(1, targetId);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                result = getLabelFromResulSet(resultSet);
            }
        } catch (SQLException e) {
            System.out.println(e);
        }

        return result;
    }

    @Override
    public Label update(Label label) {
        final String SQL = "UPDATE labels SET name = ? WHERE id = ?";
        try (PreparedStatement statement = RepositoryUtils.getPreparedStatement(SQL)) {
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
        try (PreparedStatement statement = RepositoryUtils.getPreparedStatement(SQL)) {
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
