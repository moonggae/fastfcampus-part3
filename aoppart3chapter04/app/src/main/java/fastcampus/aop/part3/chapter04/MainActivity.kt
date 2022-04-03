package fastcampus.aop.part3.chapter04

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import fastcampus.aop.part3.chapter04.adapter.BookAdapter
import fastcampus.aop.part3.chapter04.adapter.HistoryAdapter
import fastcampus.aop.part3.chapter04.api.BookService
import fastcampus.aop.part3.chapter04.databinding.ActivityMainBinding
import fastcampus.aop.part3.chapter04.model.BestSellerDto
import fastcampus.aop.part3.chapter04.model.History
import fastcampus.aop.part3.chapter04.model.SearchBookDto
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private val TAG = "로그"

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val adapter: BookAdapter by lazy {
        BookAdapter(itemClickedListener = {
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra("bookModel", it)
            startActivity(intent)
        })
    }

    private val historyAdapter by lazy {
        HistoryAdapter(historyDeleteClickedListener = {
            deleteSearchKeyword(it)
        })
    }

    private val bookService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://book.interpark.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(BookService::class.java)
    }

    private val db: AppDatabase by lazy {
        getAppDatabase(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initBookRecyclerView()
        initHistoryRecyclerView()

        bookService.getBestSellerBooks(apiKey = API_KEY)
            .enqueue(object : Callback<BestSellerDto> {
                override fun onResponse(
                    call: Call<BestSellerDto>,
                    response: Response<BestSellerDto>
                ) {
                    if (response.isSuccessful.not()) return

                    adapter.submitList(response.body()?.books.orEmpty())
                }

                override fun onFailure(call: Call<BestSellerDto>, t: Throwable) {
                    // todo 실패처리
                }
            })

        initSearchEditText()

    }

    private fun initSearchEditText() {
        binding.searchEditText.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == MotionEvent.ACTION_DOWN) {
                search(binding.searchEditText.text.toString())
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }

        binding.searchEditText.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                showHistoryView()
            }
            return@setOnTouchListener false
        }
    }

    private fun search(keyword: String) {

        Log.d(TAG, "MainActivity - search: called")

        bookService.getBooksByName(API_KEY, keyword).enqueue(object : Callback<SearchBookDto> {
            override fun onResponse(
                call: Call<SearchBookDto>,
                response: Response<SearchBookDto>
            ) {

                hideHistoryView()
                saveSearchKeyword(keyword)

                if (response.isSuccessful.not()) {
                    return
                }

                adapter.submitList(response.body()?.books.orEmpty())
            }

            override fun onFailure(call: Call<SearchBookDto>, t: Throwable) {
                hideHistoryView()
            }
        })
    }

    private fun initBookRecyclerView() {
        binding.bookRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.bookRecyclerView.adapter = adapter
    }

    private fun initHistoryRecyclerView() {
        binding.historyRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.historyRecyclerView.adapter = historyAdapter
    }

    private fun showHistoryView() {
        Thread {
            val keywords = db.historyDao().getAll().reversed()
            runOnUiThread {
                binding.historyRecyclerView.visibility = View.VISIBLE
                historyAdapter.submitList(keywords.orEmpty())
            }
        }.start()
    }

    private fun hideHistoryView() {
        binding.historyRecyclerView.visibility = View.GONE
    }

    private fun saveSearchKeyword(keyword: String) {
        Thread {
            db.historyDao().insertHistory(History(null, keyword))
        }.start()
    }

    private fun deleteSearchKeyword(keyword: String) {
        Thread {
            db.historyDao().delete(keyword)
            showHistoryView()
        }.start()
    }

    companion object {
        private const val API_KEY =
            "42790A3B89235820AED5B3BDF3BC7FE576E1313C9910C015C56929AC496E8D5E"
    }
}
