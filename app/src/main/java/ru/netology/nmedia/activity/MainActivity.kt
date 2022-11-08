package ru.netology.nmedia.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.viewModels
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.OnInteractionListenerImpl
import ru.netology.nmedia.adapter.PostAdapter
import ru.netology.nmedia.contract.NewPostResultContract
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.viewmodel.PostViewModel

const val PREVIEW_KEY = "preview"
const val TAG = "stuff"
val message = listOf(
    "root",
    "avatar",
    "like",
    "share",
    "attachments",
    "menu",
    "remove",
    "edit"
)

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val viewModel: PostViewModel by viewModels()
    private lateinit var adapter: PostAdapter
    private lateinit var newPostLauncher: ActivityResultLauncher<String?>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*Без использования инструмента ViewBinding верстка помещается на Активити вот так:
        setContentView(R.layout.activity_main)
        А уже с использованием ViewBinding, вот так:*/
        setContentView(binding.root)
        initViews()
        subscribe()
        setupListeners()
        /* Из вебинара "Constrained Layout":
           Accessibility - чтобы экран был доступен для лиц с ограниченными возможностями.
           Здесь указывается текст, который будет описывать содержимое изображения.
           => android:contentDescription <= Это прописывается в файле верстки.
           ellipsize - это свойство, указывающее в каком месте текст должен обрезаться,
           если он не входит в размер контейнера (начало, конец или середина).
           По умолчанию -> android:ellipsize="end".
           Чтобы избежать обрезания имени автора сверху при маленьком размере аватарки,
           следует указать привязку для аватарки в виде:
           app:layout_constraintBottom_toBottomOf="@id/barrierTop".*/
    }

    private fun initViews() {
        adapter = PostAdapter(OnInteractionListenerImpl(viewModel))
        binding.posts.adapter = adapter
        newPostLauncher =
            registerForActivityResult(NewPostResultContract()) { result ->
                addingNewPost(result)
            }
    }

    private fun subscribe() {
        viewModel.data.observe(this) { posts ->
            adapter.submitList(posts)
        }
        viewModel.edited.observe(this) { post ->
            if (post.id != 0L)
                newPostLauncher.launch(post.content)
        }
        viewModel.hasShared.observe(this) { post ->
            if (post.id != 0L) {
                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, post.content)
                    type = "text/plain"
                }
                val shareIntent =
                    Intent.createChooser(intent, getString(R.string.chooser_share_post))
                startActivity(shareIntent)
            }
        }
        viewModel.viewingAttachments.observe(this) {post ->
            if (post.id != 0L) {
                startActivity(
                    Intent(this, AttachmentsActivity::class.java).apply {
                        val first = post.attachments.first()
                        putExtra(Intent.EXTRA_TEXT, post.content)
                        putExtra(PREVIEW_KEY, first.preview)
                        putExtra(Intent.EXTRA_ORIGINATING_URI, first.attachment)
                    }
                )
            }
        }
    }

    private fun setupListeners() {
        binding.addNewPost.setOnClickListener {
            newPostLauncher.launch("")
        }
    }

    private fun addingNewPost(content: String?) {
        val initialContent = viewModel.edited.value?.content
        val postId = viewModel.savePost(content)
        if (content != initialContent)
            if (!content.isNullOrBlank())
                Toast.makeText(
                    this@MainActivity,
                    binding.root.context.getString(
                        if (postId == 0L)
                            R.string.new_post_has_created
                        else
                            R.string.post_has_edited
                    ),
                    Toast.LENGTH_LONG
                ).show()
    }
}