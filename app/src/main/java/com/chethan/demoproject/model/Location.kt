package com.chethan.demoproject.model

import java.io.Serializable


/**
 *Created by Bhagavan Byreddy on 19/08/21.
 */
data class Location(
    var name:String,
    var lat:Double,
    var long:Double
): Serializable
