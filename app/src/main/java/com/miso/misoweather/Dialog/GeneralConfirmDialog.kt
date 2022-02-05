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
    var mContext: Context,
    var actionListener: View.OnClickListener,
    var contentString: String,
) : DialogFragment() {
    var actionString: String = "확인"

    constructor(
        ctx: Context,
        listener: View.OnClickListener,
        contentString: String,
        actionString: String
    ) : this(ctx, listener, contentString) {
        this.actionString = actionString
    }

    lateinit var binding: DialogConfirmGeneralBinding
    lateinit var root: ConstraintLayout
    lateinit var txt_content: TextView
    lateinit var btn_action: Button
    lateinit var btn_cancel: Button

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

    fun initializeView() {
        root = binding.root
        txt_content = binding.txtContent
        btn_action = binding.btnAction
        btn_cancel = binding.btnCancel

    }

    override fun onResume() {
        super.onResume()
        resizeDialog()
    }

    fun resizeDialog() {
        val windowManager = mContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        val params: ViewGroup.LayoutParams? = dialog?.window?.attributes
        val deviceWidth = size.x
        val deviceHeight = size.y
        params?.width = (deviceWidth * 0.8).toInt()
        params?.height = (deviceHeight * 0.3).toInt()
        dialog?.window?.attributes = params as WindowManager.LayoutParams
    }

    fun setupDialog() {
        btn_action.setOnClickListener(actionListener)
        btn_cancel.setOnClickListener()
        {
            dismiss()
        }
        txt_content.text = contentString
        btn_action.text = actionString
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }
}