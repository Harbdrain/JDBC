package com.danil.crud.controller;

import java.util.List;

import com.danil.crud.model.Writer;
import com.danil.crud.repository.jdbc.JdbcWriterRepositoryImpl;
import com.danil.crud.service.WriterService;
import com.danil.crud.service.common.WriterServiceImpl;

public class WriterController {
    private final WriterService writerService = new WriterServiceImpl(new JdbcWriterRepositoryImpl());

    public Writer create(String firstName, String lastName) {
        return writerService.create(firstName, lastName);
    }

    public Writer update(int id, String firstName, String lastName) {
        return writerService.update(id, firstName, lastName);
    }

    public void deleteById(int id) {
        writerService.deleteById(id);
    }

    public List<Writer> list() {
        return writerService.list();
    }

    public Writer getById(int id) {
        return writerService.getById(id);
    }
}
