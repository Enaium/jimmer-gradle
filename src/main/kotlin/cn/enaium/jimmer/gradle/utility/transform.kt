/*
 * Copyright 2024 Enaium
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.enaium.jimmer.gradle.utility

import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.tree.ClassNode
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.jar.JarEntry
import java.util.jar.JarInputStream
import java.util.jar.JarOutputStream

/**
 * @author Enaium
 */
fun jarClass(input: File, output: File, transform: (name: String, bytes: ByteArray) -> ByteArray) {
    val jis = JarInputStream(FileInputStream(input))
    val jos = JarOutputStream(FileOutputStream(output))
    var entry: JarEntry? = null
    while (jis.nextJarEntry?.also { entry = it } != null) {
        entry?.also {
            jos.putNextEntry(JarEntry(it.name))
            if (it.name.endsWith(".class")) {
                jos.write(transform(it.name, jis.readBytes()))
            } else {
                jos.write(jis.readBytes())
            }
            jos.closeEntry()
        }
    }
    jis.close()
    jos.close()
}

fun ByteArray.cv(cv: (cw: ClassWriter) -> ClassVisitor): ByteArray {
    val cr = ClassReader(this)
    val cw = ClassWriter(cr, ClassWriter.COMPUTE_FRAMES)
    cr.accept(cv(cw), 0)
    return cw.toByteArray()
}

fun ByteArray.cn(cn: (cn: ClassNode) -> ClassNode): ByteArray {
    val cr = ClassReader(this)
    val cn = ClassNode()
    cr.accept(cn, 0)
    val cw = ClassWriter(ClassWriter.COMPUTE_FRAMES)
    cn(cn).accept(cw)
    return cw.toByteArray()
}