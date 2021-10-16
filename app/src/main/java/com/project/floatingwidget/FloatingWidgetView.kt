package com.project.floatingwidget

import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.floating_widget_layout.view.*

class FloatingWidgetView : ConstraintLayout, View.OnTouchListener {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private val layoutParams = WindowManager.LayoutParams(
        WindowManager.LayoutParams.WRAP_CONTENT,
        WindowManager.LayoutParams.WRAP_CONTENT,
        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
        PixelFormat.TRANSLUCENT
    )

    private var x: Int = 0
    private var y: Int = 0
    private var touchX: Float = 0f
    private var touchY: Float = 0f
    private var clickStartTimer: Long = 0
    private val windowManager: WindowManager

    init {
        View.inflate(context, R.layout.floating_widget_layout, this)
        setOnTouchListener(this)

        layoutParams.x = x
        layoutParams.y = y

        windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.addView(this, layoutParams)

        close_btn.setOnClickListener {
            context.stopService(Intent(context, FloatingWidgetService::class.java))
        }
        close_button.setOnClickListener {

            collapse_view.visibility= View.VISIBLE
            expanded_container.visibility=View.GONE
        }

    }

    companion object {
        private const val CLICK_DELTA = 200
    }


    override fun onTouch(v: View, event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                clickStartTimer = System.currentTimeMillis()

                x = layoutParams.x
                y = layoutParams.y

                touchX = event.rawX
                touchY = event.rawY
            }
            MotionEvent.ACTION_UP -> {
                if (System.currentTimeMillis() - clickStartTimer < CLICK_DELTA) {
                    collapse_view.visibility= View.GONE
                    expanded_container.visibility=View.VISIBLE

                }
            }
            MotionEvent.ACTION_MOVE -> {
                layoutParams.x = (x + event.rawX - touchX).toInt()
                layoutParams.y = (y + event.rawY - touchY).toInt()
                windowManager.updateViewLayout(this, layoutParams)
            }
        }
        return true
    }

}
