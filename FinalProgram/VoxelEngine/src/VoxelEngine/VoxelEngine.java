/*
        Group name: ArrayIndexOutOfBoundsException
        Group members: Freddy Gutierrez, Jose Sandoval, Ray A. Zuniga
 */

package VoxelEngine;

// Imports
import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.util.glu.GLU;

public class VoxelEngine 
{
    private FPCameraController fp;
    private DisplayMode displayMode;
    private FloatBuffer lightPosition;
    private FloatBuffer whiteLight;
    
    public static void main(String[] args) 
    {
        VoxelEngine voxelEngine = new VoxelEngine();
        voxelEngine.start();
    }
    
    
    //method: start
    //purpose: calls all neccessary methods to create window and draaw shapes
    public void start()
    {
        try
        {
            createWindow();
            initGL();
            fp = new FPCameraController(0.0f,0.0f,0.0f);
            fp.gameLoop();
        }
        catch(Exception e)
        {
            System.out.println("ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    //method: createWindow
    //purpose:  generates the window displayed to the user
    private void createWindow()throws Exception 
    {
        Display.setFullscreen(false);
        DisplayMode d[] = Display.getAvailableDisplayModes();
        
        for (int i = 0; i < d.length; i++) 
        {
            if (d[i].getWidth() == 640
                    && d[i].getHeight() == 480
                    && d[i].getBitsPerPixel() == 32) 
            {
                displayMode = d[i];
                break;
            }
        }
        
        Display.setDisplayMode(new DisplayMode(640, 480));
        Display.setTitle("Final Program - Voxel Engine (Checkpoint #2)");
        Display.create();
    }
    
    //method: initGL
    //purpose: uses methods in gl class for window
    private void initGL() 
    {
        glClearColor(0.0f, 0.8f, 0.95f, 0.0f);
        
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        GLU.gluPerspective(100.0f, 
                (float)displayMode.getWidth()/(float)displayMode.getHeight(),
                0.1f, 300.0f);
        
        glMatrixMode(GL_MODELVIEW);
        glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
        glEnable(GL_DEPTH_TEST);
        
        
        // Texturing
        glEnable(GL_TEXTURE_2D);
	glEnableClientState(GL_TEXTURE_COORD_ARRAY);
        
        // Lighting
        initLightArrays();
		glLight(GL_LIGHT0, GL_POSITION, lightPosition);
		glLight(GL_LIGHT0, GL_SPECULAR, whiteLight);
		glLight(GL_LIGHT0, GL_DIFFUSE, whiteLight);
		glLight(GL_LIGHT0, GL_AMBIENT, whiteLight);
		glEnable(GL_LIGHTING);
		glEnable(GL_LIGHT0);
        
        glEnableClientState(GL_VERTEX_ARRAY);
        glEnableClientState(GL_COLOR_ARRAY);
        glEnable(GL_DEPTH_TEST);
    }    
    
    private void initLightArrays() 
    {
        lightPosition = BufferUtils.createFloatBuffer(4);
        lightPosition.put(0.0f).put(0.0f).put(0.0f).put(1.0f).flip();
        whiteLight = BufferUtils.createFloatBuffer(4);
        whiteLight.put(1.0f).put(1.0f).put(1.0f).put(0.0f).flip();
    }
}
