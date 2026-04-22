package com.modumake.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "product_templates")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductTemplate {

    @Id
    private String id;

    private String name;

    private String description;

    private String hsnCode;

    private Category category;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public enum Category {
        FASHION,
        FURNITURE,
        HOME_DECOR
    }
}
