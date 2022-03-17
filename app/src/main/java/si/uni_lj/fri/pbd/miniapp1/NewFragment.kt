package si.uni_lj.fri.pbd.miniapp1

import android.app.Activity.RESULT_OK
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import java.util.Date
import java.text.SimpleDateFormat

class NewFragment : Fragment(R.layout.fragment_new) {

    private var memoImage: ImageView? = null
    private val IMAGE_CAPTURE_REQUEST_CODE = 1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("NewFragment", "Created new fragment")

        val view = inflater.inflate(R.layout.fragment_new, container, false)
        memoImage = view.findViewById(R.id.newMemoImage)
        val memoTitle = view.findViewById<TextView>(R.id.newMemoTitle)
        val memoText = view.findViewById<TextView>(R.id.newMemoText)
        val takePhotoButton = view.findViewById<Button>(R.id.newTakeImageButton)
        val saveMemoButton = view.findViewById<Button>(R.id.newSaveMemoButton)

        takePhotoButton.setOnClickListener {
            Log.d("NewFragment", "Taking new photo")
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            try {
                startActivityForResult(takePictureIntent, IMAGE_CAPTURE_REQUEST_CODE)
            } catch (e: ActivityNotFoundException) {
                Snackbar.make(view, R.string.memo_camera_error, BaseTransientBottomBar.LENGTH_LONG).show()
            }
        }

        saveMemoButton.setOnClickListener {
            Log.d("NewFragment", "Clicked on save memo")

            if (memoImage?.tag == null) {
                Log.d("NewFragment", "Null tag")
            }

            // Check if any field is empty
            if (memoTitle.text.isEmpty() || memoText.text.isEmpty() || memoImage?.tag == null) {
                if (memoTitle.text.isEmpty()) memoTitle.error = getString(R.string.memo_title_empty_error)
                if (memoText.text.isEmpty()) memoText.error = getString(R.string.memo_text_empty_error)
                if (memoImage?.tag == null) Snackbar.make(view, R.string.memo_image_empty_error, BaseTransientBottomBar.LENGTH_LONG).show()
            } else {
                Log.d("NewFragment", "Else")

                val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
                val memoId = sharedPref?.getInt("count", 0) as Int
                val bitmap = memoImage?.drawable as BitmapDrawable

                val memo = MemoModel(
                    id = memoId,
                    title = memoTitle.text.toString(),
                    timestamp = SimpleDateFormat("dd. MM. yy, hh:mm:ss").format(Date()),
                    text = memoText.text.toString(),
                    image = bitmap.bitmap
                )

                Log.d("NewFragment", "Adding memo '${memo.id}', '${memo.title}', '${memo.timestamp}', '${memo.text}'")

                val jsonObject = memoToJson(memo)

                with (sharedPref.edit()) {
                    this?.putString("$memoId", jsonObject.toString())
                    this?.putInt("count", memoId + 1)
                    this?.commit()
                }

                parentFragmentManager.beginTransaction().apply {
                    replace(R.id.fragment_container, ListFragment())
                    commit()
                }
            }
        }

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == IMAGE_CAPTURE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            val imageBitmap = data.extras?.get("data") as Bitmap
            memoImage?.setImageBitmap(imageBitmap)
            memoImage?.tag = "Nonempty" // Random text to make tag not null
        } else {
            Log.d("NewFragment", "Error occurred while taking image with camera")
        }
    }
}