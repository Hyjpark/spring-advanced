package org.example.expert.domain.comment.dto.response;

import lombok.Getter;

@Getter
public class CommentQueryDto {
    private Long id;
    private String contents;
    private Long userId;
    private String userEmail;

    public CommentQueryDto(Long id, String contents, Long userId, String userEmail) {
        this.id = id;
        this.contents = contents;
        this.userId = userId;
        this.userEmail = userEmail;
    }
}
