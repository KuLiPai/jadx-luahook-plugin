package com.luahook.jadx.plugins

import jadx.api.*
import jadx.api.metadata.ICodeNodeRef
import jadx.api.plugins.gui.JadxGuiContext
import jadx.core.dex.instructions.args.ArgType
import jadx.core.dex.instructions.args.PrimitiveType
import jadx.core.utils.exceptions.JadxRuntimeException
import java.util.function.Consumer

class LuaHookCodeAction(
	private val guiContext: JadxGuiContext,
	private val decompiler: JadxDecompiler
) : Consumer<ICodeNodeRef?> {

	override fun accept(iCodeNodeRef: ICodeNodeRef?) {
		val node = decompiler.getJavaNodeByRef(iCodeNodeRef)
		val code = generateLuaHookSnippet(node)
		guiContext.copyToClipboard(code)
	}

	private fun generateLuaHookSnippet(node: JavaNode?): String {
		return when (node) {
			is JavaMethod -> generateMethodSnippet(node)
			is JavaField -> generateFieldSnippet(node)
			is JavaClass -> generateClassSnippet(node)
			else -> throw JadxRuntimeException("Unsupported node: ${node?.javaClass}")
		}
	}

	// ------------------------------------------------------------
	// Method / Constructor Hook
	// ------------------------------------------------------------
	private fun generateMethodSnippet(node: JavaMethod): String {
		val rawClassName = node.declaringClass.rawName
		val methodNode = node.methodNode
		val methodName = methodNode.name
		val args = methodNode.argTypes.map { fixTypeContent(it) }

		val sb = StringBuilder()
		if (methodNode.isConstructor) {
			sb.append("hookctor {\n")
			sb.append("  class = \"$rawClassName\",\n")
			if (args.isNotEmpty()) {
				sb.append("  params = {${args.joinToString(", ") { "\"$it\"" }}},\n")
			}
		} else {
			sb.append("hook {\n")
			sb.append("  class = \"$rawClassName\",\n")
			sb.append("  method = \"$methodName\",\n")
			if (args.isNotEmpty()) {
				sb.append("  params = {${args.joinToString(", ") { "\"$it\"" }}},\n")
			}
		}

		sb.append("  before = function(it)\n")
		sb.append("    -- before call\n")
		sb.append("  end,\n")
		sb.append("  after = function(it)\n")
		sb.append("    -- after call\n")
		sb.append("  end,\n")
		sb.append("}\n")

		return sb.toString()
	}

	// ------------------------------------------------------------
	// Field Access / Modification Templates
	// ------------------------------------------------------------
	private fun generateFieldSnippet(node: JavaField): String {
		val className = node.declaringClass.rawName
		val fieldName = node.name
		val isStatic = node.fieldNode.isStatic

		return if (isStatic) {
			// ---- 静态字段模板 ----
			"""
local clazz = findClass("$className")
local value = getStaticField(clazz, "$fieldName")
-- setStaticField(clazz, "$fieldName", ...)
""".trimIndent()

		} else {
			// ---- 实例字段模板 ----
			"""
local clazz = findClass("$className")
local instance = class()
local value = getField(instance, "$fieldName")
-- setField(instance, "$fieldName", ...)
""".trimIndent()
		}
	}


	// ------------------------------------------------------------
	// Class Lookup / Template
	// ------------------------------------------------------------
	private fun generateClassSnippet(node: JavaClass): String {
		val className = node.rawName

		return """imports "$className""""
	}

	// ------------------------------------------------------------
	// Type Conversion for Parameter Lists
	// ------------------------------------------------------------
	private fun fixTypeContent(type: ArgType): String {
		return when {
			type.isPrimitive -> when (type.primitiveType) {
				PrimitiveType.INT -> "int"
				PrimitiveType.LONG -> "long"
				PrimitiveType.BOOLEAN -> "boolean"
				PrimitiveType.DOUBLE -> "double"
				PrimitiveType.FLOAT -> "float"
				PrimitiveType.CHAR -> "char"
				PrimitiveType.BYTE -> "byte"
				PrimitiveType.SHORT -> "short"
				else -> "unknown"
			}

			type.isObject -> when (type.`object`) {
				"java.lang.String" -> "String"
				else -> type.`object`
			}

			else -> type.toString()
		}
	}
}
