package net.chitholian.voipcodec

class G729 : Codec() {
    override val number = 18
    override val name = "G729"
    override val userName = "G729"
    override val description = "8kbps"
    override val title: String
        get() = "$name ($description)"

    external override fun open(): Int
    external override fun close()
    external override fun encode(lin: ShortArray, offset: Int, encoded: ByteArray, frames: Int): Int
    external override fun decode(encoded: ByteArray, lin: ShortArray, size: Int): Int

    override fun load() {
        try {
            System.loadLibrary("g729_jni")
            super.load()
        } catch (t: Throwable) {
            t.printStackTrace()
        }
    }
}
