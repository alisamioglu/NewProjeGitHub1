class User {
    var name: String? = null
    var email: String? = null
    var uid: String? = null
    var lastReadMessageId: String? = null // Yeni eklenen alan
    var unreadMessageCount: Int = 0

    constructor() {}

    constructor(name: String?, email: String?, uid: String?, lastReadMessageId: String?) {
        this.name = name
        this.email = email
        this.uid = uid
        this.lastReadMessageId = lastReadMessageId
    }

    fun hasUnreadMessages(): Boolean {
        return unreadMessageCount > 0
    }
}
