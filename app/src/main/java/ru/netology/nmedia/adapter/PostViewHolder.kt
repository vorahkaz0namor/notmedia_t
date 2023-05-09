package ru.netology.nmedia.adapter

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.PopupMenu
import androidx.core.animation.doOnRepeat
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.dto.*

class PostViewHolder(
    private val binding: CardPostBinding,
    private val onInteractionListener: OnInteractionListener
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(post: Post) {
        fillingCardPost(post)
        setupListeners(post)
    }

    fun bind(payload: Payload) {
        payload.apply {
            likedByMe?.let {
                binding.likes.isChecked = it
                binding.likes.text = likes?.let { CountDisplay.show(it) }
                ObjectAnimator.ofFloat(
                    binding.likes,
                    View.ROTATION_Y,
                    0F, if (it) 75F else -75F, 0F
                ).apply {
                    duration = 2000
                    interpolator = DecelerateInterpolator()
                }
                    .start()
            }
            content?.let {
                binding.content.text = it
            }
        }
    }

    private fun fillingCardPost(post: Post) {
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
            clickableProperty.apply {
                text = context.getString(
                    R.string.clickable_properties,
                    author.isClickable, published.isClickable, content.isClickable,
                    avatar.isClickable, menu.isClickable, likes.isClickable,
                    share.isClickable, views.isClickable, attachments.isClickable,
                    clickableProperty.isClickable
                )
            }
        }
    }

    private fun setupListeners(post: Post) {
        binding.apply {
            root.setOnClickListener {
                onInteractionListener.toSinglePost(post)
            }
            likes.setOnClickListener {
                // Демонстрация особенностей использования анимации
//                ObjectAnimator.ofPropertyValuesHolder(
//                    it,
//                    PropertyValuesHolder.ofFloat(View.SCALE_X, 1F, 1.15F),
//                    PropertyValuesHolder.ofFloat(View.SCALE_Y, 1F, 1.15F)
//                ).apply {
//                    duration = 200
//                    repeatCount = 3000
//                    interpolator = LinearInterpolator()
//                    // 1й вариант решения проблемы
//                    doOnRepeat { anim ->
//                        if (!this@PostViewHolder.itemView.isShown)
//                            anim.end()
//                    }
//                }
//                    .start()
                onInteractionListener.onLike(post)
            }
            // Click Share
            share.setOnClickListener {
                onInteractionListener.onShare(post)
            }
            attachments.setOnClickListener {
                onInteractionListener.onAttachments(post)
            }
            menu.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.options_post)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            // Click Remove
                            R.id.remove -> {
                                onInteractionListener.onRemove(post)
                                true
                            }
                            // Click Edit
                            R.id.edit -> {
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