package org.easydarwin.player.simpleplayer

class MyBean {
    var data: String? = null
    var time: String? = null
    var name: String? = null
    var number: Int = 0

    constructor() {}

    constructor(data: String, number: Int, time: String, name: String) {
        this.data = data
        this.number = number
        this.name = name
        this.time = time
    }
}