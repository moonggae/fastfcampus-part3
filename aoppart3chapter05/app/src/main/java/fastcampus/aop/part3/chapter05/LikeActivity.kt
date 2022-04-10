package fastcampus.aop.part3.chapter05

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import    android.widget.EditText
import com.google.firebase.database.*
import com.yuyakaido.android.cardstackview.CardStackLayoutManager
import com.yuyakaido.android.cardstackview.CardStackListener
import com.yuyakaido.android.cardstackview.Direction
import fastcampus.aop.part3.chapter05.DBKey.Companion.DISLIKE
import fastcampus.aop.part3.chapter05.DBKey.Companion.LIKE
import fastcampus.aop.part3.chapter05.DBKey.Companion.LIKED_BY
import fastcampus.aop.part3.chapter05.DBKey.Companion.MATCH
import fastcampus.aop.part3.chapter05.DBKey.Companion.NAME
import fastcampus.aop.part3.chapter05.DBKey.Companion.USERS
import fastcampus.aop.part3.chapter05.DBKey.Companion.USER_ID
import fastcampus.aop.part3.chapter05.databinding.ActivityLikeBinding

class LikeActivity : AppCompatActivity(), CardStackListener {

    private val binding: ActivityLikeBinding by lazy {
        ActivityLikeBinding.inflate(layoutInflater)
    }

    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }

    private val userDB: DatabaseReference by lazy {
        Firebase.database.reference.child(USERS)
    }

    private val adapter = CardItemAdapter()
    private val cardItems = mutableListOf<CardItem>()

    private val manager by lazy {
        CardStackLayoutManager(this, this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val currentUserDB = userDB.child(getCurrentUserID())
        currentUserDB.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.child(NAME).value == null) {
                    showNameInputPopup()
                    return
                }

                getUnselectedUserInfo()
                // todo 유저정보 갱신
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

        initCardStackView()
        initSignOutButton()
        initMatchedListButton()
    }


    private fun initCardStackView() {
        binding.cardStackView.layoutManager = manager
        binding.cardStackView.adapter = adapter
    }

    private fun initSignOutButton() {
        binding.signOutButton.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun initMatchedListButton() {
        binding.matchListButton.setOnClickListener {
            startActivity(Intent(this, MatchedUserActivity::class.java))
        }
    }



    private fun getUnselectedUserInfo() {
        userDB.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                if (snapshot.child(USER_ID).value != getCurrentUserID()
                    && snapshot.child(LIKED_BY).child(LIKE).hasChild(getCurrentUserID()).not()
                    && snapshot.child(LIKED_BY).child(DISLIKE).hasChild(getCurrentUserID()).not()
                ) {
                    val userId = snapshot.child(USER_ID).value.toString()
                    var name = "undecided"
                    if (snapshot.child(NAME).value != null) {
                        name = snapshot.child(NAME).value.toString()
                    }

                    cardItems.add(CardItem(userId, name))
                    adapter.submitList(cardItems)
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                cardItems.find { it.userId == snapshot.key }?.let {
                    it.name = snapshot.child(NAME).value.toString()
                }

                adapter.submitList(cardItems)
                adapter.notifyDataSetChanged()
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun showNameInputPopup() {
        val nameEditText = EditText(this)


        AlertDialog.Builder(this)
            .setTitle("이름을 입력해주세요")
            .setView(nameEditText)
            .setPositiveButton("저장") { _, _ ->
                if (nameEditText.text.isNullOrEmpty()) {
                    showNameInputPopup()
                } else {
                    saveUserName(nameEditText.text.toString())
                }
            }
            .setCancelable(false)
            .show()
    }

    private fun saveUserName(name: String) {
        val userId = getCurrentUserID()
        val currentUserDB = userDB.child(userId)
        val user = mutableMapOf<String, Any>()
        user[USER_ID] = userId
        user[NAME] = name
        currentUserDB.updateChildren(user)

        getUnselectedUserInfo()
    }


    private fun getCurrentUserID(): String {
        if (auth.currentUser == null) {
            Toast.makeText(this, "로그인이 되어있지 않습니다.", Toast.LENGTH_SHORT).show()
            finish()
        }

        return auth.currentUser!!.uid
    }

    override fun onCardDragging(direction: Direction?, ratio: Float) {}
    override fun onCardSwiped(direction: Direction?) {
        when (direction) {
            Direction.Right -> like()
            Direction.Left -> disLike()
            else -> {}
        }
    }

    override fun onCardRewound() {}
    override fun onCardCanceled() {}
    override fun onCardAppeared(view: View?, position: Int) {}
    override fun onCardDisappeared(view: View?, position: Int) {}

    private fun like() {
        val card = cardItems[manager.topPosition - 1]
        cardItems.removeFirst()

        userDB.child(card.userId)
            .child(LIKED_BY)
            .child(LIKE)
            .child(getCurrentUserID())
            .setValue(true)

        saveMatchIfOtherUserLikedMe(card.userId)

        // todo 매칭이 된 시점을 봐야한다.

        Toast.makeText(this, "Liked ${card.name}", Toast.LENGTH_SHORT).show()
    }

    private fun disLike() {
        val card = cardItems[manager.topPosition - 1]
        cardItems.removeFirst()

        userDB.child(card.userId)
            .child(LIKED_BY)
            .child(DISLIKE)
            .child(getCurrentUserID())
            .setValue(true)

        Toast.makeText(this, "DisLiked ${card.name}", Toast.LENGTH_SHORT).show()
    }

    private fun saveMatchIfOtherUserLikedMe(otherUserId: String) {
        val otherUserDB =
            userDB.child(getCurrentUserID()).child(LIKED_BY).child(LIKE).child(otherUserId)
        otherUserDB.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.value == true) {
                    userDB.child(getCurrentUserID())
                        .child(LIKED_BY)
                        .child(MATCH)
                        .child(otherUserId)
                        .setValue(true)

                    userDB.child(otherUserId)
                        .child(LIKED_BY)
                        .child(MATCH)
                        .child(getCurrentUserID())
                        .setValue(true)
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

}