package com.danil.crud.repository;

import java.util.List;

import com.danil.crud.model.Post;

public interface PostRepository extends GenericRepository<Post, Integer> {
    void setWriter(int postId, int writerId);
    List<Post> getByIds(List<Integer> ids);
}
