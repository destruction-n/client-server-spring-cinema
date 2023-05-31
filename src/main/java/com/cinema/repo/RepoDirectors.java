package com.cinema.repo;

import com.cinema.models.Directors;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepoDirectors extends JpaRepository<Directors, Long> {
}
