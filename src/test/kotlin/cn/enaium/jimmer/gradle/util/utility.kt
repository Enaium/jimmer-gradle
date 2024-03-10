package cn.enaium.jimmer.gradle.util

/**
 * @author Enaium
 */
fun dbMapBuilder(
    url: String,
    username: String,
    password: String,
    driver: String,
    language: String,
    driverDependency: String
): Map<String, String> {
    return mapOf(
        "url" to url,
        "username" to username,
        "password" to password,
        "driver" to driver,
        "language" to language,
        "driverDependency" to driverDependency
    )
}