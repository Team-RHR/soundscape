package com.example.kkgroup.soundscape_v2.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.kkgroup.soundscape_v2.R

class FragmentRoot : Fragment() {

    companion object {
        fun newInstance(): FragmentRoot {
            return FragmentRoot()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_root, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

		 // When this container fragment is created, we fill it with our first "real" fragment
        val transaction = fragmentManager!!.beginTransaction()
        transaction.replace(R.id.root_frame, FragmentLibraryCategory.newInstance())
        transaction.commit()
    }
}
