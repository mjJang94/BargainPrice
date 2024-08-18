package com.mj.core.common

fun appendCategoryData(vararg data: String): String {
    val sb = StringBuilder()
    data.forEachIndexed { index, category ->
        when (category.isNotBlank()) {
            true -> when (data.size - 1 == index) {
                true -> sb.append(category)
                else -> sb.append(category).append(" > ")
            }
            else -> sb.delete(sb.length - 3, sb.length)
        }
    }
    return sb.toString()
}