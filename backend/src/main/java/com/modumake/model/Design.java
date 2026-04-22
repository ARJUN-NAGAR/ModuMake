package com.modumake.model;

import com.modumake.dto.TechPackDto;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "designs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Design {

    @Id
    private String id;

    @DBRef
    private User user;

    private String title;

    private String originalPrompt;

    private String imageUrl;

    private TechPackDto techPack;

    private Status status;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public enum Status {
        DRAFT,
        PENDING_MANUFACTURER_MATCH,
        ACCEPTED,
        PRODUCING,
        SHIPPED
    }
}
