package kasuga.lib.core.client.frontend.common.interaction;

public record MouseEvent(
        float mouseX,
        float mouseY,
        int button,
        Object source
) {}
