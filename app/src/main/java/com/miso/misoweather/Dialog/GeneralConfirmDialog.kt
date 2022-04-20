package com.miso.misoweather.Dialog

import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import com.miso.misoweather.databinding.DialogConfirmGeneralBinding

class GeneralConfirmDialog(
    private val mContext: Context,
    private var actionListener: View.OnClickListener? = null,
    private var contentString: String,
    private val width: Float = 0.8f,
    private val height: Float = 0.3f
) : DialogFragment() {
    var actionString: String = "확인"

    constructor(
        ctx: Context,
        listener: View.OnClickListener?,
        contentString: String,
        actionString: String,
        width: Float = 0.8f,
        height: Float = 0.3f
    ) : this(ctx, listener, contentString, width, height) {
        this.actionString = actionString
    }

    private lateinit var binding: DialogConfirmGeneralBinding
    private lateinit var root: ConstraintLayout
    private lateinit var txt_content: TextView
    private lateinit var btn_action: Button
    private lateinit var btn_cancel: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = DialogConfirmGeneralBinding.inflate(inflater)
        val view = binding.root
        initializeView()
        setupDialog()
        return view
    }

    private fun initializeView() {
        root = binding.root
        txt_content = binding.txtContent
        btn_action = binding.btnAction
        btn_cancel = binding.btnCancel

    }

    override fun onResume() {
        super.onResume()
        resizeDialog()
    }

    private fun resizeDialog() {
        val windowManager = mContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        val params: ViewGroup.LayoutParams? = dialog?.window?.attributes
        val deviceWidth = size.x
        val deviceHeight = size.y
        params?.width = (deviceWidth * width).toInt()
        params?.height = (deviceHeight * height).toInt()
        dialog?.window?.attributes = params as WindowManager.LayoutParams
    }

    private fun setupDialog() {
        if (actionListener == null) {
            btn_cancel.text = "확인"
            btn_action.visibility = View.GONE
        } else {
            btn_action.setOnClickListener(actionListener)
            btn_action.text = actionString
        }

        btn_cancel.setOnClickListener()
        {
            dismiss()
        }
        txt_content.text = contentString
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }
}