package cn.enaium;

import java.time.LocalDateTime;
import java.util.UUID;
import org.babyfish.jimmer.sql.Id;
import org.babyfish.jimmer.sql.MappedSuperclass;

@MappedSuperclass
public interface BaseEntity {
    LocalDateTime createdTime();

    LocalDateTime modifiedTime();

    boolean deleted();

    @Id
    UUID id();
}
