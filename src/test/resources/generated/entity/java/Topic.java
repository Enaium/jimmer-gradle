package cn.enaium;

import java.lang.String;
import java.util.List;
import java.util.UUID;
import org.babyfish.jimmer.sql.Entity;
import org.babyfish.jimmer.sql.IdView;
import org.babyfish.jimmer.sql.ManyToMany;
import org.babyfish.jimmer.sql.ManyToOne;
import org.babyfish.jimmer.sql.Table;

@Entity
@Table(
        name = "topic"
)
public interface Topic extends BaseEntity {
    String title();

    @IdView
    UUID peopleId();

    @ManyToOne
    People people();

    @ManyToMany(
            mappedBy = "topics"
    )
    List<Post> posts();

    @ManyToMany(
            mappedBy = "topics"
    )
    List<Question> questions();
}
