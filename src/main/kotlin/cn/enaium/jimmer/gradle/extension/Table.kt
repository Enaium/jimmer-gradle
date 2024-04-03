package cn.enaium.jimmer.gradle.extension

import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import javax.inject.Inject

/**
 * @author Enaium
 */
open class Table @Inject constructor(objects: ObjectFactory) {
    val primaryKey: Property<String> = objects.property(String::class.java).convention("id")
    val association: Property<Association> = objects.property(Association::class.java).convention(Association.REAL)
    val typeMappings: MapProperty<String, String> = objects.mapProperty(String::class.java, String::class.java)
    val comment: Property<Boolean> = objects.property(Boolean::class.java).convention(false)
    val idView: Property<Boolean> = objects.property(Boolean::class.java).convention(false)
}