package net.chitholian.voipcodec

abstract class Codec {
    var isLoaded = false
        private set
    var isFailed = false
        private set
    var isValid = false
        private set
    var sampleRate = 8000
        protected set
    var frameSize = 160
        protected set
    var channels = 1
        protected set
    open var mediaType = "audio"
        protected set
    abstract val title: String
    abstract val description: String
    abstract val name: String
    abstract val userName: String
    abstract val number: Int

    abstract fun decode(encoded: ByteArray, lin: ShortArray, size: Int): Int
    abstract fun encode(lin: ShortArray, offset: Int, encoded: ByteArray, frames: Int): Int
    abstract fun open(): Int
    abstract fun close()

    open fun load() {
        isLoaded = true
        isValid = true
    }

    protected open fun fail() {
        isFailed = true
        isValid = false
    }

    open fun init() {
        load()
        if (isLoaded) open()
        else fail()
    }
}
