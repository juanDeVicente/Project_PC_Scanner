package com.projectpcscanner.models

import com.projectpcscanner.utils.toMap
import org.json.JSONObject

class StaticsModel(val name: String, var currentValue: Float, val minValue: Float, val maxValue: Float, val scalingFactor: Float, val measurementUnit: String, var details: Map<String, String>)
{
    constructor(jsonObject: JSONObject): this(
        jsonObject.getString("name"),
        jsonObject.getDouble("current_value").toFloat(),
        jsonObject.getDouble("min_value").toFloat(),
        jsonObject.getDouble("max_value").toFloat(),
        jsonObject.getDouble("scaling_factor").toFloat(),
        jsonObject.getString("measurement_unit"),
        toMap(jsonObject.getJSONObject("details")) as Map<String, String>
    )

    override fun equals(other: Any?): Boolean {
        if (other is StaticsModel)
            return other.name == name
        return super.equals(other)
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + currentValue.hashCode()
        result = 31 * result + minValue.hashCode()
        result = 31 * result + maxValue.hashCode()
        result = 31 * result + scalingFactor.hashCode()
        result = 31 * result + measurementUnit.hashCode()
        result = 31 * result + details.hashCode()
        return result
    }
}