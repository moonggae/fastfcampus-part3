package fastcampus.aop.part3.chapter06

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import fastcampus.aop.part3.chapter06.chatlist.ChatListFragment
import fastcampus.aop.part3.chapter06.databinding.ActivityMainBinding
import fastcampus.aop.part3.chapter06.home.HomeFragment
import fastcampus.aop.part3.chapter06.mypage.MyPageFragment

class MainActivity : AppCompatActivity() {

    private val binding : ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val homeFragment = HomeFragment()
    private val charListFragment = ChatListFragment()
    private val myPageFragment = MyPageFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        replaceFragment(homeFragment)
        initBottomNavigationView()

    }

    private fun initBottomNavigationView() {
        binding.bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when(menuItem.itemId){
                R.id.home -> replaceFragment(homeFragment)
                R.id.chatList-> replaceFragment(charListFragment)
                R.id.myPage ->replaceFragment(myPageFragment)
            }
            true
        }
    }

    private fun replaceFragment(fragment : Fragment){
        supportFragmentManager.beginTransaction()
            .apply {
                replace(binding.fragmentContainer.id, fragment)
                commit()
            }
    }

}

