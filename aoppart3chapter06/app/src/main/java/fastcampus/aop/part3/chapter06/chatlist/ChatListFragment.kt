package fastcampus.aop.part3.chapter06.chatlist

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import fastcampus.aop.part3.chapter06.DBKey
import fastcampus.aop.part3.chapter06.DBKey.Companion.CHILD_CHAT
import fastcampus.aop.part3.chapter06.DBKey.Companion.DB_USERS
import fastcampus.aop.part3.chapter06.R
import fastcampus.aop.part3.chapter06.chatdetail.ChatDetailActivity
import fastcampus.aop.part3.chapter06.databinding.FragmentChatlistBinding

class ChatListFragment : Fragment(R.layout.fragment_chatlist) {
    private lateinit var binding: FragmentChatlistBinding

    private lateinit var chatListAdapter: ChatListAdapter
    private val chatRoomList = mutableListOf<ChatListItem>()


    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentChatlistBinding.bind(view)

        if(auth.currentUser == null){
            return
        }

        chatListAdapter = ChatListAdapter { chatRoom ->
            context?.let {
                val intent = Intent(it, ChatDetailActivity::class.java)
                intent.putExtra("chatKey", chatRoom.key)
                startActivity(intent)
            }

        }

        chatRoomList.clear()

        binding.charListRecyclerView.adapter = chatListAdapter
        binding.charListRecyclerView.layoutManager = LinearLayoutManager(context)

        val chatDB = Firebase.database.reference.child(DB_USERS).child(auth.currentUser!!.uid).child(CHILD_CHAT)

        chatDB.addListenerForSingleValueEvent(object  : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {
                    val model = it.getValue(ChatListItem::class.java)
                    model ?: return

                    chatRoomList.add(model)
                }
                chatListAdapter.submitList(chatRoomList)
                chatListAdapter.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    override fun onResume() {
        super.onResume()

        chatListAdapter.notifyDataSetChanged()
    }
}