package com.danil.crud.controller;

import java.util.List;

import com.danil.crud.model.Label;
import com.danil.crud.repository.jdbc.JdbcLabelRepositoryImpl;
import com.danil.crud.service.LabelService;
import com.danil.crud.service.common.LabelServiceImpl;

public class LabelController {
    private LabelService labelService = new LabelServiceImpl(new JdbcLabelRepositoryImpl());

    public Label create(String name) {
        return labelService.create(name);
    }

    public Label update(int id, String content) {
        return labelService.update(id, content);
    }

    public void deleteById(int id) {
        labelService.deleteById(id);
    }

    public List<Label> list() {
        return labelService.list();
    }

    public Label getById(int id) {
        return labelService.getById(id);
    }
}
