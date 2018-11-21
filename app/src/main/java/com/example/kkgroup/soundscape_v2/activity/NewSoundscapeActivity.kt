package com.example.kkgroup.soundscape_v2.activity

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Vibrator
import android.support.v4.widget.ViewDragHelper
import android.support.v7.widget.Toolbar
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import com.example.kkgroup.soundscape_v2.R
import com.example.kkgroup.soundscape_v2.Tools.ConstantValue
import com.example.kkgroup.soundscape_v2.Tools.Tools
import kotlinx.android.synthetic.main.activity_new_soundscape.*
import org.jetbrains.anko.startActivity

class NewSoundscapeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_soundscape)

        initToolbar()
        initListeners()
    }

    private fun initToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Soundscape V2"
        Tools.setSystemBarColor(this, R.color.colorPrimary)
    }

    private fun initListeners() {
        fabInNewSoundscapePage.setOnClickListener { startActivity<SelectAudioActivity>() }
    }
}

/**
 * I added comments to each method to help understand
 */
class MyLinearLayout4(val mContext: Context, attrs: AttributeSet?) : LinearLayout(mContext, attrs) {

    internal var viewDragHelper: ViewDragHelper? = null
    private var mVibrator: Vibrator? = null
    private var mCurrentTop: Int = 0
    private var mCurrentLeft: Int = 0

    init {
        initViewDragHelper()
    }

    private fun initViewDragHelper() {
        mVibrator = mContext.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        val dragCallback = object : ViewDragHelper.Callback() {

            /**
             * This method will be invoked each time when view is dragged
             * @param child
             * @param pointerId
             * @return true  -> Capture this child view's event -> view can be dragged
             *         false -> Do not Capture this child view's event -> view can NOT be dragged
             */
            override fun tryCaptureView(child: View, pointerId: Int): Boolean {
                Tools.log_e("tryCaptureView, 原始Left=" + child.left + "; 原始Top=" + child.top)
                return true
            }

            /**
             * This method will restrict the **Horizontal** distance that the dragged view can be moved
             * We have to setup the max value that view can be moved, Otherwise it will exceed the edge of the screen
             *
             * @param child
             * @param left  The value of the dragged view is theoretically to be slid to the horizontal direction
             *              拖动的 View 理论上将要滑动到的水平方向上的值
             * @param dx    The speed of the slide, in px per second.
             * @return  The value of the actual x coordinate in the horizontal direction
             */
            override fun clampViewPositionHorizontal(child: View, left: Int, dx: Int): Int {
                Tools.log_e( "clampViewPositionHorizontal: left=$left; dx=$dx")
                Tools.log_e( "clampViewPositionHorizontal: width=$width; child.width=${child.width}; paddingRight: $paddingRight")
                // The minimum x coordinate value cannot be less than leftBound
                val leftBound = paddingLeft
                // The maximum x coordinate value cannot be greater than rightBound
                val rightBound = width - child.width - paddingRight
                val newLeft = Math.min(Math.max(left, leftBound), rightBound)
                mCurrentLeft = newLeft
                return newLeft
            }

            /**
             * This method will restrict the **Vertical** distance that the dragged view can be moved
             * We have to setup the max value that view can be moved, Otherwise it will exceed the edge of the screen
             *
             * @param child
             * @param top  The value of the dragged view is theoretically to be slid to the vertical direction
             *              拖动的 View 理论上将要滑动到的数值方向上的值
             * @param dy    The speed of the slide, in px per second.
             * @return  The value of the actual y coordinate in the vertical direction
             */
            override fun clampViewPositionVertical(child: View, top: Int, dy: Int): Int {
                Tools.log_e( "clampViewPositionVertical: top=$top; dy=$dy")
                Tools.log_e( "clampViewPositionVertical: height=$height; child.height=${child.height}; paddingRight: $paddingBottom")
                // The minimum y coordinate value cannot be less than topBound
                val topBound = paddingTop
                // The maximum y coordinate value cannot be greater than bottomBound
                val bottomBound = height - child.height - paddingBottom
                val newTop = Math.min(Math.max(top, topBound), bottomBound)
                mCurrentTop = newTop
                return newTop
            }


            /**
             * This method will be invoked when the View is no longer being dragged.
             *
             * @param releasedChild
             * @param xvel
             * @param yvel
             */
//            override fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float) {
//                super.onViewReleased(releasedChild, xvel, yvel)
//                Tools.log_e("onViewReleased, xvel=$xvel; yvel=$yvel")
//                val childWidth = releasedChild.width
//                val parentWidth = width
//                val leftBound = paddingLeft// left edge
//                val rightBound = width - releasedChild.width - paddingRight// right edge
//                if (childWidth / 2 + mCurrentLeft < parentWidth / 2) {
//                    viewDragHelper!!.settleCapturedViewAt(leftBound, mCurrentTop)
//                } else {
//                    viewDragHelper!!.settleCapturedViewAt(rightBound, mCurrentTop)
//                }
//                invalidate() // System method to refresh view position
//            }


        }

        viewDragHelper = ViewDragHelper.create(this, 1.0f, dragCallback)
    }


    /**
     * Touch Event is intercepted and pass it to our viewDragHelper to handle
     */
    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return viewDragHelper!!.shouldInterceptTouchEvent(ev)
    }

    /**
     * handle touch event
     */
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            Tools.log_e("mVibrator: ${mVibrator == null}")
            mVibrator?.let {
                /**
                 * The following method requires SDK >= 26, Our Target SDK is 21
                 * We are lazy to do SDK check, so we just use deprecated method , let me go this time :)
                 */

                // if (it.hasVibrator()) it.vibrate(VibrationEffect.createOneShot(ConstantValue.vibrationTime, -1))
                if (it.hasVibrator()) it.vibrate(ConstantValue.vibrationTime)
            }
        }

        viewDragHelper?.processTouchEvent(event)
        return true
    }


    /**
     * To farce a view to draw, in other words, redraw the view tree
     */
    override fun computeScroll() {
        super.computeScroll()
        viewDragHelper?.let {
            if (it.continueSettling(true)) {
                invalidate()
            }
        }
    }
}

