package fastcampus.aop.part3.chapter05

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import 	android.widget.EditText
import fastcampus.aop.part3.chapter05.databinding.ActivityLikeBinding

class LikeActivity : AppCompatActivity() {

    private val binding : ActivityLikeBinding by lazy {
        ActivityLikeBinding.inflate(layoutInflater)
    }

    private val auth : FirebaseAuth by lazy {
        Firebase.auth
    }

    private val userDB : DatabaseReference by lazy {
        Firebase.database.reference.child("Users")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

         val currentUserDB = userDB.child(getCurrentUserID())
        currentUserDB.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.child("name").value == null){
                    showNameInputPopup()
                    return
                }

                // todo 유저정보 갱신
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }

    private fun showNameInputPopup() {
        val nameEditText = EditText(this)


        AlertDialog.Builder(this)
            .setTitle("이름을 입력해주세요")
            .setView(nameEditText)
            .setPositiveButton("저장") { _, _ ->
                if(nameEditText.text.isNullOrEmpty()){
                    showNameInputPopup()
                } else {
                    saveUserName(nameEditText.text.toString())
                }
            }
            .setCancelable(false)
            .show()
    }

    private fun saveUserName(name : String) {
        val userId = getCurrentUserID()
        val currentUserDB = userDB.child(userId)
        val user = mutableMapOf<String, Any>()
        user["userId"] = userId
        user["name"] = name
        currentUserDB.updateChildren(user)
    }

    private fun getCurrentUserID() : String{
        if(auth.currentUser == null){
            Toast.makeText(this, "로그인이 되어있지 않습니다.", Toast.LENGTH_SHORT).show()
            finish()
        }

        return auth.currentUser!!.uid
    }
}