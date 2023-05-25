package com.xenotactic.ecs

actual fun FamilyConfiguration.getString(): String {
    val sb = StringBuilder()
    sb.appendLine("FamilyConfiguration")
    if (allOfComponents.isNotEmpty()) {
        sb.appendLine("\tallOfComponents:")
        allOfComponents.sortedBy { it.qualifiedName }.forEach { sb.appendLine("\t\t${it.qualifiedName}") }
    }
    if (anyOfComponents.isNotEmpty()) {
        sb.appendLine("\tanyOfComponents:")
        anyOfComponents.sortedBy { it.qualifiedName }.forEach { sb.appendLine("\t\t${it.qualifiedName}") }
    }
    if (noneOfComponents.isNotEmpty()) {
        sb.appendLine("\tnoneOfComponents:")
        noneOfComponents.sortedBy { it.qualifiedName }.forEach { sb.appendLine("\t\t${it.qualifiedName}") }
    }
    return sb.toString()
}