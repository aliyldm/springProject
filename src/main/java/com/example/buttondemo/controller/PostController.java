package com.example.buttondemo.controller;

import com.example.buttondemo.model.Post;
import com.example.buttondemo.model.User;
import com.example.buttondemo.repository.PostRepository;
import com.example.buttondemo.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/posts")
@CrossOrigin(origins = "http://localhost:4200")
public class PostController {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public PostController(PostRepository postRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<Post> getAllPosts() {
        return postRepository.findAllWithCommentsAndLikes();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Post> getPostById(@PathVariable Long id) {
        return postRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Post> createPost(@RequestBody Post post, Authentication authentication) {
        String username = authentication.getName();
        Optional<User> user = userRepository.findByUsername(username);
        
        if (user.isPresent()) {
            post.setUser(user.get());
            Post savedPost = postRepository.save(post);
            return ResponseEntity.ok(savedPost);
        }
        
        return ResponseEntity.badRequest().build();
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<Post> likePost(@PathVariable Long id, Authentication authentication) {
        String username = authentication.getName();
        Optional<User> user = userRepository.findByUsername(username);
        Optional<Post> post = postRepository.findById(id);
        
        if (user.isPresent() && post.isPresent()) {
            Post existingPost = post.get();
            if (existingPost.getLikes().contains(user.get())) {
                existingPost.getLikes().remove(user.get());
            } else {
                existingPost.getLikes().add(user.get());
            }
            return ResponseEntity.ok(postRepository.save(existingPost));
        }
        
        return ResponseEntity.badRequest().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id, Authentication authentication) {
        Optional<Post> post = postRepository.findById(id);
        if (post.isPresent() && post.get().getUser().getUsername().equals(authentication.getName())) {
            postRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }
} 