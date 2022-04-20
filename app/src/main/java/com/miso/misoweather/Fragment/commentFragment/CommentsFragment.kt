package com.miso.misoweather.Fragment.commentFragment

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.miso.misoweather.Acitivity.chatmain.ChatMainViewModel
import com.miso.misoweather.Acitivity.home.RecyclerChatsAdapter
import com.miso.misoweather.common.MisoActivity
import com.miso.misoweather.databinding.FragmentCommentBinding
import com.miso.misoweather.model.DTO.CommentList.CommentListResponseDto
import com.miso.misoweather.model.DTO.CommentRegisterRequestDto
import java.lang.Exception
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
class CommentsFragment @Inject constructor() : Fragment() {
    private val viewModel: ChatMainViewModel by viewModels()
    private lateinit var binding: FragmentCommentBinding
    private lateinit var recyclerChatAdapter: RecyclerChatsAdapter
    private lateinit var refreshLayout: SwipeRefreshLayout
    private lateinit var recyclerChat: RecyclerView
    private lateinit var edtComment: EditText
    private lateinit var btnSubmit: Button
    private lateinit var activity: MisoActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentCommentBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = binding.root
        initializeViews()
        getCommentList(null)

        return view
    }

    private fun initializeViews() {
        activity = getActivity() as MisoActivity
        recyclerChat = binding.recyclerChat
        refreshLayout = binding.refreshLayout
        refreshLayout.setOnRefreshListener {
            getCommentList(null)
            refreshLayout.isRefreshing = false
        }
        edtComment = binding.edtComment
        edtComment.hint = "오늘 날씨에 대한 " +
                "${activity.getBigShortScale(activity.getPreference("BigScaleRegion")!!)}의" +
                " ${activity.getPreference("nickname")!!}님의 느낌은 어떠신가요?"
        btnSubmit = binding.btnSubmit
        btnSubmit.setOnClickListener()
        {
            addComent()
        }
        recyclerChat.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val lastVisibleItemPosition =
                    ((recyclerChat.layoutManager) as LinearLayoutManager).findLastVisibleItemPosition()
                val itemTotalCount = recyclerChat.adapter!!.itemCount - 1
                if (lastVisibleItemPosition == itemTotalCount) {
                    Log.i("Paging", "페이징")
                }
            }
        })
        viewModel.commentListResponse.observe(activity) {
            try {
                val responseDto = it!!.body() as CommentListResponseDto
                Log.i("결과", "성공")
                if (this::recyclerChatAdapter.isInitialized) {
                    recyclerChatAdapter.apply {
                        if (currentBindedPosition.value == itemCount - 1) {
                            comments += responseDto.data.commentList
                            notifyDataSetChanged()
                        } else if (recyclerChat.adapter == null)
                            setRecyclerChats(responseDto)
                    }
                } else
                    setRecyclerChats(responseDto)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        viewModel.addCommentResponse.observe(activity) {
            try {
                Log.i("결과", "성공")
                getCommentList(null)
                edtComment.text.clear()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun addComent() {
        if (edtComment.text.toString().length < 2) {
            Toast.makeText(context, "텍스트를 2자 이상 입력해주세요.", Toast.LENGTH_SHORT).show()
        } else {
            viewModel.addComment(
                activity.getPreference("misoToken")!!,
                CommentRegisterRequestDto(edtComment.text.toString())
            )
        }
    }

    private fun getCommentList(commentId: Int?) {
        viewModel.getCommentList(
            commentId,
            10
        )
    }

    private fun setRecyclerChats(commentListResponseDto: CommentListResponseDto) {
        try {
            recyclerChatAdapter = RecyclerChatsAdapter(
                commentListResponseDto.data.commentList,
                true
            )
            recyclerChat.apply {
                adapter = recyclerChatAdapter
                layoutManager = LinearLayoutManager(activity.baseContext)
            }
            recyclerChatAdapter.currentBindedPosition.observe(activity) {
                if (it == recyclerChatAdapter.comments.size - 1) {
                    getCommentList(recyclerChatAdapter.getCommentItem(it).id)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}