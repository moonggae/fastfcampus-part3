package fastcampus.aop.part3.chapter06

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import fastcampus.aop.part3.chapter06.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val binding : ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }
}