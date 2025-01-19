package cn.enaium

import java.util.UUID
import kotlin.String
import org.babyfish.jimmer.sql.Entity
import org.babyfish.jimmer.sql.IdView
import org.babyfish.jimmer.sql.ManyToOne
import org.babyfish.jimmer.sql.Table

@Entity
@Table(name = "comment")
public interface Comment : BaseEntity {
    public val content: String

    @IdView
    public val peopleId: UUID

    @IdView
    public val commentId: UUID?

    @IdView
    public val answerId: UUID?

    @IdView
    public val postId: UUID?

    @ManyToOne
    public val answer: Answer?

    @ManyToOne
    public val comment: Comment?

    @ManyToOne
    public val people: People

    @ManyToOne
    public val post: Post?
}
