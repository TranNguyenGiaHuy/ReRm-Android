package com.huytran.rermandroid.data.local.localbean

import com.huytran.grpcdemo.generatedproto.Room
import java.io.File

data class RoomData(val room: Room) {
    var id: Long = 0
    var square: Float = 0F
    var address: String = ""
    var price: Long = 0
    var type: Int = 0
    var numberOfFloor: Int = 0
    var hasFurniture: Boolean = false
    var maxMember: Int = 0
    var cookingAllowance: Boolean = false
    var homeType: Int = 0
    var prepaid: Long = 0
    var description: String = ""
    var title: String = ""

    var ownerId: Long = 0
    var ownerName: String = ""
    var ownerAvatar: File? = null

    var imageList : List<File>? = null

    init {
        id = room.id
        square = room.square
        address = room.address
        price = room.price
        type = room.type
        numberOfFloor = room.numberOfFloor
        hasFurniture = room.hasFurniture
        maxMember = room.maxMember
        cookingAllowance = room.cookingAllowance
        homeType = room.homeType
        prepaid = room.prepaid
        description = room.description
        title = room.title

        ownerId = room.ownerId
        ownerName = room.ownerName
    }
}