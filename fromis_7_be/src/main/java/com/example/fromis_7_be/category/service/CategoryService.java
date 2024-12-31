package com.example.fromis_7_be.category.service;

import com.example.fromis_7_be.alarm.service.AlarmService;
import com.example.fromis_7_be.category.dto.CategoryRequest;
import com.example.fromis_7_be.category.dto.CategoryResponse;
import com.example.fromis_7_be.category.entity.Category;
import com.example.fromis_7_be.category.repository.CategoryRepository;
import com.example.fromis_7_be.listup.dto.ListupResponse;
import com.example.fromis_7_be.listup.entity.Listup;
import com.example.fromis_7_be.listup.repository.ListupRepository;
import com.example.fromis_7_be.listup.service.ListupService;
import com.example.fromis_7_be.piece.entity.Piece;
import com.example.fromis_7_be.piece.repository.PieceRepository;
import com.example.fromis_7_be.user.entity.User;
import com.example.fromis_7_be.userpiece.entity.UserPiece;
import com.example.fromis_7_be.userpiece.repository.UserPieceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final PieceRepository pieceRepository;
    private final ListupService listupService;
    private final AlarmService alarmService;
    private final UserPieceRepository userPieceRepository;
    public CategoryResponse.CategoryReadResponse createCategoryByPieceId(Long pieceId, CategoryRequest.CategoryCreateRequest req){

        Piece piece = pieceRepository.findById(pieceId)
                .orElseThrow(() -> new NoSuchElementException("찾으시는 piece 정보: " + pieceId + "가 존재하지 않습니다."));
        Category category = Category.from(req.getName(), req.getColor(), false, piece);
        categoryRepository.save(category);
        listupService.deleteListupByCateId(category.getId());
        listupService.createListupByCateId(category.getId(), req.getListups());

        List<UserPiece> userPieces = userPieceRepository.findByPieceId(pieceId);
        if (userPieces.isEmpty()) {
            throw new IllegalArgumentException("해당 piece에 연결된 사용자가 없습니다.");
        }

        // 모든 사용자에게 알림 전송
        for (UserPiece userPiece : userPieces) {
            User user = userPiece.getUser();
            alarmService.notifyCategoryCreated(user, category);
        }
        return CategoryResponse.CategoryReadResponse.builder()
                .cateId(category.getId())
                .name(category.getName())
                .color(category.getColor())
                .isHighlighted(category.getIsHighlighted())
                .lists(category.getLists().stream()
                        .map(ListupResponse.ListupReadResponse::from)
                        .collect(Collectors.toList()))
                .build();
    }

    public List<CategoryResponse.CategoryReadResponse> readCategoryByPiece(Long pieceId) {
        Piece piece = pieceRepository.findById(pieceId)
                .orElseThrow(() -> new NoSuchElementException("찾으시는 piece 정보: " + pieceId + "가 존재하지 않습니다."));

        return categoryRepository.findAllByPiece(piece).stream()
                .map(category -> CategoryResponse.CategoryReadResponse.builder()
                        .cateId(category.getId())
                        .name(category.getName())
                        .color(category.getColor())
                        .isHighlighted(category.getIsHighlighted())
                        .lists(category.getLists().stream()
                                .map(ListupResponse.ListupReadResponse::from) // 변환 로직
                                .collect(Collectors.toList()))
                        .build()
                ).collect(Collectors.toList());
    }
    public void delete(Long categoryId){
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NoSuchElementException("찾으시는 category 정보: " + categoryId + "가 존재하지 않습니다."));

        categoryRepository.delete(category);
    }
    public CategoryResponse.CategoryReadResponse updateByCategoryId(Long categoryId, boolean isHighlighted){
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NoSuchElementException("찾으시는 category 정보: " + categoryId + "가 존재하지 않습니다."));

        category.setIsHighlighted(isHighlighted);

        categoryRepository.save(category);

        return CategoryResponse.CategoryReadResponse.builder()
                .cateId(category.getId())
                .name(category.getName())
                .color(category.getColor())
                .isHighlighted(category.getIsHighlighted())
                .lists(category.getLists().stream()
                        .map(ListupResponse.ListupReadResponse::from)
                        .collect(Collectors.toList()))
                .build();
    }

}