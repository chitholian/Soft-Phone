package net.chitholian.voipcodec

class ALAW : Codec() {
    override val number = 8
    override val name = "PCMA"
    override val userName = "PCMA"
    override val description = "64kbps"
    override val title: String
        get() = "$name ($description)"

    override fun open(): Int {
        return 0
    }

    override fun decode(encoded: ByteArray, lin: ShortArray, size: Int): Int {
        G711.alaw2linear(encoded, lin, size)
        return size
    }

    override fun encode(lin: ShortArray, offset: Int, encoded: ByteArray, frames: Int): Int {
        G711.linear2alaw(lin, offset, encoded, frames)
        return frames
    }

    override fun close() {}
}
