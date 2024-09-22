package com.memil.yogimukja.review.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "reviews")
@Getter
@NoArgsConstructor
public class ReviewDocument {
    @Id
    private String id;
    private String restaurantId; // 레스토랑 ID
    private String userId; // 사용자 ID
    private Integer rate;
    private String content;
}