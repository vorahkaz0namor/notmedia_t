package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.repository.*

private val empty = Post(
    id = 0,
    author = "",
    published = "",
    content = ""
)

class PostViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PostRepository = PostRepositoryFileImpl(application)
    val data = repository.getAll()
    // Variable to hold editing post
    val edited = MutableLiveData(empty)
    // Variable to hold sharing post
    val hasShared = MutableLiveData(empty)
    // Variable to hold viewing post attachments
    val viewingAttachments = MutableLiveData(empty)

    private fun validation(text: CharSequence?) =
        (!text.isNullOrBlank() && edited.value?.content != text.trim())

    private fun save(newContent: String) {
        edited.value?.let {
            repository.save(it.copy(content = newContent))
        }
    }

    private fun clearEditedValue() {
        edited.value = empty
    }

    fun savePost(text: CharSequence?): Long? {
        if (validation(text)) {
            save(text.toString())
        }
        val result = edited.value?.id
        clearEditedValue()
        return result
    }

    fun edit(post: Post) {
        edited.value = post
    }

    fun likeById(id: Long) = repository.likeById(id)
    fun shareById(post: Post) {
        hasShared.value = post
        repository.shareById(post.id)
    }
    fun showAttachments(post: Post) {
        viewingAttachments.value = post
    }
    fun viewById(id: Long) = repository.viewById(id)
    fun removeById(id: Long) = repository.removeById(id)
}