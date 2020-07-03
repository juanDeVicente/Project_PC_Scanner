package com.projectpcscanner.models
import org.json.JSONObject
import android.util.Base64

class TasksModel (var name: String, var PID: Int, var responding: Boolean, var username: String, var memoryUse: String, var description: String, var icon: ByteArray)
{
    constructor(obj: JSONObject): this(
        obj.getString("Name"),
        obj.getInt("PID"),
        obj.getBoolean("Responding"),
        obj.getString("Username"),
        obj.getString("MemoryUse"),
        obj.getString("Description"),
        Base64.decode(obj.getString("Icon"),  Base64.DEFAULT)
    )

    override fun equals(other: Any?): Boolean {
        if (other is TasksModel)
            return this.PID == other.PID && this.name == other.name && this.description == other.description
        return super.equals(other)
    }
}