package com.example.buttondemo.controller;

import com.example.buttondemo.model.Comment;
import com.example.buttondemo.model.Post;
import com.example.buttondemo.model.User;
import com.example.buttondemo.repository.CommentRepository;
import com.example.buttondemo.repository.PostRepository;
import com.example.buttondemo.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/comments")
@CrossOrigin(origins = "http://localhost:4200")
public class CommentController {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public CommentController(CommentRepository commentRepository, 
                           PostRepository postRepository,
                           UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/post/{postId}")
    public List<Comment> getCommentsByPostId(@PathVariable Long postId) {
        return commentRepository.findByPostIdOrderByCreatedAtDesc(postId);
    }

    @PostMapping("/post/{postId}")
    public ResponseEntity<Comment> createComment(@PathVariable Long postId,
                                               @RequestBody Comment comment,
                                               Authentication authentication) {
        Optional<Post> post = postRepository.findById(postId);
        Optional<User> user = userRepository.findByUsername(authentication.getName());
        
        if (post.isPresent() && user.isPresent()) {
            comment.setPost(post.get());
            comment.setUser(user.get());
            Comment savedComment = commentRepository.save(comment);
            return ResponseEntity.ok(savedComment);
        }
        
        return ResponseEntity.badRequest().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id, Authentication authentication) {
        Optional<Comment> comment = commentRepository.findById(id);
        if (comment.isPresent() && comment.get().getUser().getUsername().equals(authentication.getName())) {
            commentRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }
} 