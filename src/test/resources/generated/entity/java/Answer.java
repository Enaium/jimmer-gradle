package cn.enaium;

import java.lang.String;
import java.util.List;
import java.util.UUID;
import org.babyfish.jimmer.sql.Entity;
import org.babyfish.jimmer.sql.IdView;
import org.babyfish.jimmer.sql.ManyToOne;
import org.babyfish.jimmer.sql.OneToMany;
import org.babyfish.jimmer.sql.Table;

@Entity
@Table(
        name = "answer"
)
public interface Answer extends BaseEntity {
    String content();

    @IdView
    UUID peopleId();

    @IdView
    UUID questionId();

    @ManyToOne
    People people();

    @ManyToOne
    Question question();

    @OneToMany(
            mappedBy = "answer"
    )
    List<Comment> comments();
}
