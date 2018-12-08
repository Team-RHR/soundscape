package com.example.kkgroup.soundscape_v2.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.kkgroup.soundscape_v2.R
import kotlinx.android.synthetic.main.fragment_library_category.*

class FragmentLibraryCategory : Fragment() {

    companion object {
        fun newInstance(): FragmentLibraryCategory {
            return FragmentLibraryCategory()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_library_category, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initListeners()
    }

    private fun initListeners() {

        card_human.setOnClickListener { changeFragment("Human") }
        card_machine.setOnClickListener { changeFragment("Machine") }
        card_nature.setOnClickListener { changeFragment("Nature") }
        card_my_recordings.setOnClickListener { changeFragment("My Recordings") }

    }

    private fun changeFragment(categoryName: String){
        val bundle = Bundle()
        bundle.putString("obj", categoryName)
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

}