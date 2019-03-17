package tech.stoneapp.secminhr.cavern.cavernObject

data class Account(
        val username: String,
        val nickname: String,
        val role: Role,
        val avatarLink: String,
        val postCount: Int,
        val email: String? = null //This is used to indicate whether it is the current user
)