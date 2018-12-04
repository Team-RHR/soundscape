package com.example.kkgroup.soundscape_v2.activity

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import com.example.kkgroup.soundscape_v2.R
import com.example.kkgroup.soundscape_v2.Tools.PrefManager
import com.example.kkgroup.soundscape_v2.Tools.Tools
import kotlinx.android.synthetic.main.activity_settings.*
import org.jetbrains.anko.startActivity
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
            select_language.text = getString(R.string.english)
        } else {
            select_language.text = getString(R.string.finnish)
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

        // Set an checked change listener for switch button
        modeSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // The switch is enabled/checked
                println("This is theme: " + packageManager.getActivityInfo(componentName, 0).theme)
                 setTheme(R.style.NightMode)
                //recreate()
            } else {
                // The switch is disabled
                println("This is theme: " + packageManager.getActivityInfo(componentName, 0).theme)
                setTheme(R.style.AppTheme)
            }
        }

    }

    private fun initToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar!!.title = getString(R.string.menu_settings)
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
        val arrayOptions = arrayOf(getString(R.string.delete_option_1),getString(R.string.delete_option_2),getString(R.string.delete_option_3))

        // Initialize a boolean array of checked items
        val arrayChecked = booleanArrayOf(false,false,false)

        // Initialize a new instance of alert dialog builder object
        val builder = AlertDialog.Builder(this)

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
                    Tools.toastShow(this, getString(R.string.delete_toast))
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
        lateinit var dialog:AlertDialog
        val array = arrayOf(getString(R.string.finnish),getString(R.string.english))
        val builder = AlertDialog.Builder(this)

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
        val builder = AlertDialog.Builder(this)

        builder.setTitle(getString(R.string.logout_confirmation))

        builder.setPositiveButton(getString(R.string.log_out)){ _, _ ->
            val prefManager = PrefManager(this)
            prefManager.setApiKey(null)
            startActivity<LoginActivity>()
        }

        builder.setNeutralButton(getString(R.string.cancel)){ _, _ ->

        }
        val dialog: AlertDialog = builder.create()
        dialog.show()

    }

    private fun changeLanguage(lang: String) {
        LocaleManager(this).changeLocale(lang)
        this.recreate()
    }

}
