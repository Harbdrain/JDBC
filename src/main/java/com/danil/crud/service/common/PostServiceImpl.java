package com.danil.crud.service.common;

import java.util.ArrayList;
import java.util.List;

import com.danil.crud.model.Label;
import com.danil.crud.model.Post;
import com.danil.crud.model.PostStatus;
import com.danil.crud.model.Writer;
import com.danil.crud.repository.LabelRepository;
import com.danil.crud.repository.PostRepository;
import com.danil.crud.repository.WriterRepository;
import com.danil.crud.service.PostService;

public class PostServiceImpl implements PostService {
    private final LabelRepository labelRepository;
    private final PostRepository postRepository;
    private final WriterRepository writerRepository;

    public PostServiceImpl(LabelRepository labelRepository, PostRepository postRepository,
            WriterRepository writerRepository) {
        this.labelRepository = labelRepository;
        this.postRepository = postRepository;
        this.writerRepository = writerRepository;
    }

    @Override
    public Post create(int writerId, String content) {
        if (writerId < 0 || content.isEmpty()) {
            return null;
        }

        Writer writer = writerRepository.getById(writerId);
        if (writer == null || writer.isDeleted()) {
            return null;
        }

        Post post = new Post();
        post.setContent(content);
        Long time = System.currentTimeMillis() / 1000L;
        post.setCreated(time);
        post.setUpdated(time);
        post.setStatus(PostStatus.ACTIVE);
        post.setLabels(new ArrayList<>());
        Post result = postRepository.create(post);

        writer.addPost(result);
        writerRepository.update(writer);

        return result;
    }

    @Override
    public Post update(int postId, String content) {
        if (postId < 0 || content.isEmpty()) {
            return null;
        }

        Post post = postRepository.getById(postId);
        if (post == null) {
            return null;
        }

        Long time = System.currentTimeMillis() / 1000L;
        post.setContent(content);
        post.setStatus(PostStatus.UNDER_REVIEW);
        post.setUpdated(time);
        return postRepository.update(post);
    }

    @Override
    public Post addLabel(int postId, int labelId) {
        if (postId < 0 || labelId < 0) {
            return null;
        }

        Post post = postRepository.getById(postId);
        if (post == null) {
            return null;
        }

        Label label = labelRepository.getById(labelId);
        if (label == null) {
            return null;
        }
        post.addLabel(label);
        return postRepository.update(post);
    }

    @Override
    public void deleteById(int id) {
        if (id < 0) {
            return;
        }
        postRepository.deleteById(id);
    }

    @Override
    public Post dellabel(int postId, int labelId) {
        if (postId < 0 || labelId < 0) {
            return null;
        }

        Post post = postRepository.getById(postId);
        if (post == null) {
            return null;
        }

        post.getLabels().removeIf(e -> e.getId() == labelId);
        return postRepository.update(post);
    }

    @Override
    public List<Post> list() {
        return postRepository.getAll();
    }

    @Override
    public Post getById(int id) {
        if (id < 0) {
            return null;
        }

        return postRepository.getById(id);
    }
}
