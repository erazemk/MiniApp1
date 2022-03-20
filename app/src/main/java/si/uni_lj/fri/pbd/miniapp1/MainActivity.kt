package si.uni_lj.fri.pbd.miniapp1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

/* ATTRIBUTION:
    Parts of the code in this project have either been copied from or inspired by code from
    previous labs, PDFs published on Učilnica or from examples on https://developers.android.com.
 */

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Specify initial fragment when first starting app
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.fragment_container, ListFragment())
                commit()
            }
        }
    }
}