package com.brackeen.javagamebook.tilegame.sprites;

import com.brackeen.javagamebook.graphics.Animation;
import java.lang.reflect.Constructor;

/**
    The Player.
*/
public class Player extends Creature {

    private static final int JUMP_TIME = 1000;
    private static final float JUMP_SPEED = -.95f;
    private Animation animJumpLeft;
    private Animation animJumpRight;
    private boolean onGround;
    private boolean bIsJumping;
    private int iVidicua;
    

    public Player(Animation left, Animation right,
        Animation deadLeft, Animation deadRight, Animation jumpLeft, 
        Animation jumpRight)
    {
        
        super(left, right, deadLeft, deadRight);
        animJumpLeft = jumpLeft;
        animJumpRight = jumpRight;
    }


    public void collideHorizontal() {
        setVelocityX(0);
    }
    
    public void collideVertical() {
        // check if collided with ground
        if (getVelocityY() > 0) {
            onGround = true;
        }
        setVelocityY(0);
    }


    public void setY(float y) {
        // check if falling
        if (Math.round(y) > Math.round(getY())) {
            onGround = false;
        }
        super.setY(y);
    }


    public void wakeUp() {
        // do nothing
    }
    
    public void stand(){
        if(getVelocityX()== 0 && state!=STATE_DYING){
        move = false;
        }
        else{
        move = true;
        }
    }


    /**
        Makes the player jump if the player is on the ground or
        if forceJump is true.
    */
    public void jump(boolean forceJump) {
        if (onGround || forceJump) {
            onGround = false;
            setVelocityY(JUMP_SPEED);
            if(anim == left)
                anim = animJumpLeft;
            else if (anim == right)
                anim =animJumpRight;
            anim.update(JUMP_TIME);
        }
    }


    public float getMaxSpeed() {
        return 0.4f;
    }

    @Override
    public Object clone() {
        // use reflection to create the correct subclass
        Constructor constructor = getClass().getConstructors()[0];
        try {
            return constructor.newInstance(new Object[] {
                (Animation)left.clone(),
                (Animation)right.clone(),
                (Animation)deadLeft.clone(),
                (Animation)deadRight.clone(),
                (Animation)animJumpLeft.clone(),
                (Animation)animJumpRight.clone()
                       
            });
        }
        catch (Exception ex) {
            // should never happen
            ex.printStackTrace();
            return null;
        }
    }
    @Override
    public void update(long elapsedTime) {
        // select the correct Animation
        Animation newAnim = anim;
        if (getVelocityX() < 0) {
            newAnim = left;
        }
        else if (getVelocityX() > 0) {
            newAnim = right;
        }
        if (state == STATE_DYING && newAnim == left) {
            newAnim = deadLeft;
        }
        else if (state == STATE_DYING && newAnim == right) {
            newAnim = deadRight;
        }
       
        // update the Animation
        if (anim != newAnim ) {
            anim = newAnim;
            anim.start();
        }
        else {
        stand();
        if(move){
            anim.update(elapsedTime);
        }
        }

        // update to "dead" state
        stateTime += elapsedTime;
        if (state == STATE_DYING && stateTime >= 630) {
            setState(STATE_DEAD);
        }
    }
}
