package kasuga.lib.core.client.frontend.common.style;

public class StyleStates {
    public enum ReadState{
        ROOT,
        PARAMETER,
        STRING,
        ESCAPE
    }

    public enum PartState{
        ATTRIBUTE,
        VALUE
    }
}
