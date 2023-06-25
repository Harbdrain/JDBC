package com.danil.crud.controller;

import java.util.List;

import com.danil.crud.model.Post;
import com.danil.crud.repository.jdbc.JdbcLabelRepositoryImpl;
import com.danil.crud.repository.jdbc.JdbcPostRepositoryImpl;
import com.danil.crud.repository.jdbc.JdbcWriterRepositoryImpl;
import com.danil.crud.service.PostService;
import com.danil.crud.service.common.PostServiceImpl;

public class PostController {
    PostService postService = new PostServiceImpl(
            new JdbcLabelRepositoryImpl(),
            new JdbcPostRepositoryImpl(),
            new JdbcWriterRepositoryImpl());

    public Post create(int writerId, String content) {
        return postService.create(writerId, content);
    }

    public Post update(int postId, String content) {
        return postService.update(postId, content);
    }

    public Post addLabel(int postId, int labelId) {
        return postService.addLabel(postId, labelId);
    }

    public void deleteById(int id) {
        postService.deleteById(id);
    }

    public Post dellabel(int postId, int labelId) {
        return postService.dellabel(postId, labelId);
    }

    public List<Post> list() {
        return postService.list();
    }

    public Post getById(int id) {
        return postService.getById(id);
    }
}
