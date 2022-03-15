package si.uni_lj.fri.pbd.miniapp1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RecyclerAdapter(var memos: List<MemoModel>) : RecyclerView.Adapter<RecyclerAdapter.CardViewHolder>() {

    inner class CardViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
        var memoTitle: TextView? = null
        var memoImage: ImageView? = null
        var memoTimestamp: TextView? = null

        init {
            memoTitle = itemView?.findViewById(R.id.listMemoTitle)
            memoImage = itemView?.findViewById(R.id.listMemoImage)
            memoTimestamp = itemView?.findViewById(R.id.listMemoTimestamp)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        return CardViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.recycler_item_memo_model, parent, false))
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        holder.apply {
            memoTitle?.text = memos[position].title
            memoTimestamp?.text = memos[position].timestamp
            memoImage?.setImageBitmap(memos[position].image)
        }
    }

    override fun getItemCount(): Int {
        return memos.size
    }
}