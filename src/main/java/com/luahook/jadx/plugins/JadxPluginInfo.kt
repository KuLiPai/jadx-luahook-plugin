package com.luahook.jadx.plugins

import jadx.api.metadata.ICodeAnnotation
import jadx.api.metadata.ICodeNodeRef
import jadx.api.plugins.JadxPlugin
import jadx.api.plugins.JadxPluginContext
import jadx.api.plugins.JadxPluginInfo
import jadx.api.plugins.JadxPluginInfoBuilder

class JadxPluginInfo : JadxPlugin {

	private val options = JadxPluginOptions()

	companion object {
		const val PLUGIN_ID: String = "jadx-luahook-plugin"
	}

	override fun getPluginInfo(): JadxPluginInfo {
		return JadxPluginInfoBuilder.pluginId(PLUGIN_ID)
			.name("Jadx LuaHook Plugin")
			.description("为 Jadx 添加 LuaHook 支持 | Add LuaHook support for Jadx")
//			.homepage("https://github.com/")
			.build()
	}

	private fun isEnable(nodeRef: ICodeNodeRef?): Boolean {
		return when (nodeRef?.annType) {
			ICodeAnnotation.AnnType.CLASS,
			ICodeAnnotation.AnnType.METHOD,
			ICodeAnnotation.AnnType.FIELD -> true

			else -> false
		}
	}

	override fun init(context: JadxPluginContext) {
		context.registerOptions(options)

		val guiContext = context.guiContext ?: return
		val decompiler = context.decompiler ?: return

		if (options.isEnable) {
			val yukiCodeAction = LuaHookCodeAction(guiContext, decompiler)
			guiContext.addPopupMenuAction(
				"复制为 LuaHook 片段",
				{ nodeRef: ICodeNodeRef? -> this.isEnable(nodeRef) },
				null, yukiCodeAction
			)
		}
	}
}
