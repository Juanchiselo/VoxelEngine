/***************************************************************
* file: Block.java
* author: F. Gutierrez, R. Zuniga, J. Sandoval
* class: CS 445 – Computer Graphics 
*
* assignment: Quarter Project
* date last modified: 11/21/2017
*
* purpose: Create block type
*
****************************************************************/ 
package checkone;

/**
 *
 * @author mrfre
 */
public class Block {
    
    private boolean IsActive;
    private BlockType Type;
    private float x,y,z;

    public enum BlockType
    {
        BlockType_Grass(0),
        BlockType_Sand(1),
        BlockType_Water(2),
        BlockType_Dirt(3),
        BlockType_Stone(4),
        BlockType_Bedrock(5),
        BlockType_Default(6);

        private int BlockID;
        BlockType(int i)
        {
            BlockID=i;
        }
        public int GetID(){
            return BlockID;
        }
        public void SetID(int i){
            BlockID = i;
        }
    }

    public Block(BlockType type){
        Type= type;
    }
    public void setCoords(float x, float y, float z){
        this.x = x;
        this.y = y;
        this.z = z;
    }
    public boolean IsActive() {
        return IsActive;
    }
    public void SetActive(boolean active){
        IsActive=active;
    }
    public int GetID(){
        return Type.GetID();
    }
}
