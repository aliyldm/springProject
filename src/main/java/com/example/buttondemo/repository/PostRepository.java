package com.example.buttondemo.repository;

import com.example.buttondemo.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByOrderByCreatedAtDesc();
    
    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.comments LEFT JOIN FETCH p.likes ORDER BY p.createdAt DESC")
    List<Post> findAllWithCommentsAndLikes();
} 