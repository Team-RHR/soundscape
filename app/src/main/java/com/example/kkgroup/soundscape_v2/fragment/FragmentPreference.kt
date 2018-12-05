package com.example.kkgroup.soundscape_v2.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.SwitchCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.kkgroup.soundscape_v2.R
import com.example.kkgroup.soundscape_v2.Tools.LocaleManager
import com.example.kkgroup.soundscape_v2.Tools.PrefManager
import com.example.kkgroup.soundscape_v2.Tools.Tools
import com.example.kkgroup.soundscape_v2.activity.LoginActivity
import org.jetbrains.anko.support.v4.startActivity

class FragmentPreference : Fragment() {

    private lateinit var selectLanguage: TextView
    private lateinit var logout: TextView
    private lateinit var delete: TextView
    private lateinit var modeSwitch: SwitchCompat

    companion object {
        fun newInstance(): FragmentPreference {
            return FragmentPreference()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_preference, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initComponents(view)
        initListeners()
        setStates()
    }

    private fun initComponents(view: View) {

        selectLanguage = view.findViewById(R.id.select_language)
        logout = view.findViewById(R.id.logout_button)
        delete = view.findViewById(R.id.delete_soundscapes)
        modeSwitch = view.findViewById(R.id.modeSwitch)
    }

    //Set current state of settings to be displayed
    private fun setStates() {
        if (PrefManager(context!!).getLocale() == "us") {
            selectLanguage.text = getString(R.string.english)
        } else {
            selectLanguage.text = getString(R.string.finnish)
        }
    }

    private fun initListeners() {
        //Logout button listener
        logout.setOnClickListener {
            showLogOutDialog()
        }

        //Language button listener
        selectLanguage.setOnClickListener {
            showLanguageDialog()
        }
        //Delete files listener
        delete.setOnClickListener {
            showDeleteDialog()
        }

        // Set an checked change listener for switch button
        modeSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // The switch is enabled/checked
                println("This is theme: " + activity?.packageManager?.getActivityInfo(activity?.componentName, 0)?.theme)
                activity?.setTheme(R.style.NightMode)
                //recreate()
            } else {
                // The switch is disabled
                println("This is theme: " + activity?.packageManager?.getActivityInfo(activity?.componentName, 0)?.theme)
                activity?.setTheme(R.style.AppTheme)
            }
        }

    }

    // Method to show an alert dialog with multiple choice list items
    private fun showDeleteDialog(){
        // Late initialize an alert dialog object
        lateinit var dialog: AlertDialog

        // Initialize an array of options
        val arrayOptions = arrayOf(getString(R.string.delete_option_1),getString(R.string.delete_option_2),getString(R.string.delete_option_3))

        // Initialize a boolean array of checked items
        val arrayChecked = booleanArrayOf(false,false,false)

        // Initialize a new instance of alert dialog builder object
        val builder = AlertDialog.Builder(context!!)

        // Set a title for alert dialog
        builder.setTitle(getString(R.string.delete_title))

        // Define multiple choice items for alert dialog
        builder.setMultiChoiceItems(arrayOptions, arrayChecked) { _, which, isChecked->
            // Update the clicked item checked status
            arrayChecked[which] = isChecked
        }

        builder.setPositiveButton("Ok"){ _, _ ->
            for (i in 0 until arrayOptions.size) {
                val checked = arrayChecked[i]
                if (checked) {
                    when (arrayOptions[i]){
                        getString(R.string.delete_option_1) -> { Tools.deleteSoundScapes() }

                        getString(R.string.delete_option_2) -> { Tools.deleteAudios() }

                        getString(R.string.delete_option_3) -> { Tools.deleteRecordings() }
                    }
                    Tools.toastShow(context!!, getString(R.string.delete_toast))
                }
            }

        }

        builder.setNeutralButton(getString(R.string.cancel)){_,_ ->
            // Dismiss the dialog
            dialog.dismiss()
        }

        // Initialize the AlertDialog using builder object
        dialog = builder.create()

        // Finally, display the alert dialog
        dialog.show()
    }

    // Method to show an alert dialog with single choice list items
    private fun showLanguageDialog(){

        var language = ""
        lateinit var dialog: AlertDialog
        val array = arrayOf(getString(R.string.finnish),getString(R.string.english))
        val builder = AlertDialog.Builder(context!!)

        builder.setTitle(getString(R.string.choose_language))

        // Set the single choice items for alert dialog with initial selection
        builder.setSingleChoiceItems(array,-1) { _, which->
            // Get the dialog selected item
            language = array[which]
        }

        builder.setPositiveButton("Ok"){ _, _ ->
            if (language == getString(R.string.english)) changeLanguage("us") else changeLanguage("fi")
        }

        builder.setNeutralButton(getString(R.string.cancel)){_,_ ->
            dialog.dismiss()

        }

        dialog = builder.create()
        dialog.show()
    }

    private fun showLogOutDialog() {
        val builder = AlertDialog.Builder(context!!)

        builder.setTitle(getString(R.string.logout_confirmation))

        builder.setPositiveButton(getString(R.string.log_out)){ _, _ ->
            val prefManager = PrefManager(context!!)
            prefManager.setApiKey(null)
            startActivity<LoginActivity>()
        }

        builder.setNeutralButton(getString(R.string.cancel)){ _, _ ->

        }
        val dialog: AlertDialog = builder.create()
        dialog.show()

    }

    private fun changeLanguage(lang: String) {
        LocaleManager(context!!).changeLocale(lang)
        activity?.recreate()
    }

}