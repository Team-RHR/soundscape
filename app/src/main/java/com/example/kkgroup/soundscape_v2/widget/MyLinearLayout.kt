package com.example.kkgroup.soundscape_v2.widget

import android.content.Context
import android.os.Vibrator
import android.support.v4.widget.ViewDragHelper
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import com.example.kkgroup.soundscape_v2.Tools.ConstantValue
import com.example.kkgroup.soundscape_v2.Tools.Tools
import org.jetbrains.anko.sdk25.coroutines.onTouch

/**
 * @ Author     ：Hao Zhang.
 * @ Date       ：Created in 0:14 2018/11/25
 * @ Description：Build for Metropolia project
 */

/**
 * I added comments to each method to help understand
 */
class MyLinearLayout(val mContext: Context, attrs: AttributeSet?) : LinearLayout(mContext, attrs) {

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
                return true
            }

            /**
             * This method will restrict the **Horizontal** distance that the dragged view can be moved
             * We have to setup the max value that view can be moved, Otherwise it will exceed the edge of the screen
             *
             * @param child
             * @param left  The value of the dragged view is theoretically to be slid to the horizontal direction
             * @param dx    The speed of the slide, in px per second.
             * @return  The value of the actual x coordinate in the horizontal direction
             */
            override fun clampViewPositionHorizontal(child: View, left: Int, dx: Int): Int {

                // The minimum x coordinate value cannot be less than leftBound
                val leftBound = paddingLeft
                // The maximum x coordinate value cannot be greater than rightBound
                val rightBound = width - child.width - paddingRight
                val newLeft = Math.min(Math.max(left, leftBound), rightBound)
                mCurrentLeft = newLeft

                // Tools.log_e("clampViewPositionHorizontal: left=$left --> leftBound: $leftBound --> rightBound: $rightBound --> newLeft: $newLeft")
                return newLeft
            }

            /**
             * This method will restrict the **Vertical** distance that the dragged view can be moved
             * We have to setup the max value that view can be moved, Otherwise it will exceed the edge of the screen
             *
             * @param child
             * @param top  The value of the dragged view is theoretically to be slid to the vertical direction
             * @param dy    The speed of the slide, in px per second.
             * @return  The value of the actual y coordinate in the vertical direction
             */
            override fun clampViewPositionVertical(child: View, top: Int, dy: Int): Int {

                // The minimum y coordinate value cannot be less than topBound
                val topBound = paddingTop
                // The maximum y coordinate value cannot be greater than bottomBound
                val bottomBound = height - child.height - paddingBottom
                val newTop = Math.min(Math.max(top, topBound), bottomBound)
                mCurrentTop = newTop

                // Tools.log_e("clampViewPositionVertical: top=$top --> topBound: $topBound --> bottomBound: $bottomBound --> newTop: $newTop")
                return newTop
            }


            /**
             * This method will be invoked when the View is no longer being dragged.
             *
             * @param releasedChild
             * @param xvel velocity of x direction
             * @param yvel velocity of y direction
             */
            override fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float) {
                super.onViewReleased(releasedChild, xvel, yvel)

                /**
                 * go to the edge of the screen
                 */
//                val childWidth = releasedChild.width
//                val parentWidth = width
//                val leftBound = paddingLeft// left edge
//                val rightBound = width - releasedChild.width - paddingRight// right edge
//                if (childWidth / 2 + mCurrentLeft < parentWidth / 2) {
//                    viewDragHelper?.settleCapturedViewAt(leftBound, mCurrentTop)
//                } else {
//                    viewDragHelper?.settleCapturedViewAt(rightBound, mCurrentTop)
//                }
//                invalidate() // System method to refresh view position

                /**
                 * go to the center of the screen
                 */
                val centerPoint = width / 2 - releasedChild.width / 2
                viewDragHelper?.settleCapturedViewAt(centerPoint, mCurrentTop)
                invalidate() // System method to refresh view position
            }

            override fun getViewHorizontalDragRange(child: View): Int {
                return measuredWidth - child.measuredWidth
            }

            override fun getViewVerticalDragRange(child: View): Int {
                return measuredHeight - child.measuredHeight
            }
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
            mVibrator?.let {
                /**
                 * The following method requires SDK >= 26, Our Target SDK is 21
                 * We are lazy to do SDK check, so we just use deprecated method , let me go this time :)
                 */

                // if (it.hasVibrator()) it.vibrate(VibrationEffect.createOneShot(ConstantValue.vibrationTime, -1))
                if (it.hasVibrator()) it.vibrate(ConstantValue.vibrationTime)
            }

        }

        //当按下时处理
//        if (event.getAction() == MotionEvent.ACTION_DOWN) {
//            xDown = event.getX();
//            yDown = event.getY();
//            Log.v("OnTouchListener", "Down");
//        } else if (event.getAction() == MotionEvent.ACTION_UP) {// 松开处理
//            //获取松开时的x坐标
//            if (isLongClickModule) {
//                isLongClickModule = false;
//                isLongClicking = false;
//            }
//            xUp = event.getX();
//
//            Tools.log_e("up")
//            //按下和松开绝对值差当大于20时滑动，否则不显示
//            if ((xUp - xDown) > 20) {
//
//            } else if ((xUp - xDown) < -20) {
//                //添加要处理的内容
//            } else if (0.0f == (xDown - xUp)) {
//                Tools.log_e( "Up == 0 点击了")
//            }
//        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
//            //当滑动时背景为选中状态 //检测是否长按,在非长按时检测
//            if (!isLongClickModule) {
//                isLongClickModule = isLongPressed(xDown, yDown, event.getX(),
//                        event.getY(), event.getDownTime(), event.getEventTime(), 300);
//            }
//            if (isLongClickModule && !isLongClicking) {
//                //处理长按事件
//
//
//
//                isLongClicking = true; }
//        } else {
//            //其他模式
//            return false;
//        }

        viewDragHelper?.processTouchEvent(event)
        return true
    }

    private var xDown: Float = 0.0f
    private var yDown: Float = 0.0f
    private var xUp: Float = 0.0f

    private var isLongClickModule = false
    private var isLongClicking = false

    /* 判断是否有长按动作发生
	   * @param lastX 按下时X坐标
	   * @param lastY 按下时Y坐标
	   * @param thisX 移动时X坐标
	   * @param thisY 移动时Y坐标
	   * @param lastDownTime 按下时间
	   * @param thisEventTime 移动时间
	   * @param longPressTime 判断长按时间的阀值
	   */
    private fun isLongPressed(lastX: Float, lastY: Float,
                              thisX: Float, thisY: Float,
                              lastDownTime: Long, thisEventTime: Long,
                              longPressTime: Long): Boolean {
        val offsetX = Math.abs(thisX - lastX)
        val offsetY = Math.abs(thisY - lastY)
        val intervalTime = thisEventTime - lastDownTime
        return if (offsetX <= 10 && offsetY <= 10 && intervalTime >= longPressTime) {
            true
        } else false
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

