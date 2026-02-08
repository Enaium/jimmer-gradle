package cn.enaium

import java.util.UUID
import kotlin.String
import kotlin.collections.List
import org.babyfish.jimmer.sql.Entity
import org.babyfish.jimmer.sql.IdView
import org.babyfish.jimmer.sql.ManyToMany
import org.babyfish.jimmer.sql.ManyToOne

@Entity
public interface Topic : BaseEntity {
    public val title: String

    @IdView
    public val peopleId: UUID

    @ManyToOne
    public val people: People

    @ManyToMany(mappedBy = "topics")
    public val posts: List<Post>

    @ManyToMany(mappedBy = "topics")
    public val questions: List<Question>
}
