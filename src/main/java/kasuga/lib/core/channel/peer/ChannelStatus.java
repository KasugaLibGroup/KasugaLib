package kasuga.lib.core.channel.peer;

public enum ChannelStatus {
    PENDING(0x00),
    ESTABLISHED(0x01),
    CLOSED(0x02);
    private final int index;
    ChannelStatus(int index){
        this.index = index;
    }

    public static ChannelStatus fromInt(int i) {
        switch (i) {
            case 0x00:
                return PENDING;
            case 0x01:
                return ESTABLISHED;
            case 0x02:
                return CLOSED;
            default:
                return null;
        }
    }

    public int toInt() {
        return index;
    }
}
