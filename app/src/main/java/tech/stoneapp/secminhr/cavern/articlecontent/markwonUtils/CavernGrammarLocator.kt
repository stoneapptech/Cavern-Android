package tech.stoneapp.secminhr.cavern.articlecontent.markwonUtils

import ru.noties.prism4j.GrammarLocator
import ru.noties.prism4j.Prism4j

class CavernGrammarLocator: GrammarLocator {

    private val supportedLanguages = mutableSetOf(
            "brainfuck",
            "c",
            "clike",
            "cpp",
            "csharp",
            "css",
            "dart",
            "git",
            "go",
            "groovy",
            "java",
            "javascript",
            "json",
            "kotlin",
            "latex",
            "makefile",
            "markdown",
            "markup",
            "python",
            "scala",
            "sql",
            "swift",
            "yaml"
    )

    override fun grammar(prism4j: Prism4j, language: String): Prism4j.Grammar? {
        val cls = Class.forName("ru.noties.prism4j.languages.Prism_$language")
        val method = cls.getMethod("create", Prism4j::class.java)
        return method.invoke(null, prism4j) as Prism4j.Grammar
    }

    override fun languages() = supportedLanguages
}