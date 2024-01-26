class User {
    var name: String? = null
    var email: String? = null
    var uid: String? = null
    var lastReadMessageId: String? = null
    var apartmanAdi: String? = null // Yeni eklenen alan
    var unreadMessageCount: Int = 0

    constructor() {
        // Parametresiz kurucu
    }

    constructor(name: String?, email: String?, uid: String?, lastReadMessageId: String?, apartmanAdi: String?) {
        this.name = name
        this.email = email
        this.uid = uid
        this.lastReadMessageId = lastReadMessageId
        this.apartmanAdi = apartmanAdi
    }

    // Boş kurucu metodu kaldırabilirsiniz

    fun hasUnreadMessages(): Boolean {
        return unreadMessageCount > 0
    }

}
