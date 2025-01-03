package com.example.fromis_7_be.piece.entity;

import com.example.fromis_7_be.category.entity.Category;
import com.example.fromis_7_be.share.entity.Share;
import com.example.fromis_7_be.userpiece.entity.UserPiece;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Piece {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    //JPA에서 array를 지원하지 않기 때문에 이를 위해 ElementCollection을 사용하여 List<String> member
    @ElementCollection
    @CollectionTable(name = "piece_member_names", joinColumns = @JoinColumn(name = "piece_id"))
    @Column(name = "member_name")
    private List<String> memberNames;
    private String color;
    private Integer startYear;
    private Integer startMonth;
    private Integer startDay;
    private Integer endYear;
    private Integer endMonth;
    private Integer endDay;
    private Integer highlightCount = 0;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "modified_at")
    private LocalDateTime modifiedAt;

    @Getter
    @OneToMany(mappedBy = "piece", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserPiece> userPieces = new ArrayList<>();


    @OneToMany(mappedBy = "piece", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Category> categories = new ArrayList<>();

    @OneToOne
    @JoinColumn(name = "share_id") // Piece가 외래 키를 소유
    private Share share; // 관계의 소유 쪽

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.modifiedAt = LocalDateTime.now();
    }


    public void updateHighlightCount(Integer highlightCount) {
        this.highlightCount = highlightCount;
    }

    public static Piece from(String title, List<String> memberNames, String color,
                             Integer startYear, Integer startMonth, Integer startDay,
                             Integer endYear, Integer endMonth, Integer endDay, Integer highlightCount) {
        return Piece.builder()
                .title(title)
                .memberNames(memberNames)
                .color(color)
                .startYear(startYear)
                .startMonth(startMonth)
                .startDay(startDay)
                .endYear(endYear)
                .endMonth(endMonth)
                .endDay(endDay)
                .highlightCount(highlightCount)
                .build();
    }
    // 업데이트 메서드 구현
    public void update(String title, List<String> memberNames, String color,
                       Integer startYear, Integer startMonth, Integer startDay,
                       Integer endYear, Integer endMonth, Integer endDay, Integer highlightCount) {
        this.title = title;
        this.memberNames = memberNames;
        this.color = color;
        this.startYear = startYear;
        this.startMonth = startMonth;
        this.startDay = startDay;
        this.endYear = endYear;
        this.endMonth = endMonth;
        this.endDay = endDay;
        this.highlightCount = highlightCount;
        this.modifiedAt = LocalDateTime.now(); // 수정 시간 갱신
    }

    public void updateShare(Share share) {
        this.share = share;
    }

}
