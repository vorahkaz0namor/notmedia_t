package ru.netology.nmedia.adapter

import androidx.recyclerview.widget.DiffUtil
import ru.netology.nmedia.dto.Payload
import ru.netology.nmedia.dto.Post

class PostItemCallback : DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean =
        oldItem == newItem

    override fun getChangePayload(oldItem: Post, newItem: Post): Any =
        Payload(
            id = newItem.id,
            likes = newItem.likes.takeIf { it != oldItem.likes },
            likedByMe = newItem.likedByMe.takeIf { it != oldItem.likedByMe },
            content = newItem.content.takeIf { it != oldItem.content }
        )
}