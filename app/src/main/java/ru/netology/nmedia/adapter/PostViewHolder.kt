package ru.netology.nmedia.adapter

import android.util.Log
import android.view.View
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.TAG
import ru.netology.nmedia.activity.message
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.dto.*

class PostViewHolder(
    private val binding: CardPostBinding,
    private val onInteractionListener: OnInteractionListener
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(post: Post) {
        binding.apply {
            author.text = post.author
            published.text = post.published
            content.text = post.content
            avatar.setImageResource(
                if (author.text.contains("нетология", true))
                    R.drawable.netology
                else
                    if (author.text.contains("гитарин", true))
                        R.drawable.guitarin
                    else
                        R.drawable.ic_local_user_24
            )
            likes.isChecked = post.likedByMe
            likes.text = CountDisplay.show(post.likes)
            share.text = CountDisplay.show(post.shares)
            if (post.attachments.isNotEmpty()) {
                attachments.text = CountDisplay.show(post.attachments.size)
                attachments.visibility = View.VISIBLE
            }
            else
                attachments.visibility = View.INVISIBLE
            views.text = CountDisplay.show(post.views)
            //  Show isClickable()
            clickableProperty.text =
                "author=${author.isClickable}\n" +
                "published=${published.isClickable}\n" +
                "content=${content.isClickable}\n" +
                "avatar=${avatar.isClickable}\n" +
                "menu=${menu.isClickable}\n" +
                "likesCount=${likes.isClickable}\n" +
                "sharesCount=${share.isClickable}\n" +
                "viewsCount=${views.isClickable}\n" +
                "clickability=${clickableProperty.isClickable}"
            //  Click root
            root.setOnClickListener {
                Log.d(TAG, message[0])
            }
            //  Click Avatar
            avatar.setOnClickListener {
                Log.d(TAG, message[1])
            }
            // Click Like
            likes.setOnClickListener {
                Log.d(TAG, message[2])
                onInteractionListener.onLike(post)
            }
            // Click Share
            share.setOnClickListener {
                Log.d(TAG, message[3])
                onInteractionListener.onShare(post)
            }
            attachments.setOnClickListener {
                Log.d(TAG, message[4])
                onInteractionListener.onAttachments(post)
            }
            menu.setOnClickListener {
                Log.d(TAG, message[5])
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.options_post)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            // Click Remove
                            R.id.remove -> {
                                Log.d(TAG, message[6])
                                onInteractionListener.onRemove(post)
                                true
                            }
                            // Click Edit
                            R.id.edit -> {
                                Log.d(TAG, message[7])
                                onInteractionListener.onEdit(post)
                                true
                            }
                            else -> false
                        }
                    }
                }.show()
            }
        }
    }
}