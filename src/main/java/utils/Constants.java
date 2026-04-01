package utils;

import main.Game;

public class Constants {

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

    }

    public static class SkeletonConstants {
        public static final int ATTACK = 0;
        public static final int DEATH = 1;
        public static final int IDLE = 2;
        public static final int TAKE_HIT = 3;
        public static final int WALK = 4;
        public static final int SHIELD = 5;

    }

    public static class UI {
        public static class Buttons {
            public static final int B_WIDTH_DEFAULT = 144;
            public static final int B_HEIGHT_DEFAULT = 72;
            public static final int B_WIDTH = (int) (B_WIDTH_DEFAULT * Game.SCALE);
            public static final int B_HEIGHT = (int) (B_HEIGHT_DEFAULT * Game.SCALE);
        }
    }
}
