package cn.enaium

import java.time.LocalDateTime
import java.util.UUID
import kotlin.Boolean
import org.babyfish.jimmer.sql.Id
import org.babyfish.jimmer.sql.MappedSuperclass

@MappedSuperclass
public interface BaseEntity {
    public val createdTime: LocalDateTime

    public val modifiedTime: LocalDateTime

    public val deleted: Boolean

    @Id
    public val id: UUID
}
