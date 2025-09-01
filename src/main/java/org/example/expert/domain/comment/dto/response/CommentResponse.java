package org.example.expert.domain.comment.dto.response;

import lombok.Getter;
import org.example.expert.domain.user.dto.response.UserResponse;

@Getter
public class CommentResponse {

    private final Long id;
    private final String contents;
    private final UserResponse user;

    private CommentResponse(Long id, String contents, UserResponse user) {
        this.id = id;
        this.contents = contents;
        this.user = user;
    }

    public static CommentResponse of(CommentQueryDto dto) {
        return new CommentResponse(
                dto.getId(),
                dto.getContents(),
                UserResponse.of(dto.getUserId(), dto.getUserEmail())
        );
    }
}
