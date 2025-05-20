package kasuga.lib.core.client.frontend.rendering;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import org.joml.Matrix4f;

import java.nio.ByteBuffer;
import java.util.*;

public class VertexBufferCache {
    // private static final BufferBuilder BUILDER = new BufferBuilder(1024 * 1024);
    HashMap<RenderType, VertexBuffer> buffers;
    MultiBufferStore multiBufferStore;
    public VertexBufferCache() {
        this.buffers = new HashMap<>();
        this.multiBufferStore = new MultiBufferStore(buffers);
    }

    public MultiBufferStore getMultiBufferStore() {
        return multiBufferStore;
    }

    public static class MultiBufferStore implements MultiBufferSource {
        HashMap<RenderType, VertexBuffer> buffers;

        public MultiBufferStore(HashMap<RenderType, VertexBuffer> buffers) {
            this.buffers = buffers;
        }

        @Override
        public VertexConsumer getBuffer(RenderType pRenderType) {
            return buffers.computeIfAbsent(pRenderType, (type) -> {
                VertexBuffer buffer = new VertexBuffer(type);
                return buffer;
            });
        }

        public void upload(Matrix4f pose, MultiBufferSource original){
            for (RenderType type : buffers.keySet()) {
                VertexBuffer buffer = buffers.get(type);
                if (buffer != null) {
                    buffer.apply(pose, original.getBuffer(type));
                }
            }
        }

        public void upload(Matrix4f pose) {
            for (Map.Entry<RenderType, VertexBuffer> entry : buffers.entrySet()) {
                VertexBuffer buffer = buffers.get(entry.getKey());
                buffer.apply(pose, entry.getKey());
            }
        }

        public void begin(){
            for (RenderType type : buffers.keySet()) {
                VertexBuffer buffer = buffers.get(type);
                if (buffer != null) {
                    buffer.reset();
                }
            }
        }

        public void gc(){
            Iterator<Map.Entry<RenderType, VertexBuffer>> iterator = buffers.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<RenderType, VertexBuffer> entry = iterator.next();
                VertexBuffer buffer = entry.getValue();
                if (buffer.isEmpty()) {
                    iterator.remove();
                }
            }
        }
    }

    public static class VertexBuffer implements VertexConsumer, BufferVertexConsumer {
        ByteBuffer buffer = ByteBuffer.allocate(16 * 1024);
        VertexFormat format;
        VertexFormatElement element;
        int elementIndex;

        int elementSize;

        int pointer = 0;

        boolean defaultColorSet = false;
        int defaultR = 0;
        int defaultG = 0;
        int defaultB = 0;
        int defaultA = 0;
        int verticies = 0;


        VertexBuffer(RenderType renderType) {
            this.format = renderType.format();
            elementSize = format.getElements().size();
            elementIndex = 0;
            element = format.getElements().get(0);
        }

        public void reset(){
            this.pointer = 0;
            this.elementIndex = 0;
            this.verticies = 0;
            this.defaultColorSet = false;
            this.defaultR = 0;
            this.defaultG = 0;
            this.defaultB = 0;
            this.defaultA = 0;
        }

        public void ensure(int next){
            if (buffer.capacity() <= pointer + next + 64) {
                ByteBuffer newBuffer = ByteBuffer.allocate(buffer.capacity() + next + 1024);
                buffer.flip();
                newBuffer.put(buffer);
                buffer = newBuffer;
            }
        }

        public boolean isEmpty(){
            return verticies == 0;
        }

        @Override
        public VertexFormatElement currentElement() {
            if (element == null) {
                throw new IllegalStateException("No current element");
            }
            return element;
        }

        @Override
        public void nextElement() {
            pointer += element.getByteSize();
            elementIndex++;
            if (elementIndex > elementSize) {
                throw new IndexOutOfBoundsException("No more elements in the format");
            }
            if(elementIndex != elementSize) {
                element = format.getElements().get(elementIndex);
            }
            if (this.defaultColorSet && this.element.getUsage() == VertexFormatElement.Usage.COLOR) {
                BufferVertexConsumer.super.color(this.defaultR, this.defaultG, this.defaultB, this.defaultA);
            }
        }

        @Override
        public void putByte(int pIndex, byte pByteValue) {
            buffer.put(this.pointer+pIndex, pByteValue);
        }

        @Override
        public void putShort(int pIndex, short pShortValue) {
            buffer.putShort(this.pointer + pIndex, pShortValue);
        }

        @Override
        public void putFloat(int pIndex, float pFloatValue) {
            buffer.putFloat(this.pointer + pIndex, pFloatValue);
        }

        @Override
        public void endVertex() {
            this.element = format.getElements().get(0);
            this.elementIndex = 0;
            this.verticies++;

            ensure(format.getVertexSize());
        }

        @Override
        public void defaultColor(int pDefaultR, int pDefaultG, int pDefaultB, int pDefaultA) {
            this.defaultR = pDefaultR;
            this.defaultG = pDefaultG;
            this.defaultB = pDefaultB;
            this.defaultA = pDefaultA;
            this.defaultColorSet = true;
            if (this.element.getUsage() == VertexFormatElement.Usage.COLOR) {
                BufferVertexConsumer.super.color(this.defaultR, this.defaultG, this.defaultB, this.defaultA);
            }
        }

        @Override
        public void unsetDefaultColor() {
            this.defaultColorSet = false;
            this.defaultR = 0;
            this.defaultG = 0;
            this.defaultB = 0;
            this.defaultA = 0;
        }

        public void apply(Matrix4f matrix4f, VertexConsumer consumer){
            buffer.position(0);
            for(int i = 0; i < verticies; i++){
                for(VertexFormatElement element : format.getElements()){
                    switch (element.getUsage()){
                        case POSITION -> consumer.vertex(matrix4f, buffer.getFloat(), buffer.getFloat(), buffer.getFloat());
                        case UV -> {
                            switch (element.getIndex()) {
                                case 0 -> consumer.uv(buffer.getFloat(), buffer.getFloat());
                                case 1 -> consumer.overlayCoords(buffer.getShort(), buffer.getShort());
                                case 2 -> consumer.uv2(buffer.getShort(), buffer.getShort());
                            }
                        }
                        case COLOR -> {
                            byte colorR =  buffer.get();
                            byte colorG = buffer.get();
                            byte colorB = buffer.get();
                            byte colorA = buffer.get();
                            consumer.color((int) colorR & 0xFF, (int) colorG & 0xFF, (int) colorB & 0xFF, (int) colorA & 0xFF);
                        }
                        case NORMAL -> consumer.normal(buffer.getFloat(), buffer.getFloat(), buffer.getFloat());
                        default -> throw new IllegalStateException("Unexpected value: " + element.getUsage());
                    }
                }
                consumer.endVertex();
            }
        }

        public void apply(Matrix4f matrix4f, RenderType type){
            RenderSystem.assertOnRenderThread();
            BufferBuilder buffer = Tesselator.getInstance().getBuilder();
            buffer.begin(type.mode(), type.format());
            apply(matrix4f, buffer);
            type.end(buffer, VertexSorting.byDistance(0,0,0));
        }
    }
}
