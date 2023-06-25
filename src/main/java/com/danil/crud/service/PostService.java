package com.danil.crud.service;

import java.util.List;

import com.danil.crud.model.Post;

public interface PostService {
    Post create(int writerId, String content);

    Post update(int postId, String content);

    Post addLabel(int postId, int labelId);

    void deleteById(int id);

    Post dellabel(int postId, int labelId);

    List<Post> list();

    Post getById(int id);
}
