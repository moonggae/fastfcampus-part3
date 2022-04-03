package fastcampus.aop.part3.chapter04

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.bumptech.glide.Glide
import fastcampus.aop.part3.chapter04.databinding.ActivityDetailBinding
import fastcampus.aop.part3.chapter04.model.Book
import fastcampus.aop.part3.chapter04.model.Review

class DetailActivity : AppCompatActivity() {

    private val binding: ActivityDetailBinding by lazy {
        ActivityDetailBinding.inflate(layoutInflater)
    }

    private val db: AppDatabase by lazy {
        getAppDatabase(this)
    }

    private val bookModel: Book? by lazy {
        intent.getParcelableExtra<Book>("bookModel")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initViews()
        initReviewSaveButton()
    }

    private fun initViews() {

        binding.titleTextView.text = bookModel?.title.orEmpty()
        binding.descriptionTextView.text = bookModel?.description.orEmpty()

        Glide.with(binding.coverImageView.context)
            .load(bookModel?.coverSmallUrl.orEmpty())
            .into(binding.coverImageView)

        getReview()
    }

    private fun getReview() {
        Thread{
            val review = db.reviewDao().getOneReview(bookModel?.id?.toInt() ?: 0)
            runOnUiThread {
                binding.reviewEditText.setText(review?.review.orEmpty())
            }
        }.start()
    }

    private fun initReviewSaveButton() {
        binding.saveButton.setOnClickListener {
            Thread {
                db.reviewDao().saveReview(
                    Review(
                        bookModel?.id?.toInt() ?: 0,
                        binding.reviewEditText.text.toString()
                    )
                )
                runOnUiThread {
                    Toast.makeText(this, "리뷰가 저장되었습니다.", Toast.LENGTH_SHORT).show()
                }
            }.start()
        }
    }

}