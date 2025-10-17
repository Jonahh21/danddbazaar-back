package com.dandbazaar.back.common.reporegister;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public class MasterRepository {
    private final RepositoryRegistry registry;

    @Autowired
    public MasterRepository(RepositoryRegistry registry) {
        this.registry = registry;
    }

    @SuppressWarnings("unchecked")
    private <T, ID> JpaRepository<T, ID> getRepo(Class<T> entityClass) {
        return (JpaRepository<T, ID>) registry
            .getRepositoryFor(entityClass)
            .orElseThrow(() -> new IllegalArgumentException("No se encontró repositorio registrado para " + entityClass.getSimpleName()));
    }

    public <T, ID> Optional<T> findById(Class<T> entityClass, ID id) {
        return getRepo(entityClass).findById(id);
    }

    public <T> List<T> findAll(Class<T> entityClass) {
        return getRepo(entityClass).findAll();
    }

    public <T> List<T> findAll(Class<T> entityClass, Example<T> example) {
        return getRepo(entityClass).findAll(example);
    }

    public <T> T save(T entity) {
        @SuppressWarnings("unchecked")
        Class<T> clazz = (Class<T>) entity.getClass();
        return getRepo(clazz).save(entity);
    }

    public <T> T saveAndFlush(T entity) {
        @SuppressWarnings("unchecked")
        Class<T> clazz = (Class<T>) entity.getClass();
        return getRepo(clazz).saveAndFlush(entity);
    }

    public <T> void delete(T entity) {
        @SuppressWarnings("unchecked")
        Class<T> clazz = (Class<T>) entity.getClass();
        getRepo(clazz).delete(entity);
    }
}
