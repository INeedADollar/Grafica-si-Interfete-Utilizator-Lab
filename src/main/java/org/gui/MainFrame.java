package org.gui;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.fixedfunc.GLMatrixFunc;
import com.jogamp.opengl.util.Animator;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame implements GLEventListener
{
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

        this.animator = new Animator();

        this.animator.add(this.canvas);

        this.animator.start();
    }

    public void init(GLAutoDrawable canvas)
    {
        // Obtain the GL instance associated with the canvas.
        GL2 gl = canvas.getGL().getGL2();

        // Set the clear color -- the color which will be used to reset the color buffer.
        gl.glClearColor(0, 0, 0, 0);

        // Select the Projection matrix.
        gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);

        // Clear the projection matrix and set it to be the identity matrix.
        gl.glLoadIdentity();

        // Set the projection to be orthographic.
        // It could have been as well chosen to be perspective.
        // Select the view volume to be x in the range of 0 to 1, y from 0 to 1 and z from -1 to 1.
        gl.glOrtho(0, getBounds().width, 0, getBounds().height, -1, 1);

        // Activate the GL_LINE_SMOOTH state variable. Other options include
        // GL_POINT_SMOOTH and GL_POLYGON_SMOOTH.
        gl.glEnable(GL.GL_LINE_SMOOTH);

        // Activate the GL_BLEND state variable. Means activating blending.
        gl.glEnable(GL.GL_BLEND);

        // Set the blend function. For antialiasing it is set to GL_SRC_ALPHA for the source
        // and GL_ONE_MINUS_SRC_ALPHA for the destination pixel.
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);

        // Control GL_LINE_SMOOTH_HINT by applying the GL_DONT_CARE behavior.
        // Other behaviours include GL_FASTEST or GL_NICEST.
        gl.glHint(GL.GL_LINE_SMOOTH_HINT, GL.GL_DONT_CARE);
        // Uncomment the following two lines in case of polygon antialiasing
        //gl.glEnable(GL.GL_POLYGON_SMOOTH);
        //glHint(GL_POLYGON_SMOOTH_HINT, GL_NICEST);
    }

    @Override
    public void dispose(GLAutoDrawable glAutoDrawable) {

    }

    public void display(GLAutoDrawable canvas)
    {
        GL2 gl = canvas.getGL().getGL2();

        // Each time the scene is redrawn we clear the color buffers which is perceived by the user as clearing the scene.

        // Set the color buffer to be filled with the color black when cleared.
        // It can be defined in the init function (method) also.
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        // Clear the color buffer.
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);

        // Set the size of the point
        gl.glPointSize(0.5f);

        // Set the width of the lines
        //gl.glLineWidth(0.5f);

        gl.glLineWidth(1.5f);

        gl.glColor3f(1.f, 0.f, 0.f);
        gl.glBegin(GL2.GL_LINES);
            gl.glVertex2f(0.2f, 0.2f);
            gl.glVertex2f(0.9f, 0.9f);
        gl.glEnd();

        gl.glColor3f(0.f, 1.f, 0.f);
        gl.glBegin(GL2.GL_LINES);
            gl.glVertex2f(0.9f, 0.2f);
            gl.glVertex2f(0.2f, 0.9f);
        gl.glEnd();

        gl.glBegin(GL2.GL_POLYGON);
            gl.glColor3f(1.f, 0.f, 0.f);
            gl.glVertex2f(0.2f, 0.2f);
            gl.glColor3f(0.f, 1.f, 0.f);
            gl.glVertex2f(0.2f, 0.4f);
            gl.glColor3f(0.f, 0.f, 1.f);
            gl.glVertex2f(0.4f, 0.4f);
            gl.glColor3f(1.f, 1.f, 1.f);
            gl.glVertex2f(0.4f, 0.2f);
        gl.glEnd();

        // Do not render front-faced polygons.
        gl.glCullFace(GL.GL_FRONT);
        // Culling must be enabled in order to work.
        gl.glEnable(GL.GL_CULL_FACE);

        gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2.GL_FILL);

        // Define vertices in clockwise order (back-faced)
        gl.glBegin(GL2.GL_POLYGON);
            gl.glColor3f(1.f, 0.f, 0.f);
            gl.glVertex2f(0.2f, 0.2f);
            gl.glColor3f(0.f, 1.f, 0.f);
            gl.glVertex2f(0.2f, 0.4f);
            gl.glColor3f(0.f, 0.f, 1.f);
            gl.glVertex2f(0.4f, 0.4f);
            gl.glColor3f(1.f, 1.f, 1.f);
            gl.glVertex2f(0.4f, 0.2f);
        gl.glEnd();

        // Generate a unique ID for our list.
        aCircle = gl.glGenLists(1);

        // Generate the Display List
        gl.glNewList(aCircle, GL2.GL_COMPILE);
            drawCircle(gl, 0.5f, 0.5f, 0.4f);
        gl.glEndList();

        gl.glColor3f(1.0f, 1.0f, 1.0f);
        // Call the Display List i.e. call the commands stored in it.
        gl.glCallList(aCircle);

        gl.glFlush();
    }

    public void reshape(GLAutoDrawable canvas, int left, int top, int width, int height)
    {
        GL2 gl = canvas.getGL().getGL2();

        // Select the viewport -- the display area -- to be the entire widget.
        gl.glViewport(0, 0, width, height);

        // Determine the width to height ratio of the widget.
        double ratio = (double) width / (double) height;

        // Select the Projection matrix.
        gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);

        gl.glLoadIdentity();

        // Select the view volume to be x in the range of 0 to 1, y from 0 to 1 and z from -1 to 1.
        // We are careful to keep the aspect ratio and enlarging the width or the height.
        if (ratio < 1)
            gl.glOrtho(0, 1, 0, 1 / ratio, -1, 1);
        else
            gl.glOrtho(0, 1 * ratio, 0, 1, -1, 1);

        // Return to the Modelview matrix.
        gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
    }

    // Here we define the function for building a circle from line segments.
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

    private GLCanvas canvas;
    private Animator animator;
    int aCircle;
}
