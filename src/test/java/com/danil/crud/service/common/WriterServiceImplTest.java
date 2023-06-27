package com.danil.crud.service.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.danil.crud.model.Writer;
import com.danil.crud.model.WriterStatus;
import com.danil.crud.repository.WriterRepository;
import com.danil.crud.service.WriterService;

public class WriterServiceImplTest {
    private WriterRepository writerRepository = mock();
    private WriterService writerService = new WriterServiceImpl(writerRepository);

    private List<Writer> writers;

    @Before
    public void init() {
        setVars();
        setWriterRepositoryCreate();
        setWriterRepositoryGetById();
        setWriterRepositoryUpdate();
        setWriterRepositoryGetAll();
    }

    private void setVars() {
        writers = new ArrayList<>();

        Writer writer = new Writer();
        writer.setId(1);
        writer.setFirstName("Danil");
        writer.setLastName("Demchenko");
        writer.setPosts(new ArrayList<>());
        writer.setStatus(WriterStatus.ACTIVE);
        writers.add(writer);
    }

    private void setWriterRepositoryCreate() {
        Writer writer = new Writer();
        writer.setFirstName("Danil");
        writer.setLastName("Demchenko");
        writer.setStatus(WriterStatus.ACTIVE);
        writer.setPosts(new ArrayList<>());
        when(writerRepository.create(writer)).thenReturn(writers.get(0));
    }

    private void setWriterRepositoryGetById() {
        when(writerRepository.getById(1)).thenReturn(writers.get(0));
        when(writerRepository.getById(1000)).thenReturn(null);
    }

    private void setWriterRepositoryUpdate() {
        Writer writer = new Writer();
        writer.setId(1);
        writer.setFirstName("Eugene");
        writer.setLastName("Suleimanov");
        writer.setStatus(WriterStatus.ACTIVE);
        writer.setPosts(new ArrayList<>());
        when(writerRepository.update(writer)).thenReturn(writer);
    }

    private void setWriterRepositoryGetAll() {
        when(writerRepository.getAll()).thenReturn(writers);
    }

    @Test
    public void createTest() {
        Writer result = writerService.create("Danil", "Demchenko");
        assertEquals(writers.get(0), result);
    }

    @Test
    public void createTestBadInput() {
        Writer result = writerService.create("", "Demchenko");
        assertNull(result);
        result = writerService.create("Danil", "");
        assertNull(result);
    }

    @Test
    public void updateTest() {
        Writer writer = new Writer();
        writer.setId(1);
        writer.setFirstName("Eugene");
        writer.setLastName("Suleimanov");
        writer.setStatus(WriterStatus.ACTIVE);
        writer.setPosts(new ArrayList<>());

        Writer result = writerService.update(1, "Eugene", "Suleimanov");
        assertEquals(writer, result);
    }

    @Test
    public void updateTestBadInput() {
        Writer result = writerService.update(-1, "Eugene", "Suleimanov");
        assertNull(result);
        result = writerService.update(1, "", "Suleimanov");
        assertNull(result);
        result = writerService.update(1, "Eugene", "");
        assertNull(result);
        result = writerService.update(1000, "Eugene", "Suleimanov");
        assertNull(result);
    }

    @Test
    public void deleteByIdTest() {
        writerService.deleteById(1);
        when(writerRepository.getById(1)).thenReturn(null);
        assertNull(writerService.getById(1));
    }

    @Test
    public void deleteByIdTestBadId() {
        writerService.deleteById(-10);
    }

    @Test
    public void listTest() {
        List<Writer> result = writerService.list();
        assertEquals(writers, result);
    }

    @Test
    public void getByIdTest() {
        Writer result = writerService.getById(1);
        assertEquals(writers.get(0), result);
    }

    @Test
    public void getByIdTestBadId() {
        Writer result = writerService.getById(-10);
        assertNull(result);
    }
}
