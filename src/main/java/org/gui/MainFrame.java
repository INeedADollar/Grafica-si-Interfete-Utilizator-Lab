package org.gui;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame implements GLEventListener
{

    private GLCanvas canvas;
    private FPSAnimator animator;
    int sun;
    int sun_x = 5;

    public MainFrame()
    {
        super("Java OpenGL");

        this.setLayout(new BorderLayout());
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.setSize(800, 600);

        // This method will be explained later
        this.initializeJogl();

        this.setVisible(true);
    }

    private void initializeJogl()
    {
        // Obtaining a reference to the default GL profile
        GLProfile glProfile = GLProfile.getDefault();
        // Creating an object to manipulate OpenGL parameters.
        GLCapabilities capabilities = new GLCapabilities(glProfile);

        // Setting some OpenGL parameters.
        capabilities.setHardwareAccelerated(true);
        capabilities.setDoubleBuffered(true);

        // Creating an OpenGL display widget -- canvas.
        this.canvas = new GLCanvas();

        // Adding the canvas in the center of the frame.
        this.getContentPane().add(this.canvas);

        // Adding an OpenGL event listener to the canvas.
        this.canvas.addGLEventListener(this);

        this.animator = new FPSAnimator(this.canvas, 400, true);

        //this.animator.add(this.canvas);

        this.animator.start();
    }

    public void init(GLAutoDrawable canvas)
    {
        GL2 gl = canvas.getGL().getGL2();

        gl.glClearColor(0f, 0f, 1f, 1f);
        gl.glOrtho(-10f, 10f, -10f, 10f, -10f, 10f);

        sun = gl.glGenLists(1);
        gl.glNewList(sun, GL2.GL_COMPILE);
            drawCircle(gl, 5f, 5f, 1f);
        gl.glEndList();
    }

    @Override
    public void dispose(GLAutoDrawable glAutoDrawable) {

    }

    public void display(GLAutoDrawable canvas)
    {
        GL2 gl = canvas.getGL().getGL2();

        drawChessTable(gl);
        gl.glFlush();

        if(sun_x == 4) {
            sun_x = -4;
        }
        else {
            sun_x += 0.5;
        }
    }

    public void reshape(GLAutoDrawable canvas, int left, int top, int width, int height) {

    }

    private void drawCircle(GL2 gl, float xCenter, float yCenter, float radius) {
        double x,y, angle;

        gl.glBegin(GL2.GL_LINE_LOOP);
        for (int i=0; i<360; i++) {
            angle = Math.toRadians(i);
            x = radius * Math.cos(angle);
            y = radius * Math.sin(angle);
            gl.glVertex2d(xCenter + x, yCenter + y);
        }
        gl.glEnd();
    }

    private void drawHouse(GL2 gl) {
        gl.glColor3f(0.82f, 0.66f, 0.91f);
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);

        gl.glBegin(GL2.GL_POLYGON);
        gl.glVertex2f(0f, 3f);
        gl.glVertex2f(-2f, 1f);
        gl.glVertex2f(2f, 1f);
        gl.glEnd();

        gl.glColor3f(0.25f, 0.45f, 0.91f);
        gl.glBegin(GL2.GL_POLYGON);
        gl.glVertex2f(1.5f, 1f);
        gl.glVertex2f(-1.5f, 1f);
        gl.glVertex2f(-1.5f, -2f);
        gl.glVertex2f(1.5f, -2f);
        gl.glEnd();

        gl.glColor3f(0.02f, 0.45f, 0.91f);
        gl.glBegin(GL2.GL_POLYGON);
        gl.glVertex2f(1.75f, -2f);
        gl.glVertex2f(-1.75f, -2f);
        gl.glVertex2f(-1.75f, -2.25f);
        gl.glVertex2f(1.75f, -2.25f);
        gl.glEnd();

        gl.glColor3f(0.5f, 0.66f, 0.91f);
        gl.glBegin(GL2.GL_POLYGON);
        gl.glVertex2f(0.5f, -.25f);
        gl.glVertex2f(-0.5f, -.25f);
        gl.glVertex2f(-0.5f, -2f);
        gl.glVertex2f(0.5f, -2f);
        gl.glEnd();

        gl.glColor3f(0.21f, 0.33f, 0.71f);
        gl.glBegin(GL2.GL_LINES);
        gl.glVertex2f(0f, -.25f);
        gl.glVertex2f(0f, -2f);
        gl.glEnd();

        gl.glColor3f(1.0f, 1.0f, 1.0f);
        gl.glCallList(sun);
    }

    private void drawChessTable(GL2 gl) {
        gl.glCullFace(GL.GL_FRONT);
        gl.glEnable(GL.GL_CULL_FACE);

        gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2.GL_FILL);
        for(int i = -5; i < 5; i++) {
            for(int j = -5; j < 5; j++) {
                gl.glBegin(GL2.GL_POLYGON);
                    gl.glVertex2f(i, i + 1);
                    gl.glVertex2f(i, j);
                    gl.glVertex2f(i + 1, j);
                    gl.glVertex2f(i, j + 1);
                gl.glEnd();
            }
        }
    }
}