package com.dc.testopengl;

import android.graphics.Bitmap;
import android.opengl.GLES20;

import java.nio.ByteBuffer;

/**
 * Created by duancong on 21/07/2018.
 */

public class Utils {

    public static Bitmap saveTexture(int texture, int width, int height) {
        int[] frame = new int[1];
        GLES20.glGenFramebuffers(1, frame, 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frame[0]);
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER,
                GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, texture, 0);

        byte[] data = new byte[width * height * 4];
        ByteBuffer buffer = ByteBuffer.wrap(data);
        GLES20.glPixelStorei(GLES20.GL_PACK_ALIGNMENT, 1);
        GLES20.glReadPixels(0, 0, width, height, GLES20.GL_RGBA,
                GLES20.GL_UNSIGNED_BYTE, buffer);

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        GLES20.glDeleteFramebuffers(1, frame, 0);

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.copyPixelsFromBuffer(ByteBuffer.wrap(data));
        return bitmap;
    }
}
