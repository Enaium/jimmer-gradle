package cn.enaium

import java.util.UUID
import kotlin.String
import org.babyfish.jimmer.sql.Entity
import org.babyfish.jimmer.sql.IdView
import org.babyfish.jimmer.sql.OneToOne
import org.babyfish.jimmer.sql.Table

@Entity
@Table(name = "profile")
public interface Profile : BaseEntity {
    @IdView
    public val peopleId: UUID

    public val nickname: String

    public val email: String

    @OneToOne
    public val people: People
}
