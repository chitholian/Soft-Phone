/*
 * Copyright (C) 2009 The Sipdroid Open Source Project
 * Copyright (C) 2005 Luca Veltri - University of Parma - Italy
 *
 * This file is part of Sipdroid (http://www.sipdroid.org)
 *
 * Sipdroid is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This source code is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this source code; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package org.mjsip.rtp;

import org.zoolu.util.Random;

/**
 * RtpPacket implements a RTP packet.
 */
public class RtpPacket {
    /* RTP packet buffer containing both the RTP header and payload */
    byte[] buffer;

    /* RTP packet length */
    int length;
    int offset = 0;

    /* RTP header length */
    // int header_len;

    /**
     * Gets the RTP packet
     */
    public byte[] getBuffer() {
        return buffer;
    }

    /**
     * Gets the RTP packet length
     */
    public int getLength() {
        return length;
    }

    /**
     * Gets the RTP header length
     */
    public int getHeaderLength() {
        if (length >= 12)
            return 12 + 4 * getCscrCount();
        else
            return length; // broken packet
    }

    /**
     * Gets the RTP header length
     */
    public int getPayloadLength() {
        if (length >= 12)
            return length - getHeaderLength();
        else
            return 0; // broken packet
    }

    /**
     * Sets the RTP payload length
     */
    public void setPayloadLength(int len) {
        length = getHeaderLength() + len;
    }

    // version (V): 2 bits
    // padding (P): 1 bit
    // extension (X): 1 bit
    // CSRC count (CC): 4 bits
    // marker (M): 1 bit
    // payload type (PT): 7 bits
    // sequence number: 16 bits
    // timestamp: 32 bits
    // SSRC: 32 bits
    // CSRC list: 0 to 15 items, 32 bits each

    /**
     * Gets the version (V)
     */
    public int getVersion() {
        if (length >= 12)
            return (buffer[0] >> 6 & 0x03);
        else
            return 0; // broken packet
    }

    /**
     * Sets the version (V)
     */
    public void setVersion(int v) {
        if (length >= 12)
            buffer[0] = (byte) ((buffer[0] & 0x3F) | ((v & 0x03) << 6));
    }

    /**
     * Whether has padding (P)
     */
    public boolean hasPadding() {
        if (length >= 12)
            return getBit(buffer[0], 5);
        else
            return false; // broken packet
    }

    /**
     * Set padding (P)
     */
    public void setPadding(boolean p) {
        if (length >= 12)
            buffer[0] = setBit(p, buffer[0], 5);
    }

    /**
     * Whether has extension (X)
     */
    public boolean hasExtension() {
        if (length >= 12)
            return getBit(buffer[0], 4);
        else
            return false; // broken packet
    }

    /**
     * Set extension (X)
     */
    public void setExtension(boolean x) {
        if (length >= 12)
            buffer[0] = setBit(x, buffer[0], 4);
    }

    /**
     * Gets the CSCR count (CC)
     */
    public int getCscrCount() {
        if (length >= 12)
            return (buffer[0] & 0x0F);
        else
            return 0; // broken packet
    }

    /**
     * Whether has marker (M)
     */
    public boolean hasMarker() {
        if (length >= 12)
            return getBit(buffer[1], 7);
        else
            return false; // broken packet
    }

    /**
     * Set marker (M)
     */
    public void setMarker(boolean m) {
        if (length >= 12)
            buffer[1] = setBit(m, buffer[1], 7);
    }

    /**
     * Gets the payload type (PT)
     */
    public int getPayloadType() {
        if (length >= 12)
            return (buffer[1] & 0x7F);
        else
            return -1; // broken packet
    }

    /**
     * Sets the payload type (PT)
     */
    public void setPayloadType(int pt) {
        if (length >= 12)
            buffer[1] = (byte) ((buffer[1] & 0x80) | (pt & 0x7F));
    }

    /**
     * Gets the sequence number
     */
    public int getSequenceNumber() {
        if (length >= 12)
            return getInt(buffer, 2, 4);
        else
            return 0; // broken packet
    }

    /**
     * Sets the sequence number
     */
    public void setSequenceNumber(int sn) {
        if (length >= 12)
            setInt(sn, buffer, 2, 4);
    }

    /**
     * Gets the timestamp
     */
    public long getTimestamp() {
        if (length >= 12)
            return getLong(buffer, 4, 8);
        else
            return 0; // broken packet
    }

    /**
     * Sets the timestamp
     */
    public void setTimestamp(long timestamp) {
        if (length >= 12)
            setLong(timestamp, buffer, 4, 8);
    }

    /**
     * Gets the SSCR
     */
    public long getSscr() {
        if (length >= 12)
            return getLong(buffer, 8, 12);
        else
            return 0; // broken packet
    }

    /**
     * Sets the SSCR
     */
    public void setSscr(long ssrc) {
        if (length >= 12)
            setLong(ssrc, buffer, 8, 12);
    }

    /**
     * Gets the CSCR list
     */
    public long[] getCscrList() {
        int cc = getCscrCount();
        long[] cscr = new long[cc];
        for (int i = 0; i < cc; i++)
            cscr[i] = getLong(buffer, 12 + 4 * i, 16 + 4 * i);
        return cscr;
    }

    /**
     * Sets the CSCR list
     */
    public void setCscrList(long[] cscr) {
        if (length >= 12) {
            int cc = cscr.length;
            if (cc > 15)
                cc = 15;
            buffer[0] = (byte) (((buffer[0] >> 4) << 4) + cc);
            cscr = new long[cc];
            for (int i = 0; i < cc; i++)
                setLong(cscr[i], buffer, 12 + 4 * i, 16 + 4 * i);
            // header_len=12+4*cc;
        }
    }

    /**
     * Sets the payload
     */
    public void setPayload(byte[] payload, int len) {
        if (length >= 12) {
            int header_len = getHeaderLength();
            for (int i = 0; i < len; i++)
                buffer[header_len + i] = payload[i];
            length = header_len + len;
        }
    }

    /**
     * Gets the payload
     */
    public byte[] getPayload() {
        int header_len = getHeaderLength();
        int len = length - header_len;
        byte[] payload = new byte[len];
        for (int i = 0; i < len; i++)
            payload[i] = buffer[header_len + i];
        return payload;
    }

    /**
     * Creates a new RTP packet
     */
    public RtpPacket(byte[] buffer, int packet_length) {
        this.buffer = buffer;
        length = packet_length;
        if (length < 12)
            length = 12;
        init(0x0F);
    }

    /**
     * init the RTP packet header (only PT)
     */
    public void init(int ptype) {
        init(ptype, Random.nextLong());
    }

    /**
     * init the RTP packet header (PT and SSCR)
     */
    public void init(int ptype, long sscr) {
        init(ptype, Random.nextInt(), Random.nextLong(), sscr);
    }

    /**
     * init the RTP packet header (PT, SQN, TimeStamp, SSCR)
     */
    public void init(int ptype, int seqn, long timestamp, long sscr) {
        setVersion(2);
        setPayloadType(ptype);
        setSequenceNumber(seqn);
        setTimestamp(timestamp);
        setSscr(sscr);
    }

    // *********************** Private and Static ***********************

    /**
     * Gets int value
     */
    private static int getInt(byte b) {
        return ((int) b + 256) % 256;
    }

    /**
     * Gets long value
     */
    private static long getLong(byte[] data, int begin, int end) {
        long n = 0;
        for (; begin < end; begin++) {
            n <<= 8;
            n += data[begin] & 0xFF;
        }
        return n;
    }

    /**
     * Sets long value
     */
    private static void setLong(long n, byte[] data, int begin, int end) {
        for (end--; end >= begin; end--) {
            data[end] = (byte) (n % 256);
            n >>= 8;
        }
    }

    /**
     * Gets Int value
     */
    private static int getInt(byte[] data, int begin, int end) {
        return (int) getLong(data, begin, end);
    }

    /**
     * Sets Int value
     */
    private static void setInt(int n, byte[] data, int begin, int end) {
        setLong(n, data, begin, end);
    }

    /**
     * Gets bit value
     */
    private static boolean getBit(byte b, int bit) {
        return (b >> bit) == 1;
    }

    /**
     * Sets bit value
     */
    private static byte setBit(boolean value, byte b, int bit) {
        if (value)
            return (byte) (b | (1 << bit));
        else
            return (byte) ((b | (1 << bit)) ^ (1 << bit));
    }
}
