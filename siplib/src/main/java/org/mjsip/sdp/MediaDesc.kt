package org.mjsip.sdp

import net.chitholian.voipcodec.Codec
import org.mjsip.sdp.field.MediaField
import java.util.*

class MediaDesc(
    val media: String,
    val port: Int,
    val codecs: List<Codec>,
    val transport: String = "RTP/AVP",
) {
    fun toMediaDescriptor(): MediaDescriptor {
        val formats = mutableListOf<String>()
        val av = mutableListOf<AttributeField>()
        for (c in codecs) {
            formats.add(c.number.toString())
            var value = "${c.number}"
            if (c.sampleRate > 0) {
                value += " ${c.userName}/${c.sampleRate}"
                if (c.channels > 1) value += "/${c.channels}"
            }
            av.add(AttributeField("rtpmap", value))
        }
        val md = MediaDescriptor(MediaField(media, port, 0, transport, Vector(formats.toList())),
            null,
            av.toTypedArray())
        return MediaDescriptor(md)
    }
}
