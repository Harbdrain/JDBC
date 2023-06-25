package com.danil.crud.service;

import java.util.List;

import com.danil.crud.model.Label;

public interface LabelService {
    Label create(String name);

    Label update(int id, String content);

    void deleteById(int id);

    List<Label> list();

    Label getById(int id);
}
