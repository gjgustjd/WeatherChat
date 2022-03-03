package com.miso.misoweather.Fragment.commentFragment

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.Button
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.miso.misoweather.Acitivity.home.RecyclerChatsAdapter
import com.miso.misoweather.common.MisoActivity
import com.miso.misoweather.databinding.FragmentCommentBinding
import com.miso.misoweather.model.DTO.CommentList.CommentListResponseDto
import com.miso.misoweather.model.DTO.CommentRegisterRequestDto
import com.miso.misoweather.model.DTO.GeneralResponseDto
import com.miso.misoweather.model.interfaces.MisoWeatherAPI
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception

@RequiresApi(Build.VERSION_CODES.O)
class CommentsFragment : Fragment() {
    lateinit var binding: FragmentCommentBinding
    lateinit var commentListResponseDto: CommentListResponseDto
    lateinit var recyclerChatAdapter: RecyclerChatsAdapter
    lateinit var refreshLayout: SwipeRefreshLayout
    lateinit var recyclerChat: RecyclerView
    lateinit var edtComment: EditText
    lateinit var btnSubmit: Button
    lateinit var activity: MisoActivity
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

    fun initializeViews() {
        activity = getActivity() as MisoActivity
        recyclerChat = binding.recyclerChat
        refreshLayout = binding.refreshLayout
        refreshLayout.setOnRefreshListener {
            updateChats()
            refreshLayout.isRefreshing = false
        }
        edtComment = binding.edtComment
        edtComment.hint = "오늘 날씨에 대한 ${activity.getPreference("nickname")!!}님의 느낌은 어떠신가요?"
        btnSubmit = binding.btnSubmit
        btnSubmit.setOnClickListener()
        {
            addComent()
        }
        recyclerChat.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                var lastVisibleItemPosition =
                    ((recyclerChat.layoutManager) as LinearLayoutManager).findLastVisibleItemPosition()
                var itemTotalCount = recyclerChat.adapter!!.itemCount - 1
                if (lastVisibleItemPosition == itemTotalCount) {
                    Log.i("Paging", "페이징")
                }
            }
        })
    }

    fun updateChats() {
        getCommentList(null)
        setRecyclerChats()
    }

    fun addComent() {
        val retrofit = Retrofit.Builder()
            .baseUrl(MisoActivity.MISOWEATHER_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(MisoWeatherAPI::class.java)
        val callAddComment = api.addComment(
            activity.getPreference("misoToken")!!,
            CommentRegisterRequestDto(edtComment.text.toString())
        )

        callAddComment.enqueue(object : Callback<GeneralResponseDto> {
            override fun onResponse(
                call: Call<GeneralResponseDto>,
                response: Response<GeneralResponseDto>
            ) {
                try {
                    Log.i("결과", "성공")
                    updateChats()
                    edtComment.text.clear()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<GeneralResponseDto>, t: Throwable) {
                Log.i("결과", "실패 : $t")
            }
        })
    }

    fun getCommentList(commentId: Int?) {
        val retrofit = Retrofit.Builder()
            .baseUrl(MisoActivity.MISOWEATHER_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(MisoWeatherAPI::class.java)
        val callgetCommentList = api.getCommentList(commentId, 5)

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
        try {
            recyclerChatAdapter = RecyclerChatsAdapter(
                activity.baseContext,
                commentListResponseDto.data.commentList,
                true
            )
            recyclerChat.adapter = recyclerChatAdapter
            recyclerChat.layoutManager = LinearLayoutManager(activity.baseContext)
        }catch (e:Exception)
        {
            e.printStackTrace()
        }
    }
}