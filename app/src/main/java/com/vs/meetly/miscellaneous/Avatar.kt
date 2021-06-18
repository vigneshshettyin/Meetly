package com.vs.meetly.miscellaneous
fun randAvatar():String {
   val  url:String="https://www.linkpicture.com/q/"
    val avatarList=listOf("bee_2.png","cat_5.png","chicken_1.png","crab.png","deer_1.png","ferret.png","fox_1.png","frog_1.png","giraffe.png","lion_1.png","ostrich.png","panda_1.png","puffer-fish.png","turtle_1.png","zebra.png")

        return url+avatarList.shuffled().take(1).joinToString()
}