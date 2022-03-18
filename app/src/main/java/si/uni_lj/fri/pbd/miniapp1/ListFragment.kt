package si.uni_lj.fri.pbd.miniapp1

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.json.JSONObject

class ListFragment : Fragment(R.layout.fragment_list) {

    private var memos: MutableList<MemoModel> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get memo count from SharedPreferences
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
        val memoCount = sharedPref?.getInt("count", 0)
        Log.d("ListFragment", "Got $memoCount memos")

        // Restore saved memos
        if (memoCount != null && memoCount > 0) {
            with(sharedPref.edit()) {
                for (memoId in 0 until memoCount) {
                    val memoJson = sharedPref.getString("$memoId", "") as String
                    if (memoJson != "") {
                        val memo : MemoModel = jsonToMemo(JSONObject(memoJson))
                        Log.d("ListFragment", "Added memo: '${memo.id}: ${memo.title}'")
                        memos.add(memo)
                    }
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_list, container, false)
        val recyclerView: RecyclerView = view.findViewById(R.id.listRecyclerView)
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(view.context)
        val adapter: RecyclerView.Adapter<*> = RecyclerAdapter(memos)
        val addButton: FloatingActionButton = view.findViewById(R.id.listMemoAddButton)

        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter

        addButton.setOnClickListener {
            Log.d("ListFragment", "Switching to new fragment")

            parentFragmentManager.beginTransaction().apply {
                replace(R.id.fragment_container, NewFragment())
                addToBackStack(null) // Allow returning to list on back button press
                commit()
            }
        }

        return view
    }
}