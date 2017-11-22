package VoxelEngine;

import java.nio.FloatBuffer;
import java.util.Random;
import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

public class Chunk
{
    static final int CHUNK_SIZE = 30;
    static final int CUBE_LENGTH = 2;
    private Block[][][] Blocks;
    private int VBOVertexHandle;
    private int VBOColorHandle;
    private int StartX, StartY, StartZ;
    private Random r;
    private int VBOTextureHandle;
    private Texture texture;
    private int smoothness = 7;
    
    public void render()
    {
        glPushMatrix();
        glPushMatrix();
        glBindBuffer(GL_ARRAY_BUFFER,
                VBOVertexHandle);
        glVertexPointer(3, GL_FLOAT, 0, 0L);
        glBindBuffer(GL_ARRAY_BUFFER,
                VBOColorHandle);
        glColorPointer(3,GL_FLOAT, 0, 0L);
        
        glBindBuffer(GL_ARRAY_BUFFER, VBOTextureHandle);
        glBindTexture(GL_TEXTURE_2D, 1);
        glTexCoordPointer(2,GL_FLOAT,0,0L);
        
        glDrawArrays(GL_QUADS, 0,
                CHUNK_SIZE *CHUNK_SIZE*
                        CHUNK_SIZE * 24);
        glPopMatrix();
    }

    public void rebuildMesh(
            float startX, float startY, float startZ) 
    {
        // VBOs
        VBOColorHandle = glGenBuffers();
        VBOVertexHandle = glGenBuffers();
        VBOTextureHandle= glGenBuffers();
        
        // Simplex Noise
        SimplexNoise simplexNoise = new SimplexNoise(30, 0.3f, r.nextInt());
        
        // Buffers
        FloatBuffer VertexPositionData =
                BufferUtils.createFloatBuffer(
                        (CHUNK_SIZE * CHUNK_SIZE *
                                CHUNK_SIZE) * 6 * 12);
        FloatBuffer VertexColorData =
                BufferUtils.createFloatBuffer(
                        (CHUNK_SIZE* CHUNK_SIZE *
                                CHUNK_SIZE) * 6 * 12);
        FloatBuffer VertexTextureData = 
                BufferUtils.createFloatBuffer(
                        (CHUNK_SIZE* CHUNK_SIZE *
                                CHUNK_SIZE)* 6 * 12);
        
        
        for (float x = 0; x < CHUNK_SIZE; x += 1) 
        {
            for (float z = 0; z < CHUNK_SIZE; z += 1) 
            { 
                // Calculate height.
                //int i = (int)(StartX + x *((EndX - StartX)/xResolution));                
                int i = (int)(startX + x *((CHUNK_SIZE - startX) / 15));
                int j = (int)(startY + (CHUNK_SIZE - 1) * ((CHUNK_SIZE - startY) / 15));
                int k = (int) (startZ + z * ((CHUNK_SIZE - startZ) / 15));
                
                float height =  (startY + (int) (smoothness
                        * simplexNoise.getNoise(i,j,k))
                        * CUBE_LENGTH);                
                
                // Add cubes to the given height.
                for(float y = 0; y < CHUNK_SIZE; y++)
                {
                    if (y <= height) 
                    {
                        VertexPositionData.put(createCube((float) (startX + x * CUBE_LENGTH),
                                (float) (y*CUBE_LENGTH+(int)(CHUNK_SIZE*.8)),
                                (float) (startZ + z * CUBE_LENGTH)));
                        VertexColorData.put(createCubeVertexCol(
                                getCubeColor(Blocks[(int) x][(int) y][(int) z])));
                        VertexTextureData.put(
                                createTexCube((float) 0, (float)0,
                                        Blocks[(int)(x)][(int) (y)][(int) (z)]));
                    }
                }
            }
        }
        
        // Flips
        VertexColorData.flip();
        VertexPositionData.flip();
        VertexTextureData.flip();
        
        // Vertex Buffer
        glBindBuffer(GL_ARRAY_BUFFER,
                VBOVertexHandle);
        glBufferData(GL_ARRAY_BUFFER,
                VertexPositionData,
                GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ARRAY_BUFFER,
                VBOColorHandle);
        glBufferData(GL_ARRAY_BUFFER,
                VertexColorData,
                GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        
        glBindBuffer(GL_ARRAY_BUFFER,
                VBOTextureHandle);
        glBufferData(GL_ARRAY_BUFFER, 
                VertexTextureData,
                   GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    private float[] createCubeVertexCol(float[] CubeColorArray) {
        float[] cubeColors = new float[CubeColorArray.length * 4 * 6];
        for (int i = 0; i < cubeColors.length; i++) {
            cubeColors[i] = CubeColorArray[i %
                    CubeColorArray.length];
        }
        return cubeColors;
    }

    public static float[] createCube(float x, float y,
                                     float z) {
        int offset = CUBE_LENGTH / 2;
        return new float[] 
        {
                // TOP QUAD
                x + offset, y + offset, z,
                x - offset, y + offset, z,
                x - offset, y + offset, z - CUBE_LENGTH,
                x + offset, y + offset, z - CUBE_LENGTH,
                // BOTTOM QUAD
                x + offset, y - offset, z - CUBE_LENGTH,
                x - offset, y - offset, z - CUBE_LENGTH,
                x - offset, y - offset, z,
                x + offset, y - offset, z,
                // FRONT QUAD
                x + offset, y + offset, z - CUBE_LENGTH,
                x - offset, y + offset, z - CUBE_LENGTH,
                x - offset, y - offset, z - CUBE_LENGTH,
                x + offset, y - offset, z - CUBE_LENGTH,
                // BACK QUAD
                x + offset, y - offset, z,
                x - offset, y - offset, z,
                x - offset, y + offset, z,
                x + offset, y + offset, z,
                // LEFT QUAD
                x - offset, y + offset, z - CUBE_LENGTH,
                x - offset, y + offset, z,
                x - offset, y - offset, z,
                x - offset, y - offset, z - CUBE_LENGTH,
                // RIGHT QUAD
                x + offset, y + offset, z,
                x + offset, y + offset, z - CUBE_LENGTH,
                x + offset, y - offset, z - CUBE_LENGTH,
                x + offset, y - offset, z };
    }

    private float[] getCubeColor(Block block) 
    {
        return new float[] { 1, 1, 1 };
    }
    
    public static float[] createTexCube(float x, float y, Block block) 
    {
        float offset = (1024f/16)/1024f;
        
        switch (block.GetID()) 
        {
            case 0: // Grass Toped Dirt
				return new float[] {
						// TOP!
						x + offset * 3, y + offset * 10,
						x + offset * 2, y + offset * 10,
						x + offset * 2, y + offset * 9,
						x + offset * 3, y + offset * 9,
						// BOTTOM QUAD(DOWN=+Y)
						x + offset * 3, y + offset * 1,
						x + offset * 2, y + offset * 1,
						x + offset * 2, y + offset * 0,
						x + offset * 3, y + offset * 0,
						// FRONT QUAD
						x + offset * 3, y + offset * 0,
						x + offset * 4, y + offset * 0,
						x + offset * 4, y + offset * 1,
						x + offset * 3, y + offset * 1,
						// BACK QUAD
						x + offset * 4, y + offset * 1,
						x + offset * 3, y + offset * 1,
						x + offset * 3, y + offset * 0,
						x + offset * 4, y + offset * 0,
						// LEFT QUAD
						x + offset * 3, y + offset * 0,
						x + offset * 4, y + offset * 0,
						x + offset * 4, y + offset * 1,
						x + offset * 3, y + offset * 1,
						// RIGHT QUAD
						x + offset * 3, y + offset * 0,
						x + offset * 4, y + offset * 0,
						x + offset * 4, y + offset * 1,
						x + offset * 3, y + offset * 1 };
			case 1: // Sand
				return new float[] {
						// TOP!
						x + offset * 3, y + offset * 2,
						x + offset * 2, y + offset * 2,
						x + offset * 2, y + offset * 1,
						x + offset * 3, y + offset * 1,
						// BOTTOM QUAD(DOWN=+Y)
						x + offset * 3, y + offset * 2,
						x + offset * 2, y + offset * 2,
						x + offset * 2, y + offset * 1,
						x + offset * 3, y + offset * 1,
						// FRONT QUAD
						x + offset * 2, y + offset * 1,
						x + offset * 3, y + offset * 1,
						x + offset * 3, y + offset * 2,
						x + offset * 2, y + offset * 2,
						// BACK QUAD
						x + offset * 3, y + offset * 2,
						x + offset * 2, y + offset * 2,
						x + offset * 2, y + offset * 1,
						x + offset * 3, y + offset * 1,
						// LEFT QUAD
						x + offset * 3, y + offset * 0,
						x + offset * 4, y + offset * 0,
						x + offset * 4, y + offset * 1,
						x + offset * 3, y + offset * 1,
						// RIGHT QUAD
						x + offset * 3, y + offset * 0,
						x + offset * 4, y + offset * 0,
						x + offset * 4, y + offset * 1,
						x + offset * 3, y + offset * 1 };
			case 2: // Water
				return new float[] {
						// TOP!
						x + offset * 1, y + offset * 10,
						x + offset * 0, y + offset * 10,
						x + offset * 0, y + offset * 9,
						x + offset * 1, y + offset * 9,
						// BOTTOM QUAD(DOWN=+Y)
						x + offset * 1, y + offset * 10,
						x + offset * 0, y + offset * 10,
						x + offset * 0, y + offset * 9,
						x + offset * 1, y + offset * 9,
						// FRONT QUAD
						x + offset * 1, y + offset * 9,
						x + offset * 0, y + offset * 9,
						x + offset * 0, y + offset * 10,
						x + offset * 1, y + offset * 10,
						// BACK QUAD
						x + offset * 0, y + offset * 10,
						x + offset * 1, y + offset * 10,
						x + offset * 1, y + offset * 9,
						x + offset * 0, y + offset * 9,
						// LEFT QUAD
						x + offset * 1, y + offset * 9,
						x + offset * 0, y + offset * 9,
						x + offset * 0, y + offset * 10,
						x + offset * 1, y + offset * 10,
						// RIGHT QUAD
						x + offset * 1, y + offset * 9,
						x + offset * 0, y + offset * 9,
						x + offset * 0, y + offset * 10,
						x + offset * 1, y + offset * 10 };
			case 3: // Dirt
				return new float[] {
						// BOTTOM QUAD(DOWN=+Y)
						x + offset * 3, y + offset * 0,
						x + offset * 2, y + offset * 0,
						x + offset * 2, y + offset * 1,
						x + offset * 3, y + offset * 1,
						// TOP!
						x + offset * 3, y + offset * 1,
						x + offset * 2, y + offset * 1,
						x + offset * 2, y + offset * 0,
						x + offset * 3, y + offset * 0,
						// FRONT QUAD
						x + offset * 3, y + offset * 0,
						x + offset * 4, y + offset * 0,
						x + offset * 4, y + offset * 1,
						x + offset * 3, y + offset * 1,
						// BACK QUAD
						x + offset * 4, y + offset * 1,
						x + offset * 3, y + offset * 1,
						x + offset * 3, y + offset * 0,
						x + offset * 4, y + offset * 0,
						// LEFT QUAD
						x + offset * 3, y + offset * 0,
						x + offset * 4, y + offset * 0,
						x + offset * 4, y + offset * 1,
						x + offset * 3, y + offset * 1,
						// RIGHT QUAD
						x + offset * 3, y + offset * 0,
						x + offset * 4, y + offset * 0,
						x + offset * 4, y + offset * 1,
						x + offset * 3, y + offset * 1 };
			case 4: // Stone
				return new float[] {
						// TOP!
						x + offset * 2, y + offset * 1,
						x + offset * 1, y + offset * 1,
						x + offset * 1, y + offset * 0,
						x + offset * 2, y + offset * 0,
						// BOTTOM QUAD(DOWN=+Y)
						x + offset * 2, y + offset * 1,
						x + offset * 1, y + offset * 1,
						x + offset * 1, y + offset * 0,
						x + offset * 2, y + offset * 0,
						// FRONT QUAD
						x + offset * 1, y + offset * 0,
						x + offset * 2, y + offset * 0,
						x + offset * 2, y + offset * 1,
						x + offset * 1, y + offset * 1,
						// BACK QUAD
						x + offset * 2, y + offset * 1,
						x + offset * 1, y + offset * 1,
						x + offset * 1, y + offset * 0,
						x + offset * 2, y + offset * 0,
						// LEFT QUAD
						x + offset * 1, y + offset * 0,
						x + offset * 2, y + offset * 0,
						x + offset * 2, y + offset * 1,
						x + offset * 1, y + offset * 1,
						// RIGHT QUAD
						x + offset * 1, y + offset * 0,
						x + offset * 2, y + offset * 0,
						x + offset * 2, y + offset * 1,
						x + offset * 1, y + offset * 1 };
			case 5: // Bedrock
				return new float[] {
						// TOP!
						x + offset * 2, y + offset * 2,
						x + offset * 1, y + offset * 2,
						x + offset * 1, y + offset * 1,
						x + offset * 2, y + offset * 1,
						// BOTTOM QUAD(DOWN=+Y)
						x + offset * 2, y + offset * 2,
						x + offset * 1, y + offset * 2,
						x + offset * 1, y + offset * 1,
						x + offset * 2, y + offset * 1,
						// FRONT QUAD
						x + offset * 1, y + offset * 1,
						x + offset * 2, y + offset * 1,
						x + offset * 2, y + offset * 2,
						x + offset * 1, y + offset * 2,
						// BACK QUAD
						x + offset * 2, y + offset * 2,
						x + offset * 1, y + offset * 2,
						x + offset * 1, y + offset * 1,
						x + offset * 2, y + offset * 1,
						// LEFT QUAD
						x + offset * 1, y + offset * 1,
						x + offset * 2, y + offset * 1,
						x + offset * 2, y + offset * 2,
						x + offset * 1, y + offset * 2,
						// RIGHT QUAD
						x + offset * 1, y + offset * 1,
						x + offset * 2, y + offset * 1,
						x + offset * 2, y + offset * 2,
						x + offset * 1, y + offset * 2 };
			default: // Lava
				return new float[] {
						// TOP!
						x + offset * 16, y + offset * 16,
						x + offset * 15, y + offset * 16,
						x + offset * 15, y + offset * 15,
						x + offset * 16, y + offset * 15,
						// BOTTOM QUAD(DOWN=+Y)
						x + offset * 16, y + offset * 16,
						x + offset * 15, y + offset * 16,
						x + offset * 15, y + offset * 15,
						x + offset * 16, y + offset * 15,
						// FRONT QUAD
						x + offset * 15, y + offset * 15,
						x + offset * 16, y + offset * 15,
						x + offset * 16, y + offset * 16,
						x + offset * 15, y + offset * 16,
						// BACK QUAD
						x + offset * 16, y + offset * 16,
						x + offset * 15, y + offset * 16,
						x + offset * 15, y + offset * 15,
						x + offset * 16, y + offset * 15,
						// LEFT QUAD
						x + offset * 15, y + offset * 15,
						x + offset * 16, y + offset * 15,
						x + offset * 16, y + offset * 16,
						x + offset * 15, y + offset * 16,
						// RIGHT QUAD
						x + offset * 15, y + offset * 15,
						x + offset * 16, y + offset * 15,
						x + offset * 16, y + offset * 16,
						x + offset * 15, y + offset * 16 };
        }
    }
    
    
    

    public Chunk(int startX, int startY, int startZ) 
    {
        
        try
        {
            texture = TextureLoader.getTexture("PNG",
                    this.getClass().getResourceAsStream("terrain.png"));
        }
        catch(Exception e)
        {
            System.out.print("ERROR: When loading texture file.");
            e.printStackTrace();
        }
        
        
        r= new Random();
        
        Blocks = new
                Block[CHUNK_SIZE][CHUNK_SIZE][CHUNK_SIZE];
        
        for (int x = 0; x < CHUNK_SIZE; x++) 
        {
            for (int y = 0; y < CHUNK_SIZE; y++) 
            {
                for (int z = 0; z < CHUNK_SIZE; z++) 
                {
                    Block.BlockType blockType;
                    
                    // Chooses the type of block AKA Texture.
                    switch(r.nextInt((6 - 0) + 1) + 0)
                    {
                        case 0:
                            blockType = Block.BlockType.BlockType_Grass;
                            break;
                        case 1:
                            blockType = Block.BlockType.BlockType_Sand;
                            break;
                        case 2:
                            blockType = Block.BlockType.BlockType_Water;
                            break;
                        case 3:
                            blockType = Block.BlockType.BlockType_Dirt;
                            break;
                        case 4:
                            blockType = Block.BlockType.BlockType_Stone;
                            break;
                        case 5:
                            blockType = Block.BlockType.BlockType_Bedrock;
                            break;
                        default:
                            blockType = Block.BlockType.BlockType_Default;
                            break;
                    }
                    
                    Blocks[x][y][z] = new Block(blockType);
                }
            }
        }
        VBOColorHandle = glGenBuffers();
        VBOVertexHandle = glGenBuffers();
        VBOTextureHandle= glGenBuffers();
        StartX = startX;
        StartY = startY;
        StartZ = startZ;
        rebuildMesh(startX, startY, startZ);
    }
}