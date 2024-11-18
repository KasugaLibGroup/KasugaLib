package kasuga.lib.core.channel.address;

import kasuga.lib.KasugaLib;
import net.minecraft.network.FriendlyByteBuf;

import java.util.List;
import java.util.Objects;
import java.util.Stack;

public class ConnectionInfo {
    Stack<Label> labels;
    ChannelPort port;

    public ConnectionInfo(Stack<Label> label, ChannelPort port) {
        this.labels = label;
        this.port = port;
    }

    public static ConnectionInfo of(Stack<Label> label, ChannelPort port) {
        return new ConnectionInfo(label, port);
    }


    public static ConnectionInfo of(Label label, ChannelPort port) {
        Stack<Label> labels = new Stack<>();
        labels.push(label);
        return new ConnectionInfo(labels, port);
    }

    public static ConnectionInfo of(ChannelPort port, Label ...labelList) {
        Stack<Label> labels = new Stack<>();
        labels.addAll(List.of(labelList));
        return new ConnectionInfo(labels, port);
    }

    public Stack<Label> getLabels() {
        return labels;
    }

    public Label lastAddress() {
        return labels.peek();
    }

    public Label firstAddress() {
        return labels.firstElement();
    }

    public Label address(){
        return lastAddress();
    }

    public Label popAddress() {
        return labels.pop();
    }

    public void pushAddress(Label label) {
        labels.push(label);
    }

    public ChannelPort getPort() {
        return port;
    }

    public void write(FriendlyByteBuf byteBuf){
        byteBuf.writeInt(labels.size());
        for (Label label : labels) {
            KasugaLib.STACKS.CHANNEL.labelTypeRegistry.write(label, byteBuf);
        }
        port.write(byteBuf);
    }

    public static ConnectionInfo read(FriendlyByteBuf byteBuf){
        Stack<Label> labels = new Stack<>();
        int size = byteBuf.readInt();
        for (int i = 0; i < size; i++) {
            labels.push(KasugaLib.STACKS.CHANNEL.labelTypeRegistry.read(byteBuf));
        }
        ChannelPort port = ChannelPort.read(byteBuf);
        return new ConnectionInfo(labels, port);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        ConnectionInfo that = (ConnectionInfo) object;
        return Objects.equals(labels, that.labels) && Objects.equals(port, that.port);
    }

    @Override
    public int hashCode() {
        return Objects.hash(labels, port);
    }
}
