package org.gui;

import javax.imageio.ImageIO;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import javax.swing.*;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.awt.ImageUtil;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

public class TextureSquares implements GLEventListener {
    private GLCanvas canvas;
    private Texture texture1;
    private Texture texture2;

    private final int WIDTH = 640;
    private final int HEIGHT = 480;
    private float x = -0.5f;
    private float y = 0.5f;
    private float speed = 0.01f;

    public void run() {
        // Create the window
        JFrame frame = new JFrame("Two Squares");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(WIDTH, HEIGHT);
        frame.setLocationRelativeTo(null);
        canvas = new GLCanvas();
        canvas.addGLEventListener(this);

        frame.getContentPane().add(canvas, BorderLayout.CENTER);
        frame.setVisible(true);
        final FPSAnimator animator = new FPSAnimator(canvas, 60);
        animator.start();
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();

        // Load textures
        try {
            texture1 = loadTexture(gl, "textures/texture3.jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            texture2 = loadTexture(gl, "textures/texture4.jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Enable blending
        gl.glEnable(GL.GL_BLEND);
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        texture1.destroy(gl);
        texture2.destroy(gl);
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();

        // Update the position of square 1

        gl.glViewport(0, 0, WIDTH, HEIGHT);
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);

        gl.glEnable(GL.GL_TEXTURE_2D);

        //square 1
        texture1.enable(gl);
        texture1.bind(gl);
        gl.glBegin(GL2.GL_QUADS);
        gl.glTexCoord2f(0.0f, 1.0f);
        gl.glVertex3f(x, y, 0.0f);
        gl.glTexCoord2f(1.0f, 1.0f);
        gl.glVertex3f(x, y - 1.0f, 0.0f);
        gl.glTexCoord2f(1.0f, 0.0f);
        gl.glVertex3f(x + 1.0f, y - 1.0f, 0.0f);
        gl.glTexCoord2f(0.0f, 0.0f);
        gl.glVertex3f(x + 1.0f, y, 0.0f);
        gl.glEnd();

        //square 2
        texture2.enable(gl);
        texture2.bind(gl);
        gl.glBegin(GL2.GL_QUADS);
        gl.glTexCoord2f(0.0f, 1.0f);
        gl.glVertex3f(0.5f, 0.5f, 0.0f);
        gl.glTexCoord2f(1.0f, 1.0f);
        gl.glVertex3f(0.5f, -0.5f, 0.0f);
        gl.glTexCoord2f(1.0f, 0.0f);
        gl.glVertex3f(1.5f, -0.5f, 0.0f);
        gl.glTexCoord2f(0.0f, 0.0f);
        gl.glVertex3f(1.5f, 0.5f, 0.0f);
        gl.glEnd();

        gl.glDisable(GL.GL_TEXTURE_2D);
        x += speed;
        if (x > 1.5f) {
            x = -0.5f;
        }
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
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


    private BufferedImage loadImage(String filename) throws IOException {
        InputStream in = getClass().getResourceAsStream(filename);
        return ImageIO.read(in);
    }

    public static void main(String[] args) {
        TextureSquares program = new TextureSquares();
        program.run();
    }
}
