package com.example.fromis_7_be.listup.entity;

import com.example.fromis_7_be.category.entity.Category;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Listup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long listId;
    private String name; //meta data로 가져온 컨텐츠 이름
    private String url;
    private String image; //meta 데이터 이미지

    @Lob private String description; //여기 주택 너무 예쁨

    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    //likecount
    //commentcount;

    @ManyToOne
    @JoinColumn(name = "cate_id")
    private Category category;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.modifiedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.modifiedAt = LocalDateTime.now();
    }

    public static Listup from(String name, String url, String image, String description, Category category) {
        return new Listup(null, name, url, image, description, LocalDateTime.now(), LocalDateTime.now(), category);
    }
    public void update(String name, String url, String image, String description) {
        this.name = name;
        this.url = url;
        this.image = image;
        this.description = description;
    }
}