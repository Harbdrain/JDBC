package com.danil.crud.service;

import java.util.List;

import com.danil.crud.model.Writer;

public interface WriterService {
    Writer create(String firstName, String lastName);

    Writer update(int id, String firstName, String lastName);

    void deleteById(int id);

    List<Writer> list();

    Writer getById(int id);
}
