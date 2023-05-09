package ru.netology.nmedia.dto

data class Payload(
    override val id: Long,
    val likes: Int? = null,
    val likedByMe: Boolean? = null,
    val content: String? = null
) : FeedItem