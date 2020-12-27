package net.chitholian.voipcodec

class BV16 : Codec() {
    override val number = 106
    override val name = "BV16"
    override val userName = "bv16"
    override val description = "16kbps"
    override val title: String
        get() = "$name ($description)"

    external override fun open(): Int
    external override fun close()
    external override fun encode(lin: ShortArray, offset: Int, encoded: ByteArray, frames: Int): Int
    external override fun decode(encoded: ByteArray, lin: ShortArray, size: Int): Int

    override fun load() {
        try {
            System.loadLibrary("bv16_jni")
            super.load()
        } catch (t: Throwable) {
            t.printStackTrace()
        }
    }
}
