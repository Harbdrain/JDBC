package com.danil.crud.repository;

import java.util.List;

import com.danil.crud.model.Label;

public interface LabelRepository extends GenericRepository<Label, Integer> {
    List<Label> getByIds(List<Integer> ids);
}
