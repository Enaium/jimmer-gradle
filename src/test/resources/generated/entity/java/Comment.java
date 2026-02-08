package cn.enaium;

import java.lang.String;
import java.util.UUID;
import org.babyfish.jimmer.sql.Entity;
import org.babyfish.jimmer.sql.IdView;
import org.babyfish.jimmer.sql.ManyToOne;
import org.jspecify.annotations.Nullable;

@Entity
public interface Comment extends BaseEntity {
    String content();

    @IdView
    UUID peopleId();

    @IdView
    @Nullable
    UUID commentId();

    @IdView
    @Nullable
    UUID answerId();

    @IdView
    @Nullable
    UUID postId();

    @ManyToOne
    @Nullable
    Answer answer();

    @ManyToOne
    @Nullable
    Comment comment();

    @ManyToOne
    People people();

    @ManyToOne
    @Nullable
    Post post();
}
