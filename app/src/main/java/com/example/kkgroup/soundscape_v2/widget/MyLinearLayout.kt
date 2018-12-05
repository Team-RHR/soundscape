package com.example.kkgroup.soundscape_v2.widget

import android.content.Context
import android.os.Vibrator
import android.support.v4.widget.ViewDragHelper
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import com.example.kkgroup.soundscape_v2.Model.AudioCardModel
import com.example.kkgroup.soundscape_v2.Tools.Tools

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
    // private var audioCardList = mutableListOf<AudioCardModel>()

    init {
        initViewDragHelper()
    }

//    fun insertAudioCard(audioCardModel: AudioCardModel){
//        audioCardList.add(audioCardModel)
//    }
//
//    fun removeAudioCard(audioCardModel: AudioCardModel){
//        if (audioCardList.contains(audioCardModel)) {
//            audioCardList.remove(audioCardModel)
//        }
//    }
//
//    fun getAudioCardList(): MutableList<AudioCardModel> {
//        return this.audioCardList
//    }

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
                // val audioCardModel = child.tag as AudioCardModel
                // return audioCardModel.isDraggable
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
             * @param view
             * @param top  The value of the dragged view is theoretically to be slid to the vertical direction
             * @param dy    The speed of the slide, in px per second.
             * @return  The value of the actual y coordinate in the vertical direction
             */
            override fun clampViewPositionVertical(view: View, top: Int, dy: Int): Int {

                // The minimum y coordinate value cannot be less than topBound
                val topBound = paddingTop
                // The maximum y coordinate value cannot be greater than bottomBound
                val bottomBound = height - view.height - paddingBottom
                val newTop = Math.min(Math.max(top, topBound), bottomBound)
                mCurrentTop = newTop

                return newTop
            }


            /**
             * This method will be invoked when the View is no longer being dragged.
             * go to the center of the screen
             *
             * @param releasedChild
             * @param xvel velocity of x direction
             * @param yvel velocity of y direction
             */
            override fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float) {
                super.onViewReleased(releasedChild, xvel, yvel)

                val centerPoint = width / 2 - releasedChild.width / 2
                viewDragHelper?.settleCapturedViewAt(centerPoint, mCurrentTop)
                invalidate() // System method to refresh view position

                myVerticalPositionDetectListener?.handleViewVerticalPostion(releasedChild)
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


    interface VerticalPositionDetectListener {
        fun handleViewVerticalPostion(view: View)
    }

    private var myVerticalPositionDetectListener: VerticalPositionDetectListener? = null
    fun setMyVerticalPositionDetectListener(myVerticalPositionDetectListener: VerticalPositionDetectListener){
        this.myVerticalPositionDetectListener = myVerticalPositionDetectListener
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

