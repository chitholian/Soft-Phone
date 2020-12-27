/*
 * Copyright (C) 2009 The Sipdroid Open Source Project
 * 
 * This file is part of Sipdroid (http://www.sipdialer.org)
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
#include <stdlib.h>
#include <stdio.h>
#include <fcntl.h>
#include <unistd.h>
#include <memory.h>
#include <ctype.h>
#include <jni.h>
#include <android/log.h>
extern "C"
{
#include "typedef.h"
#include "codecParameters.h"
#include "utils.h"
#include "bcg729/encoder.h"
#include "bcg729/decoder.h"
}
bcg729EncoderChannelContextStruct *encoderChannelContext;
bcg729DecoderChannelContextStruct *decoderChannelContext;
/*Default bit streem size*/
#define BITSTREAM_SIZE 10
/*Fixed rtp header size*/
#define RTP_HDR_SIZE 12
// #define BLOCK_LEN 160
static int codec_open = 0;
static JavaVM *gJavaVM;
const char *kInterfacePath = "net/chitholian/pjlib/g729";
extern "C" JNIEXPORT jint JNICALL Java_net_chitholian_voipcodec_G729_open(JNIEnv *env, jobject obj)
{
	if (codec_open++ != 0)
		return (jint)0;
	/*--------------------------------------------------------------------------*
	 * Initialization of the coder.											 *
	 *--------------------------------------------------------------------------*/
	encoderChannelContext = initBcg729EncoderChannel(0);
	if (encoderChannelContext == NULL)
	{
		printf("Cannot create encoder\n");
		exit(2);
	}
	/*-----------------------------------------------------------------*
	 *		   Initialization of decoder							 *
	 *-----------------------------------------------------------------*/
	decoderChannelContext = initBcg729DecoderChannel();
	if (decoderChannelContext == NULL)
	{
		printf("Cannot create decoder\n");
		exit(2);
	}
	return (jint)0;
}
extern "C" JNIEXPORT jint JNICALL Java_net_chitholian_voipcodec_G729_encode(JNIEnv *env, jobject obj, jshortArray lin, jint offset, jbyteArray encoded, jint size)
{
	/*** input and output buffers ***/
	int16_t inputBuffer[L_FRAME];		  /* input buffer: the signal */
	uint8_t bitStream[BITSTREAM_SIZE];	/* binary output of the encoder */
	uint16_t outputBuffer[NB_PARAMETERS]; /* output buffer: an array containing the 15 parameters */
	uint8_t bitStreamLength;

	int i;
	unsigned int lin_pos = 0;
	if (!codec_open)
		return 0;
	for (i = 0; i < size; i += L_FRAME)
	{
		env->GetShortArrayRegion(lin, offset + i, L_FRAME, inputBuffer);
		bcg729Encoder(encoderChannelContext, inputBuffer, bitStream, &bitStreamLength);
		env->SetByteArrayRegion(encoded, RTP_HDR_SIZE + lin_pos, BITSTREAM_SIZE, (jbyte *) bitStream);
		lin_pos += BITSTREAM_SIZE;
	}
	return (jint)lin_pos;
}
extern "C" JNIEXPORT jint JNICALL Java_net_chitholian_voipcodec_G729_decode(JNIEnv *env, jobject obj, jbyteArray encoded, jshortArray lin, jint size)
{
	jbyte bitStream[BITSTREAM_SIZE]; /* binary input for the decoder */
	int16_t outputBuffer[L_FRAME]; /* output buffer: the reconstructed signal */ 
// 	Word16 synth[BLOCK_LEN];
// 	jbyte serial[BLOCK_LEN]; /* Serial stream			   */
	unsigned int lin_pos = 0;
	jbyte i;
	int len = 80;
	if (!codec_open)
		return 0;
	for (i = 0; i < size; i = i + BITSTREAM_SIZE)
	{
		//env->GetByteArrayRegion(encoded, RTP_HDR_SIZE, size, serial);
		env->GetByteArrayRegion(encoded, i + RTP_HDR_SIZE, BITSTREAM_SIZE, bitStream);
// 		g729a_dec_process(hDecoder, (unsigned char *)serial, synth, 0);
		bcg729Decoder(decoderChannelContext, (uint8_t *) bitStream, BITSTREAM_SIZE, 0, 0, 0, outputBuffer);
		//env->SetShortArrayRegion(lin, 0, size,synth);
		//env->SetShortArrayRegion(lin, lin_pos, size,synth);
		env->SetShortArrayRegion(lin, lin_pos, len, outputBuffer);
		lin_pos = lin_pos + len;
	}
	return (jint)lin_pos;
}
extern "C" JNIEXPORT void JNICALL Java_net_chitholian_voipcodec_G729_close(JNIEnv *env, jobject obj)
{
	if (--codec_open != 0)
		return;
	/*encoder closed*/
	closeBcg729EncoderChannel(encoderChannelContext);
	/*decoder closed*/
	closeBcg729DecoderChannel(decoderChannelContext);
}
