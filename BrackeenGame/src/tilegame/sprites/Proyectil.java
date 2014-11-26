/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package tilegame.sprites;

import com.brackeen.javagamebook.graphics.Animation;
import com.brackeen.javagamebook.graphics.Sprite;
import com.brackeen.javagamebook.tilegame.sprites.Creature;
import static com.brackeen.javagamebook.tilegame.sprites.Creature.STATE_DEAD;
import static com.brackeen.javagamebook.tilegame.sprites.Creature.STATE_DYING;
import static com.brackeen.javagamebook.tilegame.sprites.Creature.STATE_NORMAL;
import java.lang.reflect.Constructor;


/**
 *
 * @author DiegoMayorga
 */
public class Proyectil extends Sprite {
    
    /**
        Amount of time to go from STATE_DYING to STATE_DEAD.
    */
    private static final int DIE_TIME = 1000;
    public static final int STATE_NORMAL = 0;
    public static final int STATE_DYING = 1;
    public static final int STATE_DEAD = 2;
    public static final int LADO_DERECHO = 1;
    public static final int LADO_IZQUIERDO = 2;
    public static final int POWER_1 = 1;
    public static final int POWER_2 = 2;
    public static final int POWER_3 = 3;
    public boolean move;
    private int iHitPoints;
    private Animation left;
    private Animation right;
    protected int state;
    private int iLado; //1 izquierda //2 derecha
    private int iXpersonaje;
    private int iYpersonaje;
    protected long stateTime;
    private int iTipoDeProyectil;
    private Animation animPersonajeAnimacion;
    
    
    public Proyectil(Animation left, Animation right) {
        super(left);
        this.left=left;
        this.right=right;
        iLado=LADO_IZQUIERDO;
        iXpersonaje=0;
        iYpersonaje=0;
        state = STATE_NORMAL;
        iTipoDeProyectil = POWER_1;
       
        move = true;
    }
    
    public Object clone() {
        // use reflection to create the correct subclass
        Constructor constructor = getClass().getConstructors()[0];
        try {
            return constructor.newInstance(
                new Object[] {(Animation)left.clone(),
                (Animation)left.clone()});
        }
        catch (Exception ex) {
            // should never happen
            ex.printStackTrace();
            return null;
        }
    }
    /**
        Gets the maximum speed of this Creature.
    */
    public float getMaxSpeed() {
        return 0;
    }


    /**
        Wakes up the creature when the Creature first appears
        on screen. Normally, the creature starts moving left.
    */
    public void wakeUp(int xP, int yP, int iLado, int iTipoDeProyectil) {
        this.iLado=iLado;
        this.iTipoDeProyectil=iTipoDeProyectil;
        iXpersonaje=xP;
        iYpersonaje=yP;
        state=STATE_DYING;
        if(iTipoDeProyectil == POWER_2) {
            setVelocityX(-getMaxSpeed());
        } 
        if (getState() == STATE_NORMAL && getVelocityX() == 0) {
            setVelocityX(-getMaxSpeed());
        }
    }


    /**
        Gets the state of this Creature. The state is either
        STATE_NORMAL, STATE_DYING, or STATE_DEAD.
    */
    public int getState() {
        return state;
    }


    /**
        Sets the state of this Creature to STATE_NORMAL,
        STATE_DYING, or STATE_DEAD.
    */
    public void setState(int state) {
        if (this.state != state) {
            this.state = state;
            stateTime = 0;
            if (state == STATE_DYING) {
                setVelocityX(0);
                setVelocityY(0);
            }
        }
    }


    /**
        Checks if this creature is alive.
    */
    public boolean isAlive() {
        return (state == STATE_NORMAL);
    }


    /**
        Checks if this creature is flying.
    */
    public boolean isFlying() {
        return false;
    }


    /**
        Called before update() if the creature collided with a
        tile horizontally.
    */
    public void collideHorizontal() {
        setVelocityX(-getVelocityX());
    }


    /**
        Called before update() if the creature collided with a
        tile vertically.
    */
    public void collideVertical() {
        setVelocityY(0);
    }


    /**
        Updates the animaton for this creature.
    */
    public void update(long elapsedTime) {
        // select the correct Animation
        Animation newAnim = anim;
        if (state == STATE_DYING && iLado==1) {
            newAnim = left;
        }
        else if (state == STATE_DYING && iLado == 2) {
            newAnim = right;
        }
       
        // update the Animation
        if (anim != newAnim ) {
            anim = newAnim;
            anim.start();
        }
        else {
            anim.update(elapsedTime);
        }
        // update to "dead" state
        stateTime += elapsedTime;
        if (state == STATE_DYING && stateTime >= 300) {
            setState(STATE_DEAD);
        }
    }
    
    public int getHitPoints() {
        return iHitPoints;
    }
    
    public void setHitPoints(int iHP) {
        iHitPoints=iHP;
    }

}

