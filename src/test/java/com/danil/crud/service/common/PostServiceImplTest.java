package com.danil.crud.service.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.danil.crud.model.Label;
import com.danil.crud.model.LabelStatus;
import com.danil.crud.model.Post;
import com.danil.crud.model.PostStatus;
import com.danil.crud.model.Writer;
import com.danil.crud.model.WriterStatus;
import com.danil.crud.repository.LabelRepository;
import com.danil.crud.repository.PostRepository;
import com.danil.crud.repository.WriterRepository;
import com.danil.crud.service.PostService;

public class PostServiceImplTest {
    private LabelRepository labelRepository = mock();
    private PostRepository postRepository = mock();
    private WriterRepository writerRepository = mock();
    private PostService postService = new PostServiceImpl(labelRepository, postRepository, writerRepository);

    private List<Post> posts;
    private Label label;

    @Before
    public void init() {
        setVars();
        setWriterRepositoryGetById();
        setPostRepositoryCreate();
        setPostRepositoryUpdate();
        setPostRepositoryGetById();
        setLabelRepositoryGetById();
        setPostRepositoryGetAll();
    }

    private void setVars() {
        posts = new ArrayList<>();
        Post post = new Post();
        post.setId(1);
        post.setCreated(10L);
        post.setUpdated(10L);
        post.setContent("post 1");
        post.setStatus(PostStatus.ACTIVE);
        post.setLabels(new ArrayList<>());
        posts.add(post);

        label = new Label();
        label.setId(1);
        label.setName("tag 1");
        label.setStatus(LabelStatus.ACTIVE);
    }

    private void setWriterRepositoryGetById() {
        Writer writer = new Writer();
        writer.setId(1);
        writer.setFirstName("Danil");
        writer.setLastName("Demchenko");
        writer.setPosts(new ArrayList<>());
        writer.setStatus(WriterStatus.ACTIVE);
        when(writerRepository.getById(1)).thenReturn(writer);
        when(writerRepository.getById(1000)).thenReturn(null);

        writer = new Writer();
        writer.setId(2000);
        writer.setFirstName("Danil");
        writer.setLastName("Demchenko");
        writer.setPosts(new ArrayList<>());
        writer.setStatus(WriterStatus.DELETED);
        when(writerRepository.getById(2000)).thenReturn(writer);
    }

    private void setPostRepositoryCreate() {
        when(postRepository.create(any(Post.class))).thenReturn(posts.get(0));
    }

    private void setPostRepositoryUpdate() {
        Post post = new Post();
        post.setId(1);
        post.setCreated(10L);
        post.setUpdated(20L);
        post.setContent("new post");
        post.setStatus(PostStatus.UNDER_REVIEW);
        post.setLabels(new ArrayList<>());

        when(postRepository.update(any(Post.class))).thenReturn(post);
    }

    private void setPostRepositoryGetById() {
        when(postRepository.getById(1)).thenReturn(posts.get(0));
        when(postRepository.getById(1000)).thenReturn(null);
    }

    private void setLabelRepositoryGetById() {
        when(labelRepository.getById(1)).thenReturn(label);
        when(labelRepository.getById(1000)).thenReturn(null);
    }

    private void setPostRepositoryGetAll() {
        when(postRepository.getAll()).thenReturn(posts);
    }

    @Test
    public void createTest() {
        Post result = postService.create(1, "post 1");
        assertEquals(posts.get(0), result);
    }

    @Test
    public void createTestBadInput() {
        Post result = postService.create(-1, "post 1");
        assertNull(result);
        result = postService.create(1, "");
        assertNull(result);
        result = postService.create(1000, "post 1");
        assertNull(result);
        result = postService.create(2000, "post 1");
        assertNull(result);
    }

    @Test
    public void updateTest() {
        Post result = postService.update(1, "new post");
        assertEquals("new post", result.getContent());
        assertEquals(new Long(20), result.getUpdated());
        assertEquals(PostStatus.UNDER_REVIEW, result.getStatus());
    }

    @Test
    public void updateTestBadInput() {
        Post result = postService.update(-10, "new post");
        assertNull(result);
        result = postService.update(11, "");
        assertNull(result);
        result = postService.update(1000, "new post");
        assertNull(result);
    }

    @Test
    public void addLabelTest() {
        Post post = new Post();
        post.setId(1);
        post.setCreated(10L);
        post.setUpdated(10L);
        post.setContent("post 1");
        post.setStatus(PostStatus.ACTIVE);
        post.setLabels(new ArrayList<>());
        post.addLabel(label);
        when(postRepository.update(any(Post.class))).thenReturn(post);
        Post result = postService.addLabel(1, 1);
        assertEquals(posts.get(0), result);
    }

    @Test
    public void addLabelTestBadInput() {
        Post result = postService.addLabel(-1, 1);
        assertNull(result);
        result = postService.addLabel(1, -1);
        assertNull(result);
        result = postService.addLabel(1000, 1);
        assertNull(result);
        result = postService.addLabel(1, 1000);
        assertNull(result);
    }

    @Test
    public void deleteByIdTest() {
        postService.deleteById(1);
        postService.deleteById(-1);
    }

    @Test
    public void dellabelTest() {
        Post postToReturn = new Post();
        postToReturn.setId(1);
        postToReturn.setCreated(10L);
        postToReturn.setUpdated(10L);
        postToReturn.setContent("post 1");
        postToReturn.setStatus(PostStatus.ACTIVE);
        postToReturn.setLabels(new ArrayList<>());
        when(postRepository.update(any(Post.class))).thenReturn(postToReturn);

        posts.get(0).addLabel(label);
        Post result = postService.dellabel(1, 1);
        assertEquals(postToReturn, result);
        posts.get(0).addLabel(label);
        result = postService.dellabel(1, 1000);
        assertEquals(postToReturn, result);
    }

    @Test
    public void dellabelTestBadInpit() {
        Post result = postService.dellabel(-1, 1);
        assertNull(result);
        result = postService.dellabel(1, -1);
        assertNull(result);
        result = postService.dellabel(1000, 1);
        assertNull(result);
    }

    @Test
    public void listTest() {
        List<Post> result = postService.list();
        assertEquals(posts, result);
    }

    @Test
    public void getByIdTest() {
        Post result = postService.getById(1);
        assertEquals(posts.get(0), result);
    }

    @Test
    public void getByIdTestBadInput() {
        Post result = postService.getById(-1000);
        assertNull(result);
    }
}
