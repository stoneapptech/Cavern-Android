package tech.stoneapp.secminhr.cavern.cavernObject

data class Role(val level: Int, val name: String, val canPostArticle: Boolean,
                val canLike: Boolean, val canComment: Boolean)