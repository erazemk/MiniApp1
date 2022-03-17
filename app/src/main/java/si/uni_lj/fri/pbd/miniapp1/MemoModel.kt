package si.uni_lj.fri.pbd.miniapp1

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import org.json.JSONObject
import java.io.ByteArrayOutputStream

data class MemoModel(
    var id: Int,
    var title: String,
    var timestamp: String,
    var text: String,
    var image: Bitmap
)

fun memoToJson(memo: MemoModel) : JSONObject {
    val jsonObject = JSONObject()
    val baos = ByteArrayOutputStream()

    // Convert image to base64
    memo.image.compress(Bitmap.CompressFormat.JPEG, 100, baos)

    // Create JSON string
    jsonObject.put("ID", memo.id)
    jsonObject.put("Title", memo.title)
    jsonObject.put("Timestamp", memo.timestamp)
    jsonObject.put("Text", memo.text)
    jsonObject.put("Image", Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT))
    return jsonObject
}

fun jsonToMemo(jsonObject: JSONObject) : MemoModel {
    val bytes = Base64.decode(jsonObject.get("Image") as String, Base64.DEFAULT)

    return MemoModel(
        id = jsonObject.get("ID") as Int,
        title = jsonObject.get("Title") as String,
        timestamp = jsonObject.get("Timestamp") as String,
        text = jsonObject.get("Text") as String,
        image = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    )
}