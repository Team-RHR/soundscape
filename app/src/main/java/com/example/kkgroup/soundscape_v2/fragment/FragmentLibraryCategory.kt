package com.example.kkgroup.soundscape_v2.fragment

import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.Toast
import com.example.kkgroup.soundscape_v2.Model.LibraryCategoryModel
import com.example.kkgroup.soundscape_v2.R
import com.example.kkgroup.soundscape_v2.Tools.Tools
import com.example.kkgroup.soundscape_v2.adapter.LibraryCategoryAdapter
import com.example.kkgroup.soundscape_v2.widget.SpacingItemDecoration
import java.util.*

class FragmentLibraryCategory : Fragment() {

    private val LOADING_DURATION = 500
    private lateinit var recyclerView: RecyclerView
    private lateinit var mAudioCategoryAdapter: LibraryCategoryAdapter

    companion object {
        fun newInstance(): FragmentLibraryCategory {
            return FragmentLibraryCategory()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_library_category, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView = view.findViewById(R.id.recyclerView)
        loadingAndDisplayContent()
    }

    private fun loadingAndDisplayContent() {
        val lytProgress = view!!.findViewById<LinearLayout>(R.id.lyt_progress)
        lytProgress.visibility = View.VISIBLE
        lytProgress.alpha = 1.0f
        recyclerView.visibility = View.GONE

        Handler().postDelayed({ Tools.viewFadeOut(lytProgress) }, LOADING_DURATION.toLong())
        Handler().postDelayed({ initComponents() }, (LOADING_DURATION + 400).toLong())
    }

    private fun initComponents() {

        recyclerView.visibility = View.VISIBLE
        recyclerView.layoutManager = GridLayoutManager(context, 2, GridLayout.VERTICAL, false)
        recyclerView.addItemDecoration(SpacingItemDecoration(2, Tools.dpToPx(context!!, 4), true))
        recyclerView.setHasFixedSize(true)

        // TODO Replace with real category list
        val items = ArrayList<LibraryCategoryModel>()
        items.add(LibraryCategoryModel("Human", R.drawable.human, R.color.blue_400))
        items.add(LibraryCategoryModel("Machine", R.drawable.machine, R.color.pink_400))
        items.add(LibraryCategoryModel("Nature", R.drawable.nature, R.color.teal_400))
        items.add(LibraryCategoryModel("My Recordings", R.drawable.myrecording, R.color.orange_400))
        // items.shuffle()

        context?.let {
            mAudioCategoryAdapter = LibraryCategoryAdapter(it, items)
            recyclerView.adapter = mAudioCategoryAdapter
            mAudioCategoryAdapter.notifyDataSetChanged()
            initListeners()
        }
    }

    private fun initListeners() {
        mAudioCategoryAdapter.setOnItemClickListener(object : LibraryCategoryAdapter.OnItemClickListener {
            override fun onItemClick(view: View, obj: LibraryCategoryModel, position: Int) {
                Toast.makeText(activity, "Item ${obj.category} clicked", Toast.LENGTH_SHORT).show()

                // pass value to FragmentLibraryAudioList fragment
                val bundle = Bundle()
                bundle.putString("obj", obj.category)
                val trans = fragmentManager!!.beginTransaction()
                val dest = FragmentLibraryAudioList.newInstance()
                dest.arguments = bundle

                // We use the "root frame" defined in "root_fragment.xml" as the reference to replace fragment
                trans.replace(R.id.root_frame, dest)

                // The following lines allow us to add the fragment to the stack and return to it later, by pressing back
                trans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                trans.addToBackStack(null)
                trans.commit()
            }
        })
    }

}