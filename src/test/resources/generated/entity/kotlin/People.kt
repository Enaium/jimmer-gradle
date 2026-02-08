package cn.enaium

import kotlin.String
import kotlin.collections.List
import org.babyfish.jimmer.sql.Entity
import org.babyfish.jimmer.sql.OneToMany
import org.babyfish.jimmer.sql.OneToOne

@Entity
public interface People : BaseEntity {
    public val phone: String

    public val password: String

    @OneToMany(mappedBy = "people")
    public val posts: List<Post>

    @OneToOne(mappedBy = "people")
    public val profile: Profile?

    @OneToMany(mappedBy = "people")
    public val questions: List<Question>

    @OneToMany(mappedBy = "people")
    public val topics: List<Topic>
}
