package utils;

import main.Game;

/**
 * The Constants class provides static constants for various game elements
 * such as animation states and UI dimensions.
 */
public class Constants {

    /** Animation state constants for the Player entity. */
    public static class PlayerConstants {
        public static final int RUN = 0;
        public static final int IDLE = 1;
        public static final int JUMP = 2;
        public static final int UP_TO_FALL = 3;
        public static final int FALL = 4;
        public static final int CROUCH = 5;
        public static final int HURT = 6;
        public static final int ATTACK = 7;
        public static final int DASH_ATTACK = 8;
        public static final int DEATH = 9;
        public static final int SLIDE = 10;
        public static final int DASH = 11;
    }

    /** Animation state constants for the Knight ally. */
    public static class KnightConstants {
        public static final int IDLE = 0;
        public static final int RUN = 1;
        public static final int ATTACK_1 = 2;
        public static final int ATTACK_2 = 3;
        public static final int ATTACK_3 = 4;
        public static final int DEATH = 5;
        public static final int JUMP = 6;
        public static final int HURT = 7;
    }

    /** Animation state constants for the Archer ally. */
    public static class ArcherConstants {
        public static final int DEATH = 0;
        public static final int DASH = 1;
        public static final int ATTACK = 2;
        public static final int CLOSE_ATTACK = 3;
        public static final int IDLE = 4;
        public static final int JUMP = 5;
        public static final int HURT = 6;
        public static final int RUN = 7;
        public static final int JUMPING = 8;
        public static final int UP_TO_FALL = 9;
        public static final int FALL = 10;
        public static final int CROUCH = 11;
    }

    /** Animation state constants for the Skeleton enemy. */
    public static class SkeletonConstants {
        public static final int ATTACK = 0;
        public static final int DEATH = 1;
        public static final int IDLE = 2;
        public static final int TAKE_HIT = 3;
        public static final int WALK = 4;
        public static final int SHIELD = 5;
    }

    /** Animation state constants for Goblin and Mushroom enemies. */
    public static class GoblinAndMushroomConstants {
        public static final int TAKE_HIT = 0;
        public static final int DEATH = 1;
        public static final int IDLE = 2;
        public static final int RUN = 3;
        public static final int ATTACK = 4;
    }

    /** Animation state constants for the Flying Eye enemy. */
    public static class FlyingEyeConstants {
        public static final int ATTACK = 0;
        public static final int FLIGHT = 1;
        public static final int DEATH = 2;
        public static final int TAKE_HIT = 3;
    }

    /** Animation state constants for the NightBorne enemy. */
    public static class NightBorneConstants {
        public static final int ATTACK = 0;
        public static final int DEATH = 1;
        public static final int IDLE = 2;
        public static final int HURT = 3;
        public static final int RUN = 4;
    }

    /** Animation state constants for the DarkKnight enemy. */
    public static class DarkKnightConstants {
        public static final int ATTACK = 0;
        public static final int DEATH = 1;
        public static final int IDLE = 2;
        public static final int HURT = 3;
        public static final int RUN = 4;
        public static final int JUMP = 5;
        public static final int UP_TO_FALL = 6;
        public static final int FALL = 7;
        public static final int CROUCH = 8;
    }

    /** UI-related constants. */
    public static class UI {
        /** Button dimension constants. */
        public static class Buttons {
            public static final int B_WIDTH_DEFAULT = 144;
            public static final int B_HEIGHT_DEFAULT = 72;
            public static final int B_WIDTH = (int) (B_WIDTH_DEFAULT * Game.SCALE);
            public static final int B_HEIGHT = (int) (B_HEIGHT_DEFAULT * Game.SCALE);
        }
    }
}
