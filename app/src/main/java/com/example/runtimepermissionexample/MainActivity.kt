package com.example.runtimepermissionexample

import android.app.Instrumentation.ActivityResult
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat

class MainActivity : AppCompatActivity() {
    //The MainActivity class extends AppCompatActivity and overrides onCreate
    private lateinit var tvStatus : TextView

    //It initializes tvStatus and defines an array of permissions (PERMISSIONS) that include CAMERA and RECORD_AUDIO.
    private val PERMISSIONS = arrayOf(android.Manifest.permission.CAMERA,android.Manifest.permission.RECORD_AUDIO)


    //The onCreate method sets the content view to activity_main.xml,
    // initializes tvStatus, and sets an OnClickListener on a button to
    // trigger the permission checks.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tvStatus = findViewById(R.id.tv_status)
        findViewById<Button>(R.id.btn_permission).setOnClickListener{
            checkPermissionRequired()
        }
    }

    //The permissionRequestLauncher is set up to handle the result of the permission request.
    // If all permissions are granted,
    // it updates the status text.
    // If not, it shows a dialog prompting the user to go to app
    // settings to grant the necessary permissions.
    private val permissionRequestLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions->
            val isGranted = permissions.entries.all { it.value}
            if (isGranted) {
                setStatus("All permission granted")
            }
            else{
                //show dialog to show permission
                //should go to setting of app to enable permission
                showPermissionDialog()
            }
        }

    //checkPermissionRequired checks if permissions are already granted.
    // If so, it updates the status. If not, it triggers the permission request.
    private fun checkPermissionRequired() {
            if(hasPermission()) {
                setStatus("All permission granted")
            }
            else{
                setStatus("Permission not allow")
                permissionRequestLauncher.launch(PERMISSIONS)
            }
    }
//hasPermission checks if all permissions in the PERMISSIONS array are granted.
    private fun hasPermission(): Boolean = PERMISSIONS.all{
        ActivityCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
    }

    //showPermissionDialog creates an AlertDialog asking the user to grant permissions.
    // If the user clicks "Grant", it opens the app's settings page.
    private fun showPermissionDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Permission Required")
        builder.setMessage("Some Permissions are needed to be allowed to use this feature")
        builder.setPositiveButton("Grant") {d, _->
            d.cancel()
            startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                val uri = Uri.fromParts("package", this@MainActivity.packageName, null)
                intent.data = uri
            })
        }
        builder.setNegativeButton("Cancel") {d,_->
            d.dismiss()
        }
        builder.show()
    }

    private fun setStatus(text:String){
        tvStatus.text = text
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

}