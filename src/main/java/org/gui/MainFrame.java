package org.gui;

import astro.PolarProjectionMap;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.fixedfunc.GLMatrixFunc;
import com.jogamp.opengl.util.Animator;
import com.jogamp.opengl.util.gl2.GLUT;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class MainFrame extends JFrame implements GLEventListener
{
    private GLUT glut;
    // Holds a reference to the PolarProjectionMap object.
    private astro.PolarProjectionMap ppm = null;
    // Used to identify the display list.
    private int ppm_list;

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
        GL2 gl = canvas.getGL().getGL2();
        // Create a new GLU object.
        glut = new GLUT();

        // Initialize the object.
        this.ppm = new astro.PolarProjectionMap(21.53, 45.17);
        // Set the separator for the line fields.
        this.ppm.setFileSep(",");
        // Read the file and compute the coordinates.
        this.ppm.initializeConstellationLines("data/conlines.dat");
        // Initialize here the rest of the elements from the remaining files using the corresponding methods.

        // Create the display list.
        this.ppm_list = gl.glGenLists(1);
        gl.glNewList(this.ppm_list, GL2.GL_COMPILE);
        this.makePPM(gl);
        gl.glEndList();
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

        // Specify the raster position.
        gl.glRasterPos2d(0.5, 0.5);
        // Render the text in the scene.
        glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, "Hello World");

        gl.glCallList(this.ppm_list);
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

        double x, y, angle;

        gl.glBegin(GL2.GL_LINE_LOOP);
        for (int i = 0; i < 360; i++) {
            angle = Math.toRadians(i);
            x = radius * Math.cos(angle);
            y = radius * Math.sin(angle);
            gl.glVertex2d(xCenter + x, yCenter + y);
        }
        gl.glEnd();
    }

    // We use this method for creating the display list.
    private void makePPM(GL2 gl) {
        final ArrayList<PolarProjectionMap.ConstellationLine> clLines = this.ppm.getConLines();
        // Add here the rest of the ArrayLists.

        gl.glColor3f(0.0f, 1.0f, 0.0f);

        gl.glBegin(GL2.GL_LINES);
        for (PolarProjectionMap.ConstellationLine cl : clLines) {
            if (cl.isVisible()) {
                gl.glVertex2d(cl.getPosX1(), cl.getPosY1());
                gl.glVertex2d(cl.getPosX2(), cl.getPosY2());
            }
        }
        gl.glEnd();

        // Add here the rest of the code for rendering constellation boundaries (use GL_LINES),
        // names (use glutBitmapString), stars (use GL_POINTS) and cardinal points (use glutBitmapString).
    }

    private GLCanvas canvas;
    private Animator animator;
}