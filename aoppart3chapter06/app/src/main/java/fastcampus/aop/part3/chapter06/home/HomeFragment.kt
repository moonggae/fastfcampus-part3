package fastcampus.aop.part3.chapter06.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import fastcampus.aop.part3.chapter06.R
import fastcampus.aop.part3.chapter06.databinding.FragmentHomeBinding

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var binding : FragmentHomeBinding
    private lateinit var articleAdapter : ArticleAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeBinding.bind(view)

        articleAdapter = ArticleAdapter()
        articleAdapter.submitList(mutableListOf<ArticleModel>().apply {
            add(ArticleModel("0", "맛있는 바나나", 1000000000, "4000원", "https://w.namu.la/s/2f1863d4178e26d11de5f3ab442e987a915546232569588b9b899a83a0718374b478e651ebec02bb53291b72b89ffab03aa337a5591bfedeb96bc89d682a32334250a1fc503b35149217103b5ee98a77f98ce22b3f1c44ed053d27c907f88812cb3ffa08c9ba2d5073d49885f09aed93"))
            add(ArticleModel("0", "바나나는 원래 하얗다", 1000000000, "4000원", "https://w.namu.la/s/2f1863d4178e26d11de5f3ab442e987a915546232569588b9b899a83a0718374b478e651ebec02bb53291b72b89ffab03aa337a5591bfedeb96bc89d682a32334250a1fc503b35149217103b5ee98a77f98ce22b3f1c44ed053d27c907f88812cb3ffa08c9ba2d5073d49885f09aed93"))
        })

        binding.articleRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.articleRecyclerView.adapter = articleAdapter

    }
}