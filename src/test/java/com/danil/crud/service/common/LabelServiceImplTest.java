package com.danil.crud.service.common;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import com.danil.crud.model.Label;
import com.danil.crud.model.LabelStatus;
import com.danil.crud.repository.LabelRepository;
import com.danil.crud.service.LabelService;

public class LabelServiceImplTest {
    private LabelRepository labelRepository = mock();
    private LabelService labelService = new LabelServiceImpl(labelRepository);

    private List<Label> labels;

    @Before
    public void init() {
        setVars();
        setLabelRepositoryCreate();
        setLabelRepositoryGetById();
        setLabelRepositoryUpdate();
        setLabelRepositoryGetAll();
    }

    private void setVars() {
        List<Label> labels = new ArrayList<>();

        Label label = new Label();
        label.setId(1);
        label.setName("tag 1");
        label.setStatus(LabelStatus.ACTIVE);
        labels.add(label);

        label = new Label();
        label.setId(2);
        label.setName("tag 2");
        label.setStatus(LabelStatus.ACTIVE);
        labels.add(label);
    }

    private void setLabelRepositoryCreate() {
        Integer id = 1;
        String labelName = "tag 1";
        LabelStatus status = LabelStatus.ACTIVE;

        Label label = new Label();
        label.setName(labelName);
        label.setStatus(status);

        Label labelToReturn = new Label();
        labelToReturn.setId(id);
        labelToReturn.setName(label.getName());
        labelToReturn.setStatus(label.getStatus());

        when(labelRepository.create(label)).thenReturn(labelToReturn);
    }

    private void setLabelRepositoryUpdate() {
        Label label = new Label();
        label.setId(1);
        label.setName("new tag");
        label.setStatus(LabelStatus.ACTIVE);

        when(labelRepository.update(label)).thenReturn(label);
    }

    private void setLabelRepositoryGetById() {
        Label labelToReturn = new Label();
        labelToReturn.setId(1);
        labelToReturn.setName("tag 1");
        labelToReturn.setStatus(LabelStatus.ACTIVE);
        when(labelRepository.getById(1)).thenReturn(labelToReturn);

        when(labelRepository.getById(1000)).thenReturn(null);
    }

    private void setLabelRepositoryGetAll() {
        when(labelRepository.getAll()).thenReturn(labels);
    }

    @Test
    public void createTest() {
        String labelName = "tag 1";
        Label result = labelService.create(labelName);
        assertEquals(new Integer(1), result.getId());
        assertEquals(labelName, result.getName());
        assertEquals(LabelStatus.ACTIVE, result.getStatus());
    }

    @Test
    public void createTestNameIsEmpty() {
        String labelName = "";
        assertNull(labelService.create(labelName));
    }

    @Test
    public void updateTest() {
        int id = 1;
        String content = "new tag";

        Label result = labelService.update(id, content);
        assertEquals(new Integer(1), result.getId());
        assertEquals(content, result.getName());
        assertEquals(LabelStatus.ACTIVE, result.getStatus());
    }

    @Test
    public void updateTestBadId() {
        int id = -5;
        String content = "new tag";

        Label result = labelService.update(id, content);
        assertNull(result);
    }

    @Test
    public void updateTestLabelIsEmpty() {
        int id = 1;
        String content = "";

        Label result = labelService.update(id, content);
        assertNull(result);
    }

    @Test
    public void updateTestNoSuchLabelWithId() {
        int id = 1000;
        String content = "content";

        Label result = labelService.update(id, content);
        assertNull(result);
    }

    @Test
    public void deleteByIdTest() {
        int id = 1;
        labelService.deleteById(id);
        when(labelRepository.getById(1)).thenReturn(null);

        Label label = labelRepository.getById(1);
        assertNull(label);
    }

    @Test
    public void deleteByIdTestBadId() {
        int id = -5;
        labelService.deleteById(id);
    }

    @Test
    public void listTest() {
        List<Label> result = labelService.list();
        assertEquals(labels, result);
    }

    @Test
    public void getByIdTest() {
        Label result = labelService.getById(1);
        assertEquals(new Integer(1), result.getId());
        assertEquals("tag 1", result.getName());
        assertEquals(LabelStatus.ACTIVE, result.getStatus());
    }

    @Test
    public void getByIdTestBadId() {
        Label result = labelService.getById(-10);
        assertNull(result);
    }
}
