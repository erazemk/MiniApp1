package si.uni_lj.fri.pbd.miniapp1

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Memos for testing
        val cameraPlaceholder : Bitmap = BitmapFactory.decodeResource(resources, android.R.drawable.ic_menu_camera)
        val memo1 : JSONObject = memoToJson(MemoModel(0, "Title 1", "16. 03. 2022, 11:30:42", "Contents 1", cameraPlaceholder))
        val memo2 : JSONObject = memoToJson(MemoModel(1, "Title 2", "16. 03. 2022, 11:30:41", "Contents 2", cameraPlaceholder))
        val memo3 : JSONObject = memoToJson(MemoModel(2, "Title 3", "16. 03. 2022, 11:30:40", "Contents 3", cameraPlaceholder))
        val sharedPref = getPreferences(Context.MODE_PRIVATE)
        with (sharedPref?.edit()) {
            Log.d("MainActivity", "Adding memos to SharedPreferences")
            this?.putString("0", memo1.toString())
            this?.putString("1", memo2.toString())
            this?.putString("2", memo3.toString())
            this?.putInt("count", 3)
            this?.commit()
        }

        // Specify initial fragment
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container, ListFragment())
            commit()
        }
    }
}
