import android.content.Context
import android.content.SharedPreferences

class NoteSharedPreferencesHelper(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("MyNotes", Context.MODE_PRIVATE)

    fun saveNote(note: String) {
        val editor = sharedPreferences.edit()
        editor.putString("userNote", note)
        editor.apply()
    }

    fun getNote(): String {
        return sharedPreferences.getString("userNote", "") ?: ""
    }
}
