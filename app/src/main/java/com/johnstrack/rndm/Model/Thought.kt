package com.johnstrack.rndm.Model

import java.util.*

/**
 * Created by John on 5/1/2018 at 3:21 PM.
 */
data class Thought constructor(val userName: String, val timestamp: Date, val thoughtTxt: String,
                               val numLikes: Int, val numComments: Int, val documentId: String, val userId: String)