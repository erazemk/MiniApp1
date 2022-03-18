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
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import java.util.Date
import java.text.SimpleDateFormat

class NewFragment : Fragment(R.layout.fragment_new) {

    private var memoTitle: EditText? = null
    private var memoImage: ImageView? = null
    private var memoText: EditText? = null
    private var title: String? = null
    private var text: String? = null
    private var image: Bitmap? = null
    private var imageTag: Any? = null

    private val imageCaptureIntentRequestId = 1

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        val isNotEmpty = (memoText?.text?.isNotBlank() == true)
        Log.d("T", "Is memo text filled in: $isNotEmpty, '${memoText?.text}'")

        // Save memo data
        outState.putString("title", memoTitle?.text.toString())
        outState.putString("text", memoText?.text.toString())
        if (memoImage?.tag != null) outState.putParcelable("image", memoImage?.drawable?.toBitmap())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Restore memo data
        if (savedInstanceState != null) {
            if (savedInstanceState.getString("title")?.isNotBlank() == true) title = savedInstanceState.getString("title")
            if (savedInstanceState.getString("text")?.isNotBlank() == true) text = savedInstanceState.getString("text")
            if (savedInstanceState.getParcelable("image") as Bitmap? != null) {
                image = savedInstanceState.getParcelable("image")
                imageTag = "nonempty"
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("NewFragment", "Created new fragment")

        val view = inflater.inflate(R.layout.fragment_new, container, false)
        val takePhotoButton = view.findViewById<Button>(R.id.newTakeImageButton)
        val saveMemoButton = view.findViewById<Button>(R.id.newSaveMemoButton)

        memoImage = view.findViewById(R.id.newMemoImage)
        memoTitle = view.findViewById(R.id.newMemoTitle)
        memoText = view.findViewById(R.id.newMemoText)

        // Set EditText's text to saved data
        if (title?.isNotBlank() == true) memoTitle?.setText(title)
        if (text?.isNotBlank() == true) memoText?.setText(text)
        if (image != null) memoImage?.setImageBitmap(image)
        if (imageTag != null) memoImage?.tag = imageTag

        takePhotoButton.setOnClickListener {
            Log.d("NewFragment", "Taking new photo")
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            try {
                startActivityForResult(takePictureIntent, imageCaptureIntentRequestId)
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
            if (memoTitle?.text?.isBlank() == true || memoText?.text?.isBlank() == true || memoImage?.tag == null) {
                if (memoTitle?.text?.isBlank() == true) memoTitle?.error = getString(R.string.memo_title_empty_error)
                if (memoText?.text?.isBlank() == true) memoText?.error = getString(R.string.memo_text_empty_error)
                if (memoImage?.tag == null) Snackbar.make(view, R.string.memo_image_empty_error, BaseTransientBottomBar.LENGTH_LONG).show()
            } else {
                Log.d("NewFragment", "Else")

                val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
                val memoId = sharedPref?.getInt("count", 0) as Int
                val bitmap = memoImage?.drawable as BitmapDrawable

                val memo = MemoModel(
                    id = memoId,
                    title = memoTitle?.text.toString(),
                    timestamp = SimpleDateFormat("dd. MM. yy, hh:mm:ss").format(Date()),
                    text = memoText?.text.toString(),
                    image = bitmap.bitmap
                )

                Log.d("NewFragment", "Adding memo '${memo.id}', '${memo.title}', '${memo.timestamp}', '${memo.text}'")

                val jsonObject = memoToJson(memo)

                with (sharedPref.edit()) {
                    this?.putString("$memoId", jsonObject.toString())
                    this?.putInt("count", memoId + 1)
                    this?.apply()
                }

                // Return to list view
                parentFragmentManager.beginTransaction().apply {
                    replace(R.id.fragment_container, ListFragment())
                    commit()
                }
            }
        }

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == imageCaptureIntentRequestId && resultCode == RESULT_OK && data != null) {
            val imageBitmap = data.extras?.get("data") as Bitmap
            memoImage?.setImageBitmap(imageBitmap)
            memoImage?.tag = "Nonempty" // Random text to make tag not null
        } else {
            Log.d("NewFragment", "Error occurred while taking image with camera")
        }
    }
}