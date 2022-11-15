package ru.netology.nmedia.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.R
import ru.netology.nmedia.util.CompanionNotMedia.ATTACHMENT_PREVIEW
import ru.netology.nmedia.util.CompanionNotMedia.ATTACHMENT_URI
import ru.netology.nmedia.util.CompanionNotMedia.POST_CONTENT
import ru.netology.nmedia.util.CompanionNotMedia.POST_ID
import ru.netology.nmedia.adapter.OnInteractionListenerImpl
import ru.netology.nmedia.adapter.PostAdapter
import ru.netology.nmedia.databinding.FragmentFeedBinding
import ru.netology.nmedia.viewmodel.PostViewModel

class FeedFragment : Fragment(R.layout.fragment_feed) {
    // Таким макаром надо расшарить ViewModel в каждом фрагменте
    private val viewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )
    private var _binding: FragmentFeedBinding? = null
    private val binding: FragmentFeedBinding
        get() = _binding!!
    private lateinit var adapter: PostAdapter
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFeedBinding.inflate(inflater)
        return binding.root
    }

    // После этого binding нужно обязательно об-null'ять
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Ф-ия onCreatedView() возвращает View, которая и передается в качестве параметра
    // view для ф-ии onViewCreated()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        subscribe()
        setupListeners()
    }

    private fun initViews() {
        adapter = PostAdapter(OnInteractionListenerImpl(viewModel))
        binding.posts.adapter = adapter
        navController = findNavController()
    }

    // После перехода на просмотр конкретного поста для binding.content
    // надо установить свойство => android:autoLink="web"
    private fun subscribe() {
        viewModel.apply {
            data.observe(viewLifecycleOwner) { posts ->
                adapter.submitList(posts)
            }
            edited.observe(viewLifecycleOwner) { post ->
                if (post.id != 0L)
                    navController.navigate(
                        R.id.action_feedFragment_to_newPostFragment,
                        Bundle().apply { POST_CONTENT = post.content }
                    )
            }
            hasShared.observe(viewLifecycleOwner) { post ->
                if (post.id != 0L)
                    navController.navigate(
                        R.id.action_feedFragment_to_sharePostFragment,
                        Bundle().apply { POST_CONTENT = post.content }
                    )
            }
            singlePostToView.observe(viewLifecycleOwner) { post ->
                if (post.id != 0L) {
                    navController.navigate(
                        R.id.action_feedFragment_to_singlePostFragment,
                        Bundle().apply { POST_ID = post.id }
                    )
                }
            }
            viewingAttachments.observe(viewLifecycleOwner) { post ->
                if (post.id != 0L) {
                    val first = post.attachments.firstOrNull()
                    navController.navigate(
                        R.id.action_feedFragment_to_attachmentsFragment,
                        Bundle().apply {
                            POST_CONTENT = post.content
                            ATTACHMENT_PREVIEW = first?.preview.toString()
                            ATTACHMENT_URI = first?.attachment ?: "https://"
                        }
                    )
                }
            }
        }
    }

    private fun setupListeners() {
        binding.addNewPost.setOnClickListener {
            navController.navigate(
                R.id.action_feedFragment_to_newPostFragment
            )
        }
    }
}