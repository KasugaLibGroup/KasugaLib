package kasuga.lib.core.model.base;

import net.minecraft.client.renderer.FaceInfo;
import net.minecraft.core.Direction;

public enum UVCorner {
    LEFT_TOP, LEFT_DOWN, RIGHT_TOP, RIGHT_DOWN;
    public static boolean isLeft(UVCorner corner) {
        return (corner == LEFT_TOP || corner == LEFT_DOWN);
    }

    public static boolean isTop(UVCorner corner) {
        return (corner == LEFT_TOP || corner == RIGHT_TOP);
    }

    public static UVCorner getCorner(FaceInfo.VertexInfo vertexInfo, Direction direction) {
        float x = vertexInfo.xFace;
        float y = vertexInfo.yFace;
        float z = vertexInfo.zFace;
        switch (direction) {
            case DOWN, UP -> {
                if (x == FaceInfo.Constants.MAX_X) {
                    if (z == FaceInfo.Constants.MAX_Z) {
                        return LEFT_TOP;
                    } else {
                        return LEFT_DOWN;
                    }
                } else {
                    if (z == FaceInfo.Constants.MAX_Z) {
                        return RIGHT_TOP;
                    } else {
                        return RIGHT_DOWN;
                    }
                }
            }
            case NORTH -> {
                if (x == FaceInfo.Constants.MAX_X) {
                    if (y == FaceInfo.Constants.MAX_Y) {
                        return LEFT_TOP;
                    } else {
                        return LEFT_DOWN;
                    }
                } else {
                    if (y == FaceInfo.Constants.MAX_Y) {
                        return RIGHT_TOP;
                    } else {
                        return RIGHT_DOWN;
                    }
                }
            }
            case SOUTH -> {
                if (x == FaceInfo.Constants.MAX_X) {
                    if (y == FaceInfo.Constants.MAX_Y) {
                        return RIGHT_TOP;
                    } else {
                        return RIGHT_DOWN;
                    }
                } else {
                    if (y == FaceInfo.Constants.MAX_Y) {
                        return LEFT_TOP;
                    } else {
                        return LEFT_DOWN;
                    }
                }
            }
            case WEST -> {
                if (z == FaceInfo.Constants.MIN_Z) {
                    if (y == FaceInfo.Constants.MAX_Y) {
                        return LEFT_TOP;
                    } else {
                        return LEFT_DOWN;
                    }
                } else {
                    if (y == FaceInfo.Constants.MAX_Y) {
                        return RIGHT_TOP;
                    } else {
                        return RIGHT_DOWN;
                    }
                }
            }
            default -> {
                if (z == FaceInfo.Constants.MIN_Z) {
                    if (y == FaceInfo.Constants.MAX_Y) {
                        return RIGHT_TOP;
                    } else {
                        return RIGHT_DOWN;
                    }
                } else {
                    if (y == FaceInfo.Constants.MAX_Y) {
                        return LEFT_TOP;
                    } else {
                        return LEFT_DOWN;
                    }
                }
            }
        }
    }
}
