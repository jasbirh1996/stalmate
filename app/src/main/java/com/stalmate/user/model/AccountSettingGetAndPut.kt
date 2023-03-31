package com.stalmate.user.model

data class AccountSettingGetAndPut(
    val message: String = "", // success !!
    val reponse: Reponse = Reponse()
) {
    data class Reponse(
        val __v: Int = 0, // 0
        val _id: String = "", // 642453d8365d5c0bde4a8d30
        val call: Call = Call(),
        val chat: Chat = Chat(),
        val funtime: Funtime = Funtime(),
        val games: Games = Games(),
        val groups: Groups = Groups(),
        val jobs: Jobs = Jobs(),
        val page: Page = Page(),
        val post: Post = Post(),
        val setting: Setting = Setting(),
        val user_id: String = "" // 6422398ad7070c51811ff0f9
    ) {
        data class Call(
            val light: String = "", // White
            val notification_tones: String = "", // Sweet
            val popup_tones: Boolean = false // true
        )

        data class Chat(
            val conversion_tones: Boolean = false, // true
            val light: String = "", // White
            val popup_tones: Boolean = false // true
        )

        data class Funtime(
            val light: String = "", // White
            val notification_tones: String = "", // Sweet
            val popup_tones: Boolean = false // true
        )

        data class Games(
            val light: String = "", // White
            val notification_tones: String = "", // Sweet
            val popup_tones: Boolean = false // true
        )

        data class Groups(
            val broadcast_tones: Boolean = false, // true
            val light: String = "", // White
            val notification_tones: String = "", // Sweet
            val popup_tones: Boolean = false // true
        )

        data class Jobs(
            val light: String = "", // White
            val notification_tones: String = "", // Sweet
            val popup_tones: Boolean = false // true
        )

        data class Page(
            val light: String = "", // White
            val notification_tones: String = "", // Sweet
            val popup_tones: Boolean = false // true
        )

        data class Post(
            val light: String = "", // White
            val notification_tones: String = "", // Sweet
            val popup_tones: Boolean = false // true
        )

        data class Setting(
            val conversion_tones: Boolean = false, // false
            val light: String = "", // White
            val notification_tones: String = "" // Sweet
        )
    }
}