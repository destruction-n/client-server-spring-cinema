package com.cinema.repo;

import com.cinema.models.Income;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RepoIncomes extends JpaRepository<Income, Long> {
}
