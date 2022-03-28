package fastcampus.aop.part3.chapter01

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import fastcampus.aop.part3.chapter01.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val TAG = "로그"

    private val binding : ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initFirebase()
    }

    private fun initFirebase(){

//        val firebaseApp = FirebaseApp.initializeApp(this)

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if(task.isSuccessful){
                val token = task.result
                binding.firebaseTokenTextView.text = token
                Log.d(TAG, "MainActivity - initFirebase - token : $token")
            }
        }
    }
}