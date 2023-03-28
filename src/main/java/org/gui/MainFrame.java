package org.gui;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.fixedfunc.GLMatrixFunc;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.Animator;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class MainFrame extends JFrame implements GLEventListener
{
    // Number of textures we want to create
    private final int NO_TEXTURES = 2;

    private int texture[] = new int[NO_TEXTURES];
    TextureReader.Texture[] tex = new TextureReader.Texture[NO_TEXTURES];

    // GLU object used for mipmapping.
    private GLU glu;

    byte mask[] = {
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x03, (byte)0x80, 0x01, (byte)0xC0, 0x06, (byte)0xC0, 0x03, 0x60,

            0x04, 0x60, 0x06, 0x20, 0x04, 0x30, 0x0C, 0x20,
            0x04, 0x18, 0x18, 0x20, 0x04, 0x0C, 0x30, 0x20,
            0x04, 0x06, 0x60, 0x20, 0x44, 0x03, (byte)0xC0, 0x22,
            0x44, 0x01, (byte)0x80, 0x22, 0x44, 0x01, (byte)0x80, 0x22,
            0x44, 0x01, (byte)0x80, 0x22, 0x44, 0x01, (byte)0x80, 0x22,
            0x44, 0x01, (byte)0x80, 0x22, 0x44, 0x01, (byte)0x80, 0x22,
            0x66, 0x01, (byte)0x80, 0x66, 0x33, 0x01, (byte)0x80, (byte)0xCC,

            0x19, (byte)0x81, (byte)0x81, (byte)0x98, 0x0C, (byte)0xC1, (byte)0x83, 0x30,
            0x07, (byte)0xe1, (byte)0x87, (byte)0xe0, 0x03, 0x3f, (byte)0xfc, (byte)0xc0,
            0x03, 0x31, (byte)0x8c, (byte)0xc0, 0x03, 0x33, (byte)0xcc, (byte)0xc0,
            0x06, 0x64, 0x26, 0x60, 0x0c, (byte)0xcc, 0x33, 0x30,
            0x18, (byte)0xcc, 0x33, 0x18, 0x10, (byte)0xc4, 0x23, 0x08,
            0x10, 0x63, (byte)0xC6, 0x08, 0x10, 0x30, 0x0c, 0x08,
            0x10, 0x18, 0x18, 0x08, 0x10, 0x00, 0x00, 0x08};
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

        // Generate a unique ID for our list.
        aCircle = gl.glGenLists(1);

        // Generate the Display List
        gl.glNewList(aCircle, GL2.GL_COMPILE);
        drawCircle(gl, 0.5f, 0.5f, 0.4f);
        gl.glEndList();

        // Create a new GLU object.
        glu = GLU.createGLU();

        // Generate a name (id) for the texture.
        // This is called once in init no matter how many textures we want to generate in the texture vector
        gl.glGenTextures(NO_TEXTURES, texture, 0);

        // Define the filters used when the texture is scaled.
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);

        // Do not forget to enable texturing.
        gl.glEnable(GL.GL_TEXTURE_2D);

        // The following lines are for creating ONE texture
        // If you want TWO textures modify NO_TEXTURES=2 and copy-paste again the next lines of code
        // up until (and including) this.makeRGBTexture(...)
        // Modify texture[0] and tex[0] to texture[1] and tex[1] in the new code and that's it

        try {
            tex[0] = TextureReader.readTexture("Texturi/textura0.jpg");
            // This line reads another image that will be used to replace a part of the previous
            tex[1] = TextureReader.readTexture("Texturi/textura1.jpg");
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        if( gl.isExtensionAvailable("GL_EXT_texture_filter_anisotropic") )
        {
            float max[] = new float[1];
            gl.glGetFloatv( GL.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT, max, 0 );

            gl.glTexParameterf( GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAX_ANISOTROPY_EXT, max[0] );
        }

        // Create a new GLU object.
        glu = GLU.createGLU();

        // Generate a name (id) for the texture.
        // This is called once in init no matter how many textures we want to generate in the texture vector
        gl.glGenTextures(NO_TEXTURES, texture, 0);

        // Bind (select) the FIRST texture.
        gl.glBindTexture(GL.GL_TEXTURE_2D, texture[0]);


        // Define the filters used when the texture is scaled.
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);

        // Construct the texture and use mipmapping in the process.
        this.makeRGBTexture(gl, glu, tex[0], GL.GL_TEXTURE_2D, true);

        // Bind (select) the SECOND texture.
        gl.glBindTexture(GL.GL_TEXTURE_2D, texture[1]);

        // Define the filters used when the texture is scaled.
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);

        // Construct the texture and use mipmapping in the process.
        this.makeRGBTexture(gl, glu, tex[1], GL.GL_TEXTURE_2D, true);

        // Do not forget to enable texturing.
        gl.glEnable(GL.GL_TEXTURE_2D);
    }

    private void makeRGBTexture(GL gl, GLU glu, TextureReader.Texture img, int target, boolean mipmapped) {
        if (mipmapped) {
            glu.gluBuild2DMipmaps(target, GL.GL_RGB8, img.getWidth(), img.getHeight(), GL.GL_RGB, GL.GL_UNSIGNED_BYTE, img.getPixels());
        } else {
            gl.glTexImage2D(target, 0, GL.GL_RGB, img.getWidth(), img.getHeight(), 0, GL.GL_RGB, GL.GL_UNSIGNED_BYTE, img.getPixels());
        }
    }

    int aCircle;

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
        gl.glLineWidth(0.5f);

        gl.glLineStipple(1, (short) 0x3F07);
        gl.glEnable(GL2.GL_LINE_STIPPLE);

        gl.glBegin(GL2.GL_POINTS);
            // Set the vertex color to Red.
            gl.glColor3f(1.0f, 0.0f, 0.0f);
            gl.glVertex2f(0.2f, 0.2f);
            // Set the vertex color to Green.
            gl.glColor3f(0.0f, 1.0f, 0.0f);
            gl.glVertex2f(0.4f, 0.2f);
            // Set the vertex color to Blue.
            gl.glColor3f(0.0f, 0.0f, 1.0f);
            gl.glVertex2f(0.2f, 0.4f);
            // Set the vertex color to White.
            gl.glColor3f(1.0f, 1.0f, 1.0f);
            gl.glVertex2f(0.4f, 0.4f);
        gl.glEnd();

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

        gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2.GL_FILL);

        // Set the polygon mask.
        gl.glPolygonStipple (mask, 0);
        // Enable polygon stipple.
        gl.glEnable (GL2.GL_POLYGON_STIPPLE);

        // Define vertices in clockwise order (back-faced).
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

        // Disable polygon stipple.
        gl.glDisable (GL2.GL_POLYGON_STIPPLE);

        // Do not render front-faced polygons.
        gl.glCullFace(GL.GL_FRONT);
        // Culling must be enabled in order to work.
        gl.glEnable(GL.GL_CULL_FACE);

        gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2.GL_FILL);

        // Define vertices in clockwise order (back-faced).
        gl.glBegin(GL2.GL_POLYGON);
            // Define normal for vertex 1
            gl.glNormal3f(0.f, 0.f, 1.f);
            gl.glColor3f(1.f, 0.f, 0.f);
            gl.glVertex2f(0.2f, 0.2f);

            // Define normal for vertex 2
            gl.glNormal3f(0.f, 0.f, 1.f);
            gl.glColor3f(0.f, 1.f, 0.f);
            gl.glVertex2f(0.2f, 0.4f);

            // Define normal for vertex 3
            gl.glNormal3f(0.f, 0.f, 1.f);
            gl.glColor3f(0.f, 0.f, 1.f);
            gl.glVertex2f(0.4f, 0.4f);

            // Define normal for vertex 4
            gl.glNormal3f(0.f, 0.f, 1.f);
            gl.glColor3f(1.f, 1.f, 1.f);
            gl.glVertex2f(0.4f, 0.2f);
        gl.glEnd();

        gl.glColor3f(1.0f, 1.0f, 1.0f);
        // Call the Display List i.e. call the commands stored in it.
        gl.glCallList(aCircle);

        // Bind (select) the texture
        gl.glBindTexture(GL.GL_TEXTURE_2D, texture[0]);

        // Draw a square and apply a texture on it.
        gl.glBegin(GL2.GL_QUADS);
        // Lower left corner.
            gl.glTexCoord2f(0.0f, 0.0f);
            gl.glVertex2f(0.1f, 0.1f);

            // Lower right corner.
            gl.glTexCoord2f(1.0f, 0.0f);
            gl.glVertex2f(0.9f, 0.1f);

            // Upper right corner.
            gl.glTexCoord2f(1.0f, 1.0f);
            gl.glVertex2f(0.9f, 0.9f);

            // Upper left corner.
            gl.glTexCoord2f(0.0f, 1.0f);
            gl.glVertex2f(0.1f, 0.9f);
        gl.glEnd();

        // Replace all of our texture with another one.
        gl.glBindTexture(GL.GL_TEXTURE_2D, texture[0]); // the pixel data for this texture is given by tex[0] in our example.
        gl.glTexSubImage2D(GL.GL_TEXTURE_2D, 0, 0, 0, tex[1].getWidth(), tex[1].getHeight(), GL.GL_RGB, GL.GL_UNSIGNED_BYTE, tex[1].getPixels());

        // Draw a square and apply a texture on it.

        // Disable blending for this texture.
        gl.glDisable(GL.GL_BLEND);

        // Bind (select) the texture
        gl.glBindTexture(GL.GL_TEXTURE_2D, texture[0]);
        // Draw a square and apply a texture on it.

        gl.glBegin(GL2.GL_QUADS);
            // Lower left corner.
            gl.glTexCoord2f(0.0f, 0.0f);
            gl.glVertex2f(0.1f, 0.1f);

            // Lower right corner.
            gl.glTexCoord2f(1.0f, 0.0f);
            gl.glVertex2f(0.9f, 0.1f);

            // Upper right corner.
            gl.glTexCoord2f(1.0f, 1.0f);
            gl.glVertex2f(0.9f, 0.9f);

            // Upper left corner.
            gl.glTexCoord2f(0.0f, 1.0f);
            gl.glVertex2f(0.1f, 0.9f);
            gl.glEnd();

            // Enable blending for this texture.
            gl.glEnable(GL.GL_BLEND);

            // Set the blend function.
            gl.glBlendFunc(GL.GL_SRC_COLOR, GL.GL_DST_ALPHA);

            // Bind (select) the texture
            gl.glBindTexture(GL.GL_TEXTURE_2D, texture[1]);

            // Draw a square and apply a texture on it.
            gl.glBegin(GL2.GL_QUADS);
            // Lower left corner.
            gl.glTexCoord2f(0.0f, 0.0f);
            gl.glVertex2f(0.1f, 0.1f);

            // Lower right corner.
            gl.glTexCoord2f(1.0f, 0.0f);
            gl.glVertex2f(0.9f, 0.1f);

            // Upper right corner.
            gl.glTexCoord2f(1.0f, 1.0f);
            gl.glVertex2f(0.9f, 0.9f);

            // Upper left corner.
            gl.glTexCoord2f(0.0f, 1.0f);
            gl.glVertex2f(0.1f, 0.9f);
        gl.glEnd();

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

    private GLCanvas canvas;
    private Animator animator;
}