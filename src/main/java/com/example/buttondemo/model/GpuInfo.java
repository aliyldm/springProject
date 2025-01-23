package com.example.buttondemo.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "gpu_info")
public class GpuInfo {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private String architecture;
    private String memorySize;
    private String memoryType;
    private String boostClock;
    private String baseClock;
    private String tdp;
    private String launchDate;
    private String price;

    @OneToMany(mappedBy = "gpu", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GpuComment> comments = new ArrayList<>();

    @ManyToMany
    @JoinTable(
        name = "gpu_likes",
        joinColumns = @JoinColumn(name = "gpu_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> likes = new HashSet<>();

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArchitecture() {
        return architecture;
    }

    public void setArchitecture(String architecture) {
        this.architecture = architecture;
    }

    public String getMemorySize() {
        return memorySize;
    }

    public void setMemorySize(String memorySize) {
        this.memorySize = memorySize;
    }

    public String getMemoryType() {
        return memoryType;
    }

    public void setMemoryType(String memoryType) {
        this.memoryType = memoryType;
    }

    public String getBoostClock() {
        return boostClock;
    }

    public void setBoostClock(String boostClock) {
        this.boostClock = boostClock;
    }

    public String getBaseClock() {
        return baseClock;
    }

    public void setBaseClock(String baseClock) {
        this.baseClock = baseClock;
    }

    public String getTdp() {
        return tdp;
    }

    public void setTdp(String tdp) {
        this.tdp = tdp;
    }

    public String getLaunchDate() {
        return launchDate;
    }

    public void setLaunchDate(String launchDate) {
        this.launchDate = launchDate;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public List<GpuComment> getComments() {
        return comments;
    }

    public void setComments(List<GpuComment> comments) {
        this.comments = comments;
    }

    public Set<User> getLikes() {
        return likes;
    }

    public void setLikes(Set<User> likes) {
        this.likes = likes;
    }

    // Progress bar yüzdelik hesaplamaları
    public int getBoostClockPercentage() {
        try {
            String numericValue = boostClock.replaceAll("[^0-9]", "");
            int mhz = Integer.parseInt(numericValue);
            return (mhz * 100) / 3000; // 3000 MHz maksimum değer olarak kabul edildi
        } catch (Exception e) {
            return 0;
        }
    }

    public int getBaseClockPercentage() {
        try {
            String numericValue = baseClock.replaceAll("[^0-9]", "");
            int mhz = Integer.parseInt(numericValue);
            return (mhz * 100) / 2500; // 2500 MHz maksimum değer olarak kabul edildi
        } catch (Exception e) {
            return 0;
        }
    }

    public int getTdpPercentage() {
        try {
            String numericValue = tdp.replaceAll("[^0-9]", "");
            int watts = Integer.parseInt(numericValue);
            return (watts * 100) / 450; // 450W maksimum değer olarak kabul edildi
        } catch (Exception e) {
            return 0;
        }
    }
} 