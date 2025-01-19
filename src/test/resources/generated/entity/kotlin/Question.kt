package cn.enaium

import java.util.UUID
import kotlin.String
import kotlin.collections.List
import org.babyfish.jimmer.sql.Entity
import org.babyfish.jimmer.sql.IdView
import org.babyfish.jimmer.sql.JoinColumn
import org.babyfish.jimmer.sql.JoinTable
import org.babyfish.jimmer.sql.ManyToMany
import org.babyfish.jimmer.sql.ManyToOne
import org.babyfish.jimmer.sql.Table

@Entity
@Table(name = "question")
public interface Question : BaseEntity {
    public val title: String

    public val content: String

    @IdView
    public val peopleId: UUID

    @ManyToOne
    public val people: People

    @ManyToMany
    @JoinTable(
        name = "question_topic",
        joinColumns = [JoinColumn(name = "question_id")],
        inverseJoinColumns = [JoinColumn(name = "topic_id")],
    )
    public val topics: List<Topic>
}
