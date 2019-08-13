package com.codegym.service;

import com.codegym.model.Department;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DepartmentService {
    Page<Department> findAll(Pageable pageable);

    Department findById(Long id);

    void save(Department department);

    void remove(Long id);

    Page <Department> findAllByNameContaining(String name, Pageable pageable);
}
