package kasuga.lib.core.sync;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;
import java.util.function.Function;

public class DataSynchronizer {
    protected ArrayList<DataState> states = new ArrayList<>();
    protected ArrayList<Function<Integer, DataState>> stateSuppliers = new ArrayList<>();
    protected HashMap<Object, ArrayList<DataState>> remoteStates = new HashMap<>();
    protected HashMap<Object, Consumer<CompoundTag>> remoteConsumers = new HashMap<>();
    
    public void addRemote(Object object, Consumer<CompoundTag> tagConsumer) {
        remoteConsumers.put(object, tagConsumer);
        ArrayList<DataState> remote = new ArrayList<>();
        for (int i = 0; i < states.size(); i++) {
            remote.add(stateSuppliers.get(i).apply(i));
        }
        remoteStates.put(object, remote);
        
        for (int i = 0; i < states.size(); i++) {
            sendStateDiff(object, i);
        }
    }
    
    public void removeRemote(Object object) {
        remoteConsumers.remove(object);
        remoteStates.remove(object);
    }
    
    public void registerState(DataState initialState, Function<Integer, DataState> dataSupplier) {
        int newId = states.size();
        states.add(initialState);
        stateSuppliers.add(dataSupplier);
        
        for (Object remote : remoteStates.keySet()) {
            ArrayList<DataState> remoteStateList = remoteStates.get(remote);
            remoteStateList.add(dataSupplier.apply(newId));
        }
        updateState(newId, initialState);
    }
    
    public void updateState(int stateId, DataState newState) {
        if (stateId >= 0 && stateId < states.size()) {
            DataState oldState = states.get(stateId);
            states.set(stateId, newState);
            
            for (Object remote : remoteConsumers.keySet()) {
                sendStateDiff(remote, stateId);
            }
        }
    }
    
    private void sendStateDiff(Object remote, int stateId) {
        ListTag diffList = new ListTag();
        DataState localState = states.get(stateId);
        DataState remoteState = remoteStates.get(remote).get(stateId);
        CompoundTag diff = localState.diff(remoteState);
        if (diff != null) {
            diff.putInt("index", stateId);
            diffList.add(diff);
        }
        
        if (!diffList.isEmpty()) {
            CompoundTag packet = new CompoundTag();
            packet.put("diffs", diffList);
            remoteConsumers.get(remote).accept(packet);
        }
    }
    
    public void handleRemoteUpdate(Object sender, CompoundTag packet) {
        ListTag diffs = packet.getList("diffs", Tag.TAG_COMPOUND);
        for (int i = 0; i < diffs.size(); i++) {
            CompoundTag diffTag = diffs.getCompound(i);
            int stateId = diffTag.getInt("index");
        
            if (stateId >= 0 && stateId < states.size()) {
                DataState state = states.get(stateId);
                state.applyDiff(diffTag);
            }
        }
    }
    
    public DataState getRemoteState(Object sender, int stateId) {
        ArrayList<DataState> senderStates = remoteStates.get(sender);
        if (senderStates != null && stateId < senderStates.size()) {
            return senderStates.get(stateId);
        }
        return null;
    }
    
    public void syncAllStates(Object remote) {
        ListTag diffList = new ListTag();
        
        for (int i = 0; i < states.size(); i++) {
            DataState localState = states.get(i);
            DataState remoteState = remoteStates.get(remote).get(i);

            CompoundTag diff = localState.diff(remoteState);
            if (diff != null) {
                diff.putInt("index", i);
                diffList.add(diff);
            }
        }
        
        if (!diffList.isEmpty()) {
            CompoundTag packet = new CompoundTag();
            packet.put("diffs", diffList);
            remoteConsumers.get(remote).accept(packet);
        }
    }
}
