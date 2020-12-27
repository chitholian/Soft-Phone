package net.chitholian.voipcodec

class ULAW : Codec() {

    override val number = 0
    override val name = "PCMU"
    override val userName = "PCMU"
    override val description = "64kbps"
    override val title: String
        get() = "$name ($description)"

    override fun open(): Int {
        return 0
    }

    override fun decode(encoded: ByteArray, lin: ShortArray, size: Int): Int {
        G711.ulaw2linear(encoded, lin, size)
        return size
    }

    override fun encode(lin: ShortArray, offset: Int, encoded: ByteArray, frames: Int): Int {
        G711.linear2ulaw(lin, offset, encoded, frames)
        return frames
    }

    override fun close() {}
}
