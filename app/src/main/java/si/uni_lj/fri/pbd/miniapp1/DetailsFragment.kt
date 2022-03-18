package si.uni_lj.fri.pbd.miniapp1

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream

class DetailsFragment : Fragment(R.layout.fragment_details) {

    private lateinit var memo: MemoModel
    private var memoId = -1
    private var emailIntentRequestId = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)

        // Get memo ID from RecyclerAdapter through a bundle
        if (savedInstanceState != null) {
            memoId = savedInstanceState.getInt("memoId")
        } else {
            memoId = this.arguments?.getInt("memoId") as Int
        }

        with (sharedPref?.edit()) {
            val memoJson = sharedPref?.getString("$memoId", "")
            memo = jsonToMemo(JSONObject(memoJson as String))
            Log.d("DetailsFragment", "Opened memo titled '${memo.title}'")
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("memoId", memoId)
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
                this?.remove("${memo.id}") // Delete memo from list
                this?.apply()
            }

            parentFragmentManager.beginTransaction().apply {
                parentFragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                replace(R.id.fragment_container, ListFragment())
                commit()
            }
        }

        shareButton?.setOnClickListener {
            // Save added image as file for sending over email
            val bitmap = memoImage.drawable.toBitmap()
            val imageFile = saveBitmap(bitmap)
            val uriFile = FileProvider.getUriForFile(requireContext(), BuildConfig.APPLICATION_ID + ".provider", imageFile)
            val emailIntent = Intent(Intent.ACTION_SEND)
            emailIntent.putExtra(Intent.EXTRA_STREAM, uriFile)
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, memo.title)
            emailIntent.putExtra(Intent.EXTRA_TEXT, memo.timestamp + "\n\n" + memo.text)
            emailIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            emailIntent.type = "plain/text"

            try {
                startActivityForResult(emailIntent, emailIntentRequestId)
            } catch (e: ActivityNotFoundException) {
                Snackbar.make(view, R.string.memo_email_send_error, BaseTransientBottomBar.LENGTH_LONG).show()
            }
        }

        return view
    }

    private fun saveBitmap(bitmap: Bitmap) : File {
        val externalDir = context?.getExternalFilesDir(null)
        val imageFile = File(externalDir, "temp.jpeg")
        val out = FileOutputStream(imageFile)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
        out.flush()
        out.close()
        return imageFile
    }
}