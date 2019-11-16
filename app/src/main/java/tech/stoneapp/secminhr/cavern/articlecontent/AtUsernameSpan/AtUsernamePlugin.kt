package tech.stoneapp.secminhr.cavern.articlecontent.AtUsernameSpan

import ru.noties.markwon.AbstractMarkwonPlugin
import ru.noties.markwon.MarkwonVisitor


class AtUsernamePlugin(val visitor: AtUsernameVisitor): AbstractMarkwonPlugin() {
    override fun configureVisitor(builder: MarkwonVisitor.Builder) {
        builder.on(AtUsernameNode::class.java, visitor)
    }
}