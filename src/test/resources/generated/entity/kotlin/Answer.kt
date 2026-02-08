package cn.enaium

import java.util.UUID
import kotlin.String
import kotlin.collections.List
import org.babyfish.jimmer.sql.Entity
import org.babyfish.jimmer.sql.IdView
import org.babyfish.jimmer.sql.ManyToOne
import org.babyfish.jimmer.sql.OneToMany

@Entity
public interface Answer : BaseEntity {
    public val content: String

    @IdView
    public val peopleId: UUID

    @IdView
    public val questionId: UUID

    @ManyToOne
    public val people: People

    @ManyToOne
    public val question: Question

    @OneToMany(mappedBy = "answer")
    public val comments: List<Comment>
}
