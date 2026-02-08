package cn.enaium

import java.util.UUID
import kotlin.String
import kotlin.collections.List
import org.babyfish.jimmer.sql.Entity
import org.babyfish.jimmer.sql.IdView
import org.babyfish.jimmer.sql.ManyToMany
import org.babyfish.jimmer.sql.ManyToOne

@Entity
public interface Question : BaseEntity {
    public val title: String

    public val content: String

    @IdView
    public val peopleId: UUID

    @ManyToOne
    public val people: People

    @ManyToMany
    public val topics: List<Topic>
}
