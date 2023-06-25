package com.danil.crud.service.common;

import java.util.ArrayList;
import java.util.List;

import com.danil.crud.model.Writer;
import com.danil.crud.model.WriterStatus;
import com.danil.crud.repository.WriterRepository;
import com.danil.crud.service.WriterService;

public class WriterServiceImpl implements WriterService {
    private final WriterRepository writerRepository;

    public WriterServiceImpl(WriterRepository writerRepository) {
        this.writerRepository = writerRepository;
    }

    @Override
    public Writer create(String firstName, String lastName) {
        if (firstName.isEmpty() || lastName.isEmpty()) {
            return null;
        }

        Writer writer = new Writer();
        writer.setFirstName(firstName);
        writer.setLastName(lastName);
        writer.setStatus(WriterStatus.ACTIVE);
        writer.setPosts(new ArrayList<>());
        return writerRepository.create(writer);
    }

    @Override
    public Writer update(int id, String firstName, String lastName) {
        if (id < 0 || firstName.isEmpty() || lastName.isEmpty()) {
            return null;
        }

        Writer writer = writerRepository.getById(id);
        if (writer == null) {
            return null;
        }

        writer.setFirstName(firstName);
        writer.setLastName(lastName);
        return writerRepository.update(writer);
    }

    @Override
    public void deleteById(int id) {
        if (id < 0) {
            return;
        }

        writerRepository.deleteById(id);
    }

    @Override
    public List<Writer> list() {
        return writerRepository.getAll();
    }

    @Override
    public Writer getById(int id) {
        if (id < 0) {
            return null;
        }

        return writerRepository.getById(id);
    }
}
