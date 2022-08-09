package com.slatmate.user.model

data class ModelRegisterResponse (

    val message : String?            = null,
    val status  : Boolean?           = null,
    val results : ArrayList<Results> = arrayListOf()
        )
data class Results (

    val id        : String? = null,
    val firstName : String? = null,
    val lastName  : String? = null,
    val email     : String? = null,
    val gender    : String? = null,
    val token     : String? = null

)