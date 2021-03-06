package fastcampus.aop.part3.chapter06.mypage

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import fastcampus.aop.part3.chapter06.R
import fastcampus.aop.part3.chapter06.databinding.FragmentMypageBinding

class MyPageFragment : Fragment(R.layout.fragment_mypage) {

    private lateinit var binding: FragmentMypageBinding
    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMypageBinding.bind(view)


        initViews()

    }

    override fun onStart() {
        super.onStart()

        if (auth.currentUser == null) {
            signOutViewInit()
        } else {
            binding?.let {
                binding.emailEditText.setText(auth.currentUser!!.email.orEmpty())
                binding.emailEditText.isEnabled = false
                binding.passwordEditText.setText("*************")
                binding.passwordEditText.isEnabled = false

                binding.signInOutButton.text = "๋ก๊ทธ์์"
                binding.signInOutButton.isEnabled = true
                binding.signUpButton.isEnabled = false
            }
        }
    }

    private fun initViews() {
        binding.signInOutButton.setOnClickListener {
            binding?.let { binding ->
                val email = binding.emailEditText.text.toString()
                val password = binding.passwordEditText.text.toString()

                if (auth.currentUser == null) {
                    signIn(email, password)

                } else {
                    signOut()
                }
            }
        }

        binding.signUpButton.setOnClickListener {
            binding?.let { binding ->
                val email = binding.emailEditText.text.toString()
                val password = binding.passwordEditText.text.toString()

                signUp(email, password)

            }
        }

        binding.emailEditText.addTextChangedListener {
            binding?.let { binding ->
                val enable =
                    binding.emailEditText.text.isNotEmpty() && binding.passwordEditText.text.isNotEmpty()
                binding.signUpButton.isEnabled = enable
                binding.signInOutButton.isEnabled = enable
            }
        }

        binding.passwordEditText.addTextChangedListener {
            binding?.let { binding ->
                val enable =
                    binding.emailEditText.text.isNotEmpty() && binding.passwordEditText.text.isNotEmpty()
                binding.signUpButton.isEnabled = enable
                binding.signInOutButton.isEnabled = enable
            }
        }
    }

    private fun signIn(email: String, password: String) {
        activity?.let {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        successSignIn()
                    } else {
                        Toast.makeText(context, "๋ก๊ทธ์ธ ์คํจ, ์ด๋ฉ์ผ ๋๋ ๋น๋ฐ๋ฒํธ๋ฅผ ํ์ธ ํด์ฃผ์ธ์.", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
        }
    }

    private fun signUp(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()){ task ->
                if(task.isSuccessful){
                    Toast.makeText(context, "ํ์๊ฐ์ ์ฑ๊ณต. ๋ก๊ทธ์ธ ๋ฒํผ์ ๋๋ฌ์ฃผ์ธ์.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "ํ์๊ฐ์ ์คํจ. ์ด๋ฏธ ๊ฐ์ํ ์ด๋ฉ์ผ์ผ ์ ์์ต๋๋ค.", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener(requireActivity()){ _ ->
                Toast.makeText(context, "ํ์๊ฐ์ ์คํจ. ์ด๋ฉ์ผ ๋๋ ๋น๋ฐ๋ฒํธ๋ฅผ ํ์ธ ํด์ฃผ์ธ์.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun successSignIn() {
        if (auth.currentUser == null) {
            Toast.makeText(context, "๋ก๊ทธ์ธ ์คํจ, ๋ค์ ์๋ํด์ฃผ์ธ์.", Toast.LENGTH_SHORT).show()
            return
        }

        binding?.let {
            binding.emailEditText.isEnabled = false
            binding.passwordEditText.isEnabled = false
            binding.signUpButton.isEnabled = false
            binding.signInOutButton.text = "๋ก๊ทธ์์"
        }
    }

    private fun signOut() {
        auth.currentUser?.let {
            auth.signOut()
        }
        signOutViewInit()
    }

    private fun signOutViewInit() {
        binding?.let {
            binding.emailEditText.text.clear()
            binding.emailEditText.isEnabled = true
            binding.passwordEditText.text.clear()
            binding.passwordEditText.isEnabled = true

            binding.signInOutButton.text = "๋ก๊ทธ์ธ"
            binding.signInOutButton.isEnabled = false
            binding.signUpButton.isEnabled = false
        }
    }
}