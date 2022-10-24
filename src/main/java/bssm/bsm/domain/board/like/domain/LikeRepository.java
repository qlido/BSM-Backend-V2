package bssm.bsm.domain.board.like.domain;

import bssm.bsm.domain.board.post.domain.PostPk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<PostLike, PostLikePk> {

    @Query(value = "SELECT COUNT(l) FROM PostLike l WHERE l.pk.post.pk.board.id = :boardId AND l.pk.post.pk.id = :postId")
    long countByPostPk(@Param("boardId") String boardId, @Param("postId") long postId);

    Optional<PostLike> findByPkPostPkAndUserCode(PostPk postPk, long userCode);
}
