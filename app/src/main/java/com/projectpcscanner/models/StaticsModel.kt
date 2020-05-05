package com.projectpcscanner.models

class StaticsModel(val name: String, val currentValue: Float, val minValue: Float, val maxValue: Float, val relative: Boolean)
{
    override fun equals(other: Any?): Boolean {
        if (other is StaticsModel)
            return other.name == name
        return super.equals(other)
    }
}