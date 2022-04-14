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

                binding.signInOutButton.text = "로그아웃"
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
                        Toast.makeText(context, "로그인 실패, 이메일 또는 비밀번호를 확인 해주세요.", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
        }
    }

    private fun signUp(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()){ task ->
                if(task.isSuccessful){
                    Toast.makeText(context, "회원가입 성공. 로그인 버튼을 눌러주세요.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "회원가입 실패. 이미 가입한 이메일일 수 있습니다.", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener(requireActivity()){ _ ->
                Toast.makeText(context, "회원가입 실패. 이메일 또는 비밀번호를 확인 해주세요.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun successSignIn() {
        if (auth.currentUser == null) {
            Toast.makeText(context, "로그인 실패, 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        binding?.let {
            binding.emailEditText.isEnabled = false
            binding.passwordEditText.isEnabled = false
            binding.signUpButton.isEnabled = false
            binding.signInOutButton.text = "로그아웃"
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

            binding.signInOutButton.text = "로그인"
            binding.signInOutButton.isEnabled = false
            binding.signUpButton.isEnabled = false
        }
    }
}