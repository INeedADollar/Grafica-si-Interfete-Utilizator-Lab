package org.gui;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.awt.ImageUtil;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;
import com.jogamp.opengl.util.texture.TextureData;

public class Chessboard implements GLEventListener {

    private Texture whiteTexture;
    private Texture blackTexture;
    private GLCanvas canvas;

    public Chessboard() {
        GLProfile glProfile = GLProfile.getDefault();
        // Creating an object to manipulate OpenGL parameters.
        GLCapabilities capabilities = new GLCapabilities(glProfile);

        // Setting some OpenGL parameters.
        capabilities.setHardwareAccelerated(true);
        capabilities.setDoubleBuffered(true);

        canvas = new GLCanvas(capabilities);
        canvas.addGLEventListener(this);
    }

    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();


        gl.glEnable(GL2.GL_TEXTURE_2D);

        // load the white texture from an image file

        try {
            whiteTexture = loadTexture(gl, "textures/texture1.jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            blackTexture = loadTexture(gl, "textures/texture5.jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Texture loadTexture(GL2 gl, String filename) throws IOException {
        BufferedImage image = ImageIO.read(new File(filename));
        ImageUtil.flipImageVertically(image);

        TextureData data = AWTTextureIO.newTextureData(gl.getGLProfile(), image, false);
        Texture texture = TextureIO.newTexture(data);
        int textureId = texture.getTextureObject(gl);


        gl.glBindTexture(GL2.GL_TEXTURE_2D, textureId);
        gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR);
        gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR);
        gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_REPEAT);
        gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T, GL2.GL_REPEAT);
        gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_MODULATE);

        return texture;
    }

    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();

        //gl.glClear(GL2.GL_COLOR_BUFFER_BIT);


        //  chessboard
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if ((i + j) % 2 == 1) {
                    whiteTexture.bind(gl);                                                  // <--- but here
                    gl.glBegin(GL2.GL_QUADS);
                    gl.glTexCoord2f(0.0f, 0.0f);
                    gl.glVertex2f(-1.0f + j * 0.25f, -1.0f + i * 0.25f);
                    gl.glTexCoord2f(1.0f, 0.0f);
                    gl.glVertex2f(-0.75f + j * 0.25f, -1.0f + i * 0.25f);
                    gl.glTexCoord2f(1.0f, 1.0f);
                    gl.glVertex2f(-0.75f + j * 0.25f, -0.75f + i * 0.25f);
                    gl.glTexCoord2f(0.0f, 1.0f);
                    gl.glVertex2f(-1.0f + j * 0.25f, -0.75f + i * 0.25f);
                    gl.glEnd();
                } else {
                    blackTexture.bind(gl);                                                 // <--- but here
                    gl.glBegin(GL2.GL_QUADS);
                    gl.glTexCoord2f(0.0f, 0.0f);
                    gl.glVertex2f(-1.0f + j * 0.25f, -1.0f + i * 0.25f);
                    gl.glTexCoord2f(1.0f, 0.0f);
                    gl.glVertex2f(-0.75f + j * 0.25f, -1.0f + i * 0.25f);
                    gl.glTexCoord2f(1.0f, 1.0f);
                    gl.glVertex2f(-0.75f + j * 0.25f, -0.75f + i * 0.25f);
                    gl.glTexCoord2f(0.0f, 1.0f);
                    gl.glVertex2f(-1.0f + j * 0.25f, -0.75f + i * 0.25f);
                    gl.glEnd();
                }
            }
        }
    }

    public void dispose(GLAutoDrawable drawable) {
        // dispose of the textures
        whiteTexture.destroy(drawable.getGL().getGL2());
        blackTexture.destroy(drawable.getGL().getGL2());
    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL2 gl = drawable.getGL().getGL2();


        gl.glViewport(0, 0, width, height);

        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glOrtho(-1.0, 1.0, -1.0, 1.0, -1.0, 1.0);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    public void run() {
        JFrame frame = new JFrame("Chessboard");
        frame.getContentPane().add(canvas);
        frame.setSize(400, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
