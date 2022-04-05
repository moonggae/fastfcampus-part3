package fastcampus.aop.part3.chapter05

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import fastcampus.aop.part3.chapter05.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }

    private val binding: ActivityLoginBinding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }

    private val callbackManager: CallbackManager by lazy {
        CallbackManager.Factory.create()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initLoginButton()
        initSignUpButton()
        initEmailAndPasswordEditText()
        initFacebookLoginButton()
    }


    private fun initLoginButton() {
        binding.loginButton.setOnClickListener {
            val email = getInputEmail()
            val password = getInputPassword()

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        finish()
                    } else {
                        Toast.makeText(
                            this,
                            "로그인에 실패했습니다. 이메일 또는 비밀번호를 확인해주세요.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }

    private fun initSignUpButton() {
        binding.signUpButton.setOnClickListener {
            val email = getInputEmail()
            val password = getInputPassword()

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            this,
                            "회원강비에 성공했습니다. 로그인 버튼을 눌러 로그인해주세요.",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(this, "이미 가입한 이메일이거나, 회원가입에 실패했습니다.", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
        }
    }

    private fun initEmailAndPasswordEditText() {
        binding.emailEditText.addTextChangedListener {
            val enable =
                binding.emailEditText.text.isNotEmpty() && binding.passwordEditText.text.isNotEmpty()
            binding.loginButton.isEnabled = enable
            binding.signUpButton.isEnabled = enable
        }
        binding.passwordEditText.addTextChangedListener {
            val enable =
                binding.emailEditText.text.isNotEmpty() && binding.passwordEditText.text.isNotEmpty()
            binding.loginButton.isEnabled = enable
            binding.signUpButton.isEnabled = enable
        }
    }

    private fun initFacebookLoginButton() {
        binding.facebookLoginButton.setPermissions("email", "public_profile")
        binding.facebookLoginButton.registerCallback(
            callbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onCancel() {}

                override fun onError(error: FacebookException) {
                    Toast.makeText(this@LoginActivity, "페이스북 로그인 실패", Toast.LENGTH_SHORT).show()
                }

                override fun onSuccess(result: LoginResult) {
                    val credential = FacebookAuthProvider.getCredential(result.accessToken.token)
                    auth.signInWithCredential(credential)
                        .addOnCompleteListener(this@LoginActivity) { task ->
                            if(task.isSuccessful){
                                finish()
                            } else {
                                Toast.makeText(this@LoginActivity, "페이스북 로그인 실패", Toast.LENGTH_SHORT).show()
                            }
                        }
                }

            })
    }


    private fun getInputEmail() = binding.emailEditText.text.toString()
    private fun getInputPassword() = binding.passwordEditText.text.toString()


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

}