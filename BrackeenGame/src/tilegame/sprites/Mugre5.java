package com.brackeen.javagamebook.tilegame.sprites;

import com.brackeen.javagamebook.graphics.Animation;

/**
    A Fly is a Creature that fly slowly in the air.
*/
public class Mugre5 extends Creature {

    public Mugre5(Animation left, Animation right,
        Animation deadLeft, Animation deadRight)
    {
        super(left, right, deadLeft, deadRight);
    }

    public float getMaxSpeed() {
        return 0.2f;
    }
}
