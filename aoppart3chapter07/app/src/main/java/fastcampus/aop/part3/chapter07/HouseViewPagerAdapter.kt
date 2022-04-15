package fastcampus.aop.part3.chapter07

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import fastcampus.aop.part3.chapter07.databinding.ItemHouseDetailForViewpagerBinding

class HouseViewPagerAdapter : ListAdapter<HouseModel, HouseViewPagerAdapter.ViewHolder>(diffUtil) {
    inner class ViewHolder(private val binding: ItemHouseDetailForViewpagerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(houseModel: HouseModel) {
            binding.titleTextView.text = houseModel.title
            binding.priceTextView.text = houseModel.price

            Glide.with(binding.thumbnailImageView)
                .load(houseModel.imgUrl)
                .into(binding.thumbnailImageView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemHouseDetailForViewpagerBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<HouseModel>() {
            override fun areItemsTheSame(oldItem: HouseModel, newItem: HouseModel): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: HouseModel, newItem: HouseModel): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}