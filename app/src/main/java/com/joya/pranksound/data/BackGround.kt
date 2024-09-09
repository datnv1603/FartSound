package com.joya.pranksound.data

import com.joya.pranksound.R

object BackGround {
    private val listBackground = ArrayList<Int>()

    init {
        listBackground.add(R.drawable.bg1)
        listBackground.add(R.drawable.bg3)
        listBackground.add(R.drawable.bg2)
        listBackground.add(R.drawable.bg4)
        listBackground.add(R.drawable.bg5)
        listBackground.add(R.drawable.bg6)
        listBackground.add(R.drawable.bg7)
        listBackground.add(R.drawable.bg8)
        listBackground.add(R.drawable.bg9)
        listBackground.add(R.drawable.bg10)
        listBackground.add(R.drawable.bg11)
        listBackground.add(R.drawable.bg12)
        listBackground.add(R.drawable.bg13)
        listBackground.add(R.drawable.bg14)
        listBackground.add(R.drawable.bg15)
    }

    fun getBackground(position: Int): Int {
        val pos = position % listBackground.size
        return listBackground[pos]
    }
}