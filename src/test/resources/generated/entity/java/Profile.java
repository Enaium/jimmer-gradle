package cn.enaium;

import java.lang.String;
import java.util.UUID;
import org.babyfish.jimmer.sql.Entity;
import org.babyfish.jimmer.sql.IdView;
import org.babyfish.jimmer.sql.OneToOne;
import org.babyfish.jimmer.sql.Table;

@Entity
@Table(
        name = "profile"
)
public interface Profile extends BaseEntity {
    @IdView
    UUID peopleId();

    String nickname();

    String email();

    @OneToOne
    People people();
}
