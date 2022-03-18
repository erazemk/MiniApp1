package si.uni_lj.fri.pbd.miniapp1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Specify initial fragment
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.fragment_container, ListFragment())
                commit()
            }
        }
    }
}