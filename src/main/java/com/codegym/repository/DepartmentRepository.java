package com.codegym.repository;

import com.codegym.model.Department;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;


public interface DepartmentRepository extends PagingAndSortingRepository<Department,Long> {
    Page<Department> findAllByNameContaining(String name, Pageable pageable);
}
