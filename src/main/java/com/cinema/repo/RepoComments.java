package com.cinema.repo;

import com.cinema.models.Comments;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepoComments extends JpaRepository<Comments, Long> {
}
