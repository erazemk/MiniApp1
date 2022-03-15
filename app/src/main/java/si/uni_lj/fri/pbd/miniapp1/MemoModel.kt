package si.uni_lj.fri.pbd.miniapp1

import android.graphics.Bitmap
import java.sql.Timestamp

data class MemoModel (
    val title: String,
    val timestamp: Timestamp,
    val contents: String,
    val image: Bitmap
)