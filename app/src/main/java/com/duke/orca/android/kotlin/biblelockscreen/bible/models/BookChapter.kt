package com.duke.orca.android.kotlin.biblelockscreen.bible.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class BookChapter (
    val id: Int,
    val book: Int,
    val chapter: Int
) : Parcelable

object BookToChapters {
    fun get(book: Int) = map.getOrElse(book) { emptyArray() }

    private val map = mapOf(
        1 to Array(50, Int::inc),
        2 to Array(40, Int::inc),
        3 to Array(27, Int::inc),
        4 to Array(36, Int::inc),
        5 to Array(34, Int::inc),
        6 to Array(24, Int::inc),
        7 to Array(21, Int::inc),
        8 to Array(4, Int::inc),
        9 to Array(31, Int::inc),
        10 to Array(24, Int::inc),
        11 to Array(22, Int::inc),
        12 to Array(25, Int::inc),
        13 to Array(29, Int::inc),
        14 to Array(36, Int::inc),
        15 to Array(10, Int::inc),
        16 to Array(13, Int::inc),
        17 to Array(10, Int::inc),
        18 to Array(42, Int::inc),
        19 to Array(150, Int::inc),
        20 to Array(31, Int::inc),
        21 to Array(12, Int::inc),
        22 to Array(8, Int::inc),
        23 to Array(66, Int::inc),
        24 to Array(52, Int::inc),
        25 to Array(5, Int::inc),
        26 to Array(48, Int::inc),
        27 to Array(12, Int::inc),
        28 to Array(14, Int::inc),
        29 to Array(3, Int::inc),
        30 to Array(9, Int::inc),
        31 to Array(1, Int::inc),
        32 to Array(4, Int::inc),
        33 to Array(7, Int::inc),
        34 to Array(3, Int::inc),
        35 to Array(3, Int::inc),
        36 to Array(3, Int::inc),
        37 to Array(2, Int::inc),
        38 to Array(14, Int::inc),
        39 to Array(4, Int::inc),
        40 to Array(28, Int::inc),
        41 to Array(16, Int::inc),
        42 to Array(24, Int::inc),
        43 to Array(21, Int::inc),
        44 to Array(28, Int::inc),
        45 to Array(16, Int::inc),
        46 to Array(16, Int::inc),
        47 to Array(13, Int::inc),
        48 to Array(6, Int::inc),
        49 to Array(6, Int::inc),
        50 to Array(4, Int::inc),
        51 to Array(4, Int::inc),
        52 to Array(5, Int::inc),
        53 to Array(3, Int::inc),
        54 to Array(6, Int::inc),
        55 to Array(4, Int::inc),
        56 to Array(3, Int::inc),
        57 to Array(1, Int::inc),
        58 to Array(13, Int::inc),
        59 to Array(5, Int::inc),
        60 to Array(5, Int::inc),
        61 to Array(3, Int::inc),
        62 to Array(5, Int::inc),
        63 to Array(1, Int::inc),
        64 to Array(1, Int::inc),
        65 to Array(1, Int::inc),
        66 to Array(22, Int::inc)
    )
}