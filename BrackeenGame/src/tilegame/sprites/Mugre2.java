package com.brackeen.javagamebook.tilegame.sprites;

import com.brackeen.javagamebook.graphics.Animation;

/**
    A Grub is a Creature that moves slowly on the ground.
*/
public class Mugre2 extends Creature {

    public Mugre2(Animation left, Animation right,
        Animation deadLeft, Animation deadRight, Animation hurtLeft,
        Animation hurtRight)
    {
        super(left, right, deadLeft, deadRight, hurtLeft, hurtRight);
    }


    public float getMaxSpeed() {
        return 0.05f;
    }

}
