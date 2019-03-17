package tech.stoneapp.secminhr.cavern.articlecontent.AtUsernameSpan

import org.commonmark.node.CustomNode
import org.commonmark.node.Delimited

class AtUsernameNode(val username: String): CustomNode(), Delimited {
    override fun getOpeningDelimiter() = "@"

    override fun getClosingDelimiter() = "@"
}