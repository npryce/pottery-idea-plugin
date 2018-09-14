package com.natpryce.pottery

fun teamChangeMarkdown(joiners: List<String>, leavers: List<String>): String {
    return listOfNotNull(joiners.toMarkdownListNamed("Joining"), leavers.toMarkdownListNamed("Leaving"))
        .joinToString("\n\n")
}

private fun List<String>.toMarkdownListNamed(name: String) =
    if (isNotEmpty()) "$name:\n\n${toMarkdownList()}" else null

private fun List<String>.toMarkdownList() = map { "* $it" }.joinToString("\n")
