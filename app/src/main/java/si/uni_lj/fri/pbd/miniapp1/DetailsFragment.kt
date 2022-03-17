package si.uni_lj.fri.pbd.miniapp1

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import org.json.JSONObject

class DetailsFragment(var memoId: Int) : Fragment(R.layout.fragment_details) {

    private lateinit var memo: MemoModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
        with (sharedPref?.edit()) {
            val memoJson = sharedPref?.getString("$memoId", "")
            memo = jsonToMemo(JSONObject(memoJson as String))
            Log.d("DetailsFragment", "Opened memo titled '${memo.title}'")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_details, container, false)

        Log.d("DetailsFragment", "Memo info inside onCreateView: '${memo.title}', '${memo.timestamp}', '${memo.text}'")

        // Attach to views
        val memoTitle = view.findViewById<TextView>(R.id.detailsMemoTitle)
        val memoImage = view.findViewById<ImageView>(R.id.detailsMemoImage)
        val memoTimestamp = view.findViewById<TextView>(R.id.detailsMemoTimestamp)
        val memoText = view.findViewById<TextView>(R.id.detailsMemoText)
        val deleteButton = view.findViewById<Button>(R.id.detailsDeleteButton)
        val shareButton = view.findViewById<Button>(R.id.detailsShareButton)

        // Set elements
        memoTitle?.text = memo.title
        memoImage?.setImageBitmap(memo.image)
        memoTimestamp?.text = memo.timestamp
        memoText?.text = memo.text

        deleteButton?.setOnClickListener {
            val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
            with (sharedPref?.edit()) {
                this?.remove("$memoId") // Delete memo from list
                this?.apply()
            }

            parentFragmentManager.beginTransaction().apply {
                replace(R.id.fragment_container, ListFragment())
                commit()
            }
        }

        shareButton?.setOnClickListener {

        }

        return view
    }
}