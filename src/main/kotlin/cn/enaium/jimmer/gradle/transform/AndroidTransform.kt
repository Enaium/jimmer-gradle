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

package cn.enaium.jimmer.gradle.transform

import cn.enaium.jimmer.gradle.JimmerPlugin
import cn.enaium.jimmer.gradle.Utility
import cn.enaium.jimmer.gradle.utility.cn
import cn.enaium.jimmer.gradle.utility.cv
import cn.enaium.jimmer.gradle.utility.jarClass
import org.gradle.api.artifacts.transform.InputArtifact
import org.gradle.api.artifacts.transform.TransformAction
import org.gradle.api.artifacts.transform.TransformOutputs
import org.gradle.api.artifacts.transform.TransformParameters
import org.gradle.api.file.FileSystemLocation
import org.gradle.api.provider.Provider
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.*

/**
 * @author Enaium
 */
abstract class AndroidTransform : TransformAction<TransformParameters.None> {
    @get:InputArtifact
    abstract val inputArtifact: Provider<FileSystemLocation>

    override fun transform(outputs: TransformOutputs) {
        val inputFile = inputArtifact.get().asFile
        if (!listOf("jimmer-sql", "jimmer-sql-kotlin", "jimmer-ksp").any { inputFile.name.contains(it) }) {
            outputs.file(inputFile)
            return
        }
        val outputFile = outputs.file(inputFile.name.replace(".jar", "-transformed.jar"))
        jarClass(inputFile, outputFile) { name, bytes ->
            bytes.cv { cw ->
                object : ClassVisitor(Opcodes.ASM9, cw) {

                    lateinit var className: String

                    override fun visit(
                        version: Int,
                        access: Int,
                        name: String,
                        signature: String?,
                        superName: String?,
                        interfaces: Array<out String>?
                    ) {
                        className = name
                        super.visit(version, access, name, signature, superName, interfaces)
                    }

                    override fun visitMethod(
                        access: Int,
                        name: String,
                        descriptor: String,
                        signature: String?,
                        exceptions: Array<String>?
                    ): MethodVisitor? {
                        val name = if (className == "org/babyfish/jimmer/sql/kt/ast/expression/KPredicatesKt") {
                            name.replace("?", "If")
                        } else {
                            name
                        }
                        return super.visitMethod(access, name, descriptor, signature, exceptions)
                    }
                }
            }.cn { cn ->
                if (cn.name == "org/babyfish/jimmer/sql/ast/impl/Variables") {
                    val cr = ClassReader(
                        JimmerPlugin::class.java.classLoader.getResourceAsStream(
                            "${Utility::class.java.name.replace(".", "/")}.class"
                        )
                    )
                    val classNode = ClassNode()
                    cr.accept(classNode, 0)
                    classNode.methods.find { it.name == "handleDateTime" }?.also {
                        cn.methods.removeIf { mn -> mn.name == "handleDateTime" }
                        cn.methods.add(it)
                    }
                } else if (cn.name == "org/babyfish/jimmer/ksp/immutable/generator/PropsGenerator") {
                    cn.methods.find { it.name == "addProp" }?.also { mn ->
                        val plusChar = mn.instructions.filterIsInstance<IntInsnNode>()
                            .find { it.opcode == Opcodes.BIPUSH && it.operand == '?'.code }
                            ?: return@also
                        val plusStr = InsnList()
                        plusStr.add(LdcInsnNode("If"))
                        plusStr.add(
                            MethodInsnNode(
                                Opcodes.INVOKEVIRTUAL,
                                "java/lang/StringBuilder",
                                "append",
                                "(Ljava/lang/String;)Ljava/lang/StringBuilder;",
                                false
                            )
                        )
                        mn.instructions.insert(plusChar.next, plusStr)
                        mn.instructions.remove(plusChar.next)
                        mn.instructions.remove(plusChar)
                    }
                }
                cn
            }
        }
    }
}