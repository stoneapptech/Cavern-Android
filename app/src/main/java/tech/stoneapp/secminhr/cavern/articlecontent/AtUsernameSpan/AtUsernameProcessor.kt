package tech.stoneapp.secminhr.cavern.articlecontent.AtUsernameSpan

import org.commonmark.node.Text
import org.commonmark.parser.delimiter.DelimiterProcessor
import org.commonmark.parser.delimiter.DelimiterRun

class AtUsernameProcessor: DelimiterProcessor {
    override fun getClosingCharacter() = '@'

    override fun getDelimiterUse(opener: DelimiterRun?, closer: DelimiterRun?) =
            if (opener!!.length() >= 1 && closer!!.length() >= 1) 1 else 0

    override fun getMinLength() = 1

    override fun getOpeningCharacter() = '@'

    override fun process(opener: Text?, closer: Text?, delimiterUse: Int) {
        if(opener?.next is Text && opener.next.next == closer) {
            val regex = Regex("[a-zA-z0-9]+")
            val text = (opener.next as Text).literal
            if(regex.matches(text)) {
                opener.next.unlink()
                opener.insertBefore(AtUsernameNode(text))
            }
        }
    }
}