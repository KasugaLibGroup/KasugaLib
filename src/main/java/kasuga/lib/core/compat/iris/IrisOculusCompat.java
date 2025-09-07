package kasuga.lib.core.compat.iris;

public interface IrisOculusCompat {
    boolean isRenderingShadow();

    void pushExtendedVertexFormat(boolean newValue);

    void popExtendedVertexFormat();
}
