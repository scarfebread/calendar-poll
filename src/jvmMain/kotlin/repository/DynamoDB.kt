package repository

import software.amazon.awssdk.services.dynamodb.model.AttributeValue


const val TABLE = "calendar-poll"
const val VOTE_INDEX = "sessionId-date-index"
const val USER = "user-"
const val POLL = "poll-"
const val VOTE = "vote-"

fun userId(id: String): AttributeValue {
    return AttributeValue.builder().s(USER + id).build()
}

fun pollId(id: String): AttributeValue {
    return AttributeValue.builder().s(POLL + id).build()
}

fun voteId(id: String): AttributeValue {
    return AttributeValue.builder().s(VOTE + id).build()
}

fun string(value: String?): AttributeValue {
    return AttributeValue.builder().s(value).build()
}

fun boolean(value: Boolean): AttributeValue {
    return AttributeValue.builder().bool(value).build()
}

fun toString(value: AttributeValue?): String? {
    if (value == null) {
        return null
    }

    // TODO this is actually mental
    val string = value.s().substringAfter("AttributeValue(S=").substringBefore(")")

    // TODO better way to handle this
    return string
        .replace("poll-", "")
        .replace("user-", "")
        .replace("vote-", "")
}

fun toBoolean(value: AttributeValue?): Boolean {
    if (value == null) {
        return false
    }

    return value.bool()
}