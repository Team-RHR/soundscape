package com.example.kkgroup.soundscape_v2.activity

import android.app.Dialog
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Window
import android.widget.Toast
import com.example.kkgroup.soundscape_v2.R
import com.example.kkgroup.soundscape_v2.Tools.PrefManager
import com.example.kkgroup.soundscape_v2.Tools.Tools
import kotlinx.android.synthetic.main.activity_settings.*
import org.jetbrains.anko.startActivity
import android.widget.RadioButton
import android.widget.RadioGroup
import android.view.Window.FEATURE_NO_TITLE
import com.example.kkgroup.soundscape_v2.Tools.LocaleManager


class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        initToolbar()
        initListeners()
        setStates()

    }

    //Set current state of settings to be displayed
    private fun setStates() {
        if (PrefManager(this).getLocale() == "us") {
           // LocaleManager(this).changeLocale("fi")
           // this.recreate()
            select_language.text = "English"
        } else {
          //  LocaleManager(this).changeLocale("us")
          // this.recreate()
            select_language.text = "Finnish"
        }
    }

    private fun initListeners() {
        //Logout button listener
        logout_button.setOnClickListener {
            showLogOutDialog()
        }

        //Language button listener
        select_language.setOnClickListener {
            showLanguageDialog()
        }
        //Delete files listener
        delete_soundscapes.setOnClickListener {
            showDeleteDialog()
        }
    }

    private fun initToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar!!.title = "Settings"
        Tools.setSystemBarColor(this, R.color.colorPrimary)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

    }

    // Method to show an alert dialog with multiple choice list items
    private fun showDeleteDialog(){
        // Late initialize an alert dialog object
        lateinit var dialog:AlertDialog

        // Initialize an array of options
        val arrayOptions = arrayOf("Soundscapes","Downloaded audio files","Your own recordings")

        // Initialize a boolean array of checked items
        val arrayChecked = booleanArrayOf(false,false,false)

        // Initialize a new instance of alert dialog builder object
        val builder = AlertDialog.Builder(this)

        // Set a title for alert dialog
        builder.setTitle("Choose which files you want to delete: ")

        // Define multiple choice items for alert dialog
        builder.setMultiChoiceItems(arrayOptions, arrayChecked) { dialog, which, isChecked->
            // Update the clicked item checked status
            arrayChecked[which] = isChecked
        }

        builder.setPositiveButton("Ok"){dialog, which ->
            Tools.toastShow(this, "All files deleted")
            for (i in 0 until arrayOptions.size) {
                val checked = arrayChecked[i]
                if (checked) {
                    when (arrayOptions[i]){
                        "Soundscapes" -> { Tools.deleteSoundScapes() }

                        "Downloaded audio files" -> { Tools.deleteAudios() }

                        "Your own recordings" -> { Tools.deleteRecordings() }
                    }
                }
            }

        }

        builder.setNeutralButton("Cancel"){_,_ ->
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
        lateinit var dialog:AlertDialog
        val array = arrayOf("Finnish","English")
        val builder = AlertDialog.Builder(this)

        builder.setTitle("Choose a language: ")

        // Set the single choice items for alert dialog with initial selection
        builder.setSingleChoiceItems(array,-1) { _, which->
            // Get the dialog selected item
            language = array[which]
        }

        builder.setPositiveButton("Ok"){dialog, which ->
            if (language == "English") changeLanguage("us") else changeLanguage("fi")
            Tools.toastShow(this, "Language set to $language")

        }

        builder.setNeutralButton("Cancel"){_,_ ->
              dialog.dismiss()
        }

        dialog = builder.create()
        dialog.show()
    }

    private fun showLogOutDialog() {
        val builder = AlertDialog.Builder(this)

        // Set the alert dialog title
        builder.setTitle("Are you sure you want to log out?")

        // Set a positive button and its click listener on alert dialog
        builder.setPositiveButton("Log out"){dialog, which ->
            // Do something when user press the positive button
            val prefManager = PrefManager(this)
            prefManager.setApiKey(null)
            startActivity<LoginActivity>()
        }

        // Display a neutral button on alert dialog
        builder.setNeutralButton("Cancel"){dialog, which ->
            //Do something here when cancel button is pressed
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()

    }

    private fun changeLanguage(lang: String) {
        LocaleManager(this).changeLocale(lang)
        this.recreate()
    }

}
