package cn.enaium.jimmer.gradle.util

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.gradle.util.GradleVersion
import java.io.File
import java.io.FileNotFoundException
import java.nio.file.Files

/**
 * @author Enaium
 */
class ProjectTest(private val name: String) {

    val testProjectDir: File =
        Files.createTempDirectory("jimmer-gradle-${name}-${System.currentTimeMillis()}").toFile()

    private fun copyInputFiles(stringMap: Map<String, String>) {
        val baseProjectDir = File("src/test/resources/projects/${name}")
        if (!baseProjectDir.exists()) {
            throw FileNotFoundException("Failed to find project directory at:" + baseProjectDir.absolutePath)
        }
        baseProjectDir.walk().forEach {
            if (it.isDirectory) {
                return@forEach
            }
            val path = it.path.replace(baseProjectDir.path, "")
            val tempFile = File(testProjectDir, path)
            if (tempFile.exists()) {
                tempFile.delete()
            }
            tempFile.parentFile.mkdirs()
            val text = it.readText(Charsets.UTF_8)

            tempFile.writeText(stringMap.entries.fold(text) { acc, entry ->
                acc.replace("%{${entry.key}}", entry.value)
            })
        }
    }

    fun clear() {
        testProjectDir.deleteRecursively()
    }

    fun create(
        task: String,
        stringMap: Map<String, String> = mapOf(),
    ): BuildResult {
        copyInputFiles(stringMap)

        return GradleRunner.create().withProjectDir(testProjectDir).withArguments(
            task,
            "--stacktrace",
            "--warning-mode",
            "fail",
            "--gradle-user-home",
            File(System.getProperty("user.home"), ".gradle").absolutePath,
        ).withGradleVersion(GradleVersion.current().version).forwardOutput().withDebug(true)
            .build()
    }
}