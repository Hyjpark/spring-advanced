package org.example.expert.domain.comment.service;

import org.example.expert.domain.comment.entity.Comment;
import org.example.expert.domain.comment.repository.CommentRepository;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommentAdminServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CommentAdminService commentAdminService;

    @Test
    public void 존재하는_commentId로_삭제하면_정상적으로_삭제된다() {
        // given
        long commentId = 1L;
        User user = User.create("asd@asd.com", "pass", UserRole.USER);
        Todo todo = Todo.create("title", "contents", "Sunny", user);
        Comment comment = Comment.create("contens", user, todo);

        given(commentRepository.findById(anyLong())).willReturn(Optional.of(comment));
        doNothing().when(commentRepository).deleteById(anyLong());

        // when
        commentAdminService.deleteComment(commentId);

        // then
        verify(commentRepository, times(1)).deleteById(anyLong());
    }
}
