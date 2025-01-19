package com.sspirit.nadiiaspaceassistant.utils

private interface IdEquatableInterface {
    val id: String
}

abstract class IdEquatableEntity: IdEquatableInterface {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is IdEquatableInterface) return false
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()
}