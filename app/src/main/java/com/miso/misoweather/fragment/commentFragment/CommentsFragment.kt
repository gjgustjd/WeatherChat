package com.miso.misoweather.fragment.commentFragment

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.miso.misoweather.R
import com.miso.misoweather.activity.chatmain.ChatMainActivity
import com.miso.misoweather.activity.chatmain.ChatMainViewModel
import com.miso.misoweather.databinding.FragmentCommentBinding
import com.miso.misoweather.model.dto.commentList.CommentListResponseDto
import com.miso.misoweather.model.dto.CommentRegisterRequestDto
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
@ActivityRetainedScoped
class CommentsFragment @Inject constructor() : Fragment() {
    val viewModel by activityViewModels<ChatMainViewModel>()
    private lateinit var binding: FragmentCommentBinding
    lateinit var activity: ChatMainActivity
    val commentListResponseDto = MutableLiveData<CommentListResponseDto>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        activity = getActivity() as ChatMainActivity
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_comment, container, false)
        binding.lifecycleOwner = activity
        binding.activity = activity
        binding.fragment = this
        setupCommentList(null)

        return binding.root
    }

    fun addComent() {
        val edtComment = binding.edtComment
        if (edtComment.text.toString().length < 2) {
            Toast.makeText(context, "텍스트를 2자 이상 입력해주세요.", Toast.LENGTH_SHORT).show()
        } else {
            lifecycleScope.launch {
                viewModel.addComment(
                    viewModel.misoToken,
                    CommentRegisterRequestDto(edtComment.text.toString())
                )
                {
                    try {
                        Log.i("결과", "성공")
                        setupCommentList(null)
                        edtComment.text.clear()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    fun setupCommentList(commentId: Int?) {
        lifecycleScope.launch {
            val response = viewModel.getCommentList(commentId, 10)
            commentListResponseDto.value = response.body()!!
        }
    }
}