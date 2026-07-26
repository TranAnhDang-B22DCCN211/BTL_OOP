package com.techstore.repository;

import java.util.List;

public interface BaseRepository<T> {
    boolean insert(T entity);

    boolean update(T entity);

    boolean delete(String id);

    List<T> findAll();

    T findById(String id);
}
