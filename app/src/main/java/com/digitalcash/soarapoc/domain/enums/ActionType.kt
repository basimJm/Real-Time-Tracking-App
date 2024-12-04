package com.digitalcash.soarapoc.domain.enums

enum class ActionType(val action: String) {
    REQUEST_ORDER("REQUEST_ORDER"),
    CANCEL_ORDER("CANCEL_ORDER"),
    REJECT_ORDER("REJECT_ORDER"),
    ACCEPT_ORDER("ACCEPT_ORDER"),
    NAVIGATION("NAVIGATION"),
}