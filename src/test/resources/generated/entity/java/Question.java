package cn.enaium;

import java.lang.String;
import java.util.List;
import java.util.UUID;
import org.babyfish.jimmer.sql.Entity;
import org.babyfish.jimmer.sql.IdView;
import org.babyfish.jimmer.sql.ManyToMany;
import org.babyfish.jimmer.sql.ManyToOne;

@Entity
public interface Question extends BaseEntity {
    String title();

    String content();

    @IdView
    UUID peopleId();

    @ManyToOne
    People people();

    @ManyToMany
    List<Topic> topics();
}
