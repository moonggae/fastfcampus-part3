package fastcampus.aop.part3.chapter01

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import fastcampus.aop.part3.chapter01.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val TAG = "로그"

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initFirebase()
        updateResult()
    }

    // Intent.FLAG_ACTIVITY_SINGLE_TOP 플래그로 액티비티가 호출됐을 때 실행되는 함수
    // https://developer.android.com/guide/components/activities/tasks-and-back-stack?hl=ko#ManagingTasks
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        setIntent(intent)
        updateResult(true)
    }

    private fun initFirebase() {

//        val firebaseApp = FirebaseApp.initializeApp(this)

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                binding.firebaseTokenTextView.text = token
                Log.d(TAG, "MainActivity - initFirebase - token : $token")
            }
        }
    }

    private fun updateResult(isNewIntent: Boolean = false) {
        binding.resultTextView.text = (intent.getStringExtra("notificationType") ?: "앱 런쳐") +
                if (isNewIntent) {
                    "(으)로 갱신했습니다."
                } else {
                    "(으)로 실행했습니다."
                }
    }
}