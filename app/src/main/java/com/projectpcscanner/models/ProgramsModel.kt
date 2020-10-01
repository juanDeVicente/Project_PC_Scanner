package com.projectpcscanner.models

import android.util.Base64
import org.json.JSONObject

class ProgramsModel(var name: String, var icon: ByteArray) {
    constructor(obj: JSONObject): this(
        obj.getString("Name"),
        Base64.decode(obj.getString("Icon"),  Base64.DEFAULT)
    )
}