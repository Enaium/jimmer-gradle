package cn.enaium;

import java.lang.String;
import java.util.List;
import org.babyfish.jimmer.sql.Entity;
import org.babyfish.jimmer.sql.OneToMany;
import org.babyfish.jimmer.sql.OneToOne;
import org.babyfish.jimmer.sql.Table;
import org.jetbrains.annotations.Nullable;

@Entity
@Table(
        name = "people"
)
public interface People extends BaseEntity {
    String phone();

    String password();

    @OneToMany(
            mappedBy = "people"
    )
    List<Post> posts();

    @OneToOne(
            mappedBy = "people"
    )
    @Nullable
    Profile profile();

    @OneToMany(
            mappedBy = "people"
    )
    List<Question> questions();

    @OneToMany(
            mappedBy = "people"
    )
    List<Topic> topics();
}
