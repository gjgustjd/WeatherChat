package com.miso.misoweather.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.miso.misoweather.Acitivity.home.RecyclerChatsAdapter
import com.miso.misoweather.R
import com.miso.misoweather.common.MisoActivity
import com.miso.misoweather.databinding.FragmentCommentBinding
import com.miso.misoweather.model.DTO.CommentList.CommentListResponseDto
import com.miso.misoweather.model.interfaces.MisoWeatherAPI
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception

class CommentsFragment : Fragment() {
    lateinit var binding: FragmentCommentBinding
    lateinit var commentListResponseDto: CommentListResponseDto
    lateinit var recyclerChatAdapter: RecyclerChatsAdapter
    lateinit var recyclerChat: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentCommentBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        val view = binding.root
        initializeViews()
        getCommentList()

        return view
    }

    fun initializeViews() {
        recyclerChat = binding.recyclerChat
    }

    fun getCommentList() {
        val retrofit = Retrofit.Builder()
            .baseUrl(MisoActivity.MISOWEATHER_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(MisoWeatherAPI::class.java)
        val callgetCommentList = api.getCommentList(null, 4)

        callgetCommentList.enqueue(object : Callback<CommentListResponseDto> {
            override fun onResponse(
                call: Call<CommentListResponseDto>,
                response: Response<CommentListResponseDto>
            ) {
                try {
                    Log.i("결과", "성공")
                    commentListResponseDto = response.body()!!
                    setRecyclerChats()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<CommentListResponseDto>, t: Throwable) {
                Log.i("결과", "실패 : $t")
            }
        })
    }

    fun setRecyclerChats() {
        recyclerChatAdapter = RecyclerChatsAdapter(requireActivity().baseContext, commentListResponseDto.data.commentList)
        recyclerChat.adapter = recyclerChatAdapter
        recyclerChat.layoutManager = LinearLayoutManager(requireActivity().baseContext)
    }
}