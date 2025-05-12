package kasuga.lib.core.client.frontend.rendering;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.Hash;
import kasuga.lib.core.util.data_type.Pair;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

public class VertexBufferCache {

    protected HashMap<RenderType, BufferBuilder.RenderedBuffer> buffers;

    protected HashMap<RenderType, BufferBuilder> bufferBuilders;

    public void render(Consumer<MultiBufferSource> consumer) {
        CachedMultiBufferSource cache = new CachedMultiBufferSource(bufferBuilders);
        consumer.accept(cache);
        buffers = cache.end();
    }


    public static class CachedMultiBufferSource implements MultiBufferSource {
        private HashMap<RenderType, BufferBuilder> buffers;

        public CachedMultiBufferSource(HashMap<RenderType, BufferBuilder> buffers) {
            this.buffers = buffers;
            for (var entry : buffers.entrySet()) {
                var buffer = entry.getValue();
                buffer.begin(entry.getKey().mode(), entry.getKey().format());
            }
        }
        @Override
        public VertexConsumer getBuffer(RenderType pRenderType) {
            return buffers.computeIfAbsent(pRenderType, (r)->{
                var buffer = new BufferBuilder(r.bufferSize());
                buffer.begin(r.mode(), r.format());
                return buffer;
            });
        }

        public HashMap<RenderType, BufferBuilder.RenderedBuffer> end() {
            HashMap<RenderType, BufferBuilder.RenderedBuffer> result = new HashMap<>();
            HashSet<RenderType> cleanUp = new HashSet<>();
            for (var entry : buffers.entrySet()) {
                if(entry.getValue().isCurrentBatchEmpty()){
                    cleanUp.add(entry.getKey());
                    continue;
                }
                var buffer = entry.getValue();
                result.put(entry.getKey(), buffer.end());
            }

            for (var entry : cleanUp) {
                buffers.remove(entry);
            }

            return result;
        }
    }
}
