package kasuga.lib.core.client.gui.context;

public record MouseEvent(
        float mouseX,
        float mouseY,
        int button,
        Object source
) {}
