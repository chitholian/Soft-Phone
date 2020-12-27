package net.chitholian.voipcodec

class Speex : Codec() {
    private val compression = 6

    override val number = 97
    override val name = "Speex"
    override val userName = "speex"
    override val description = "11kbps"
    override val title: String
        get() = "$name ($description)"

    override fun open(): Int {
        return open(compression)
    }

    external fun open(compression: Int): Int
    external override fun decode(encoded: ByteArray, lin: ShortArray, size: Int): Int
    external override fun encode(lin: ShortArray, offset: Int, encoded: ByteArray, frames: Int): Int
    external override fun close()

    override fun load() {
        try {
            System.loadLibrary("speex_jni")
            super.load()
        } catch (t: Throwable) {
            t.printStackTrace()
        }
    }
}
