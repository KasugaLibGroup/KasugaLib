package kasuga.lib.core.javascript.utils;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class MultipleReader extends Reader {

    Reader[] readers = {};
    AtomicInteger readerIndex = new AtomicInteger(0);

    public MultipleReader(Reader... readers){
        this.readers = readers;
    }

    @Override
    public int read(@NotNull char[] cbuf, int off, int len) throws IOException {
        int cnt = 0;
        int nextOff = off;
        int remain = len;
        while(this.readerIndex.get() < this.readers.length){
            int curCnt = this.readers[this.readerIndex.get()].read(cbuf,nextOff,remain);
            if(curCnt == -1){
                this.readerIndex.incrementAndGet();
                continue;
            }
            remain -= curCnt;
            nextOff += curCnt;
            cnt += curCnt;
            if(remain <= 0)
                break;
        }
        return (this.readerIndex.get() < this.readers.length || cnt!=0) ? cnt : -1;
    }

    @Override
    public void close() throws IOException {
        for (Reader reader : this.readers) {
            reader.close();
        }
    }
}
