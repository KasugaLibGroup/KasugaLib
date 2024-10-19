package kasuga.lib.core.client.model.model_json;

import com.mojang.math.Vector3f;
import kasuga.lib.core.client.render.texture.Vec2f;
import kasuga.lib.core.util.data_type.Pair;
import net.minecraft.core.Direction;

import java.util.HashMap;

public class BoxLayerProcessor {
    private final boolean mirror, visible, emissive, flipV;
    private final Vec2f textOffset;
    private final HashMap<Direction, Vec2f> faceAndSize;
    private final HashMap<Direction, Vec2f> positions;
    private final Vec2f textureSize;

    public BoxLayerProcessor(Cube cube, Vec2f textOffset, boolean flipV) {
        this.flipV = flipV;
        this.mirror = cube.mirror;
        this.visible = cube.visible;
        this.emissive = cube.emissive;
        this.textOffset = textOffset;
        faceAndSize = new HashMap<>();
        positions = new HashMap<>();
        for (Direction d : Direction.values()) {
            faceAndSize.put(d, flatten(getPositionPair(cube, d)));
        }
        generatePositions();
        GeometryDescription description = cube.getDescription();
        textureSize = new Vec2f(description.getTextureWidth(), description.getTextureHeight());
    }

    private void generatePositions() {
        float we_width = faceAndSize.get(Direction.WEST).x();
        float ud_width = faceAndSize.get(Direction.UP).x();
        float ud_height = faceAndSize.get(Direction.UP).y();
        float ns_width = faceAndSize.get(Direction.NORTH).x();

        positions.put(Direction.UP, textOffset.add(we_width, 0));
        positions.put(Direction.DOWN, textOffset.add(we_width + ud_width, 0));
        positions.put(Direction.NORTH, textOffset.add(we_width, ud_height));
        positions.put(Direction.SOUTH, textOffset.add(2 * we_width + ns_width, ud_height));
        positions.put(mirror ? Direction.WEST : Direction.EAST, textOffset.add(0, ud_height));
        positions.put(mirror ? Direction.EAST : Direction.WEST, textOffset.add(we_width + ns_width, ud_height));
    }

    public UnbakedUV getUV(Direction direction) {
        boolean shouldMirror = mirror && !(direction == Direction.EAST || direction == Direction.WEST);
        return new UnbakedUV(direction, positions.get(direction), faceAndSize.get(direction),
                textureSize.x(), textureSize.y(), shouldMirror, flipV, visible, emissive);
    }

    public static Direction getSide(Cube cube, Vector3f pos1, Vector3f pos2) {
        Vector3f org = cube.getOrigin(),
                max = org.copy();
        max.add(cube.getSize());
        if (pos1.y() == max.y() && pos2.y() == max.y()) {
            return Direction.UP;
        } else if (pos1.y() == org.y() && pos2.y() == org.y()) {
            return Direction.DOWN;
        } else if (pos1.x() == max.x() && pos2.x() == max.x()) {
            return Direction.EAST;
        } else if (pos1.x() == org.x() && pos2.x() == org.x()) {
            return Direction.WEST;
        } else if (pos1.z() == max.z() && pos2.z() == max.z()) {
            return Direction.SOUTH;
        } else if (pos1.z() == org.z() && pos2.z() == org.z()) {
            return Direction.NORTH;
        }
        return null;
    }

    public static Direction getSide(Cube cube, Pair<Vector3f, Vector3f> pair) {
        return getSide(cube, pair.getFirst(), pair.getSecond());
    }

    public static Pair<Vector3f, Vector3f> getPositionPair(Cube cube, Direction direction) {
        Vector3f org = cube.getOrigin();
        Vector3f max = org.copy();
        max.add(cube.getSize());

        return switch (direction) {
            case UP -> Pair.of(new Vector3f(org.x(), max.y(), org.z()), max.copy());
            case DOWN -> Pair.of(org.copy(), new Vector3f(max.x(), org.y(), max.z()));
            case EAST -> Pair.of(new Vector3f(max.x(), org.y(), org.z()), max.copy());
            case WEST -> Pair.of(org.copy(), new Vector3f(org.x(), max.y(), max.z()));
            case NORTH -> Pair.of(org.copy(), new Vector3f(max.x(), max.y(), org.z()));
            case SOUTH -> Pair.of(new Vector3f(org.x(), org.y(), max.z()), max.copy());
        };
    }

    public static Vec2f flatten(Vector3f pos1, Vector3f pos2) {
        if (pos1.y() - pos2.y() == 0) {
            return new Vec2f(pos2.x() - pos1.x(), pos2.z() - pos1.z());
        } else if (pos1.z() - pos2.z() == 0) {
            return new Vec2f(pos2.x() - pos1.x(), pos2.y() - pos1.y());
        } else if (pos1.x() - pos2.x() == 0) {
            return new Vec2f(pos2.z() - pos1.z(), pos2.y() - pos1.y());
        }
        return Vec2f.ZERO;
    }

    public static Vec2f flatten(Pair<Vector3f, Vector3f> pair) {
        return flatten(pair.getFirst(), pair.getSecond());
    }
}
