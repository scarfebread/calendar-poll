package service

import kotlinx.browser.document

const val EMPTY = ""

fun setPollCookie(id: String? = EMPTY) {
    if (id == null) {
        document.cookie = "${Poll.cookie}=${EMPTY};path=/poll"
    } else {
        document.cookie = "${Poll.cookie}=${id};path=/poll"
    }
}