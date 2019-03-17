package tech.stoneapp.secminhr.cavern.cavernObject

enum class Role(val v: String) {
    Member("會員"),
    Author("作者"),
    Nugget("麥克雞塊"),
    Admin("管理者");

    companion object {
        fun createFromValue(v: String): Role {
            return Role.values().filter {
                it.v == v
            }[0]
        }
    }
}