/*
        Group name: ArrayIndexOutOfBoundsException
        Group members: Freddy Gutierrez, Jose Sandoval, Ray A. Zuniga
 */
package VoxelEngine;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.util.glu.GLU;
public class VoxelEngine
{

    /**
     * @param args the command line arguments
     */
       
    private FPCameraController fp = new FPCameraController(0f,0f,0f);
    private DisplayMode displayMode;
    //method: start
    //purpose: calls all neccessary methods to create window and draaw shapes
    public void start(){
        try{
            createWindow();
            initGL();
            fp.gameLoop();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    //method: createWindow
    //purpose:  generates the window displayed to the user
    private void createWindow()throws Exception {
        Display.setFullscreen(false);
        DisplayMode d[] =Display.getAvailableDisplayModes();
        for (int i = 0; i < d.length; i++) {
            if (d[i].getWidth() == 640
            && d[i].getHeight() == 480
            && d[i].getBitsPerPixel() == 32) {
            displayMode = d[i];
            break;
            }
        }
        Display.setDisplayMode(new DisplayMode(640, 480));
        Display.setTitle("Cube");
        Display.create();
    }
    
    //method: initGL
    //purpose: uses methods in gl class for window
    private void initGL() {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        GLU.gluPerspective(100.0f, (float)displayMode.getWidth()/(float)displayMode.getHeight(), 0.1f, 300.0f);
        glMatrixMode(GL_MODELVIEW);
        glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
        glEnable(GL_DEPTH_TEST);
    }
    
     
    public static void main(String[] args) {
        VoxelEngine c = new VoxelEngine();// TODO code application logic here
        c.start();
    }
    
}
