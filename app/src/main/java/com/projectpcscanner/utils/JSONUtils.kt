package com.projectpcscanner.utils

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

// https://www.quora.com/How-do-I-convert-JSONObject-to-HashMap-in-Java

@Throws(JSONException::class)
fun toMap(`object`: JSONObject): Map<String, Any> {
    val map: MutableMap<String, Any> = HashMap()
    val keysItr = `object`.keys()
    while (keysItr.hasNext()) {
        val key = keysItr.next()
        var value = `object`[key]
        if (value is JSONArray)
            value = toList(value)
        else if (value is JSONObject)
            value = toMap(value)

        map[key] = value
    }
    return map
}

@Throws(JSONException::class)
fun toList(array: JSONArray): List<Any> {
    val list: MutableList<Any> = ArrayList()
    for (i in 0 until array.length()) {
        var value = array[i]
        if (value is JSONArray)
            value = toList(value)
        else if (value is JSONObject)
            value = toMap(value)
        list.add(value)
    }
    return list
}