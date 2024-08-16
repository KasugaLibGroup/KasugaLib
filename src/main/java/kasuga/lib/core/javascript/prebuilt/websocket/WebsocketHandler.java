package kasuga.lib.core.javascript.prebuilt.websocket;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketClientCompressionHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.util.CharsetUtil;

import javax.net.ssl.SSLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class WebsocketHandler extends SimpleChannelInboundHandler<Object> {
    private final String url;
    private Channel channel;

    private WebSocketClientHandshaker handshaker;
    private EventLoopGroup eventLoopGroup;

    boolean isClosed = false;

    WebsocketHandler(String url){
        this.url = url;
        this.open();
    }

    private void open() {
        URI uri;
        try{
            uri = new URI(this.url);
        }catch (URISyntaxException e){
            throw new IllegalArgumentException("Illegal URL",e);
        }
        final String protocol = uri.getScheme().toLowerCase();
        if(!protocol.equals("ws") && !protocol.equals("wss")){
            throw new IllegalArgumentException("Illegal URL: Invalid protocol "+protocol+ ", expected ws or wss");
        }
        int port = uri.getPort();
        if( port == -1 ){
            switch (protocol){
                case "ws"  -> port = 80;
                case "wss" -> port = 443;
            }
        }
        SslContext ssl = null;
        try{
            if(protocol.equals("wss")){
                ssl = SslContextBuilder.forClient()
                        .trustManager(InsecureTrustManagerFactory.INSTANCE)
                        .build();
            }
        }catch (SSLException e){
            throw new RuntimeException("Failed to build SSL context",e);
        }

        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        this.eventLoopGroup = eventLoopGroup;
        Bootstrap bootstrap = new Bootstrap();
        int finalPort = port;
        String host = uri.getHost();
        SslContext finalSsl = ssl;
        SimpleChannelInboundHandler<Object> that = this;
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ChannelPipeline p = ch.pipeline();
                        if (finalSsl != null) {
                            p.addLast(finalSsl.newHandler(ch.alloc(), host, finalPort));
                        }
                        p.addLast(
                                new HttpClientCodec(),
                                new HttpObjectAggregator(8192),
                                WebSocketClientCompressionHandler.INSTANCE,
                                that);
                    }
                });
        this.handshaker = WebSocketClientHandshakerFactory.newHandshaker(
                uri, WebSocketVersion.V13, null, true, new DefaultHttpHeaders());
        this.channel = bootstrap.connect(host,port).channel();
        System.out.println("Websocket Mod Side Open successful");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        handshaker.handshake(ctx.channel());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("Websocket Mod Channel Read");
        if(!handshaker.isHandshakeComplete()){
            try {
                handshaker.finishHandshake(channel, (FullHttpResponse) msg);
                System.out.println("WebSocket Client connected!");
                dispatchEvent(onOpen,new WebsocketEvent.OpenEvent());
            } catch (WebSocketHandshakeException e) {
                System.out.println("WebSocket Client failed to connect");
            }
            return;
        }

        if(msg instanceof FullHttpResponse response){
            throw new IllegalStateException(
                    "Unexpected FullHttpResponse (getStatus=" + response.status() +
                            ", content=" + response.content().toString(CharsetUtil.UTF_8) + ')');
        }

        WebSocketFrame frame = (WebSocketFrame) msg;

        if(frame instanceof TextWebSocketFrame textFrame){
            System.out.println("[WS Monitor] RX[TEXT]: "+textFrame.text());
            dispatchEvent(onMessage,new WebsocketEvent.MessageEvent(textFrame.text()));
        }else if(frame instanceof BinaryWebSocketFrame binaryFrame){
            System.out.println("[WS Monitor] RX[Binary] ");
            dispatchEvent(onMessage,new WebsocketEvent.MessageEvent(binaryFrame.content()));
        }else if(frame instanceof PongWebSocketFrame){
            return;
        }else if(frame instanceof CloseWebSocketFrame closeFrame) {
            dispatchEvent(onClose,new WebsocketEvent.CloseEvent(
                    closeFrame.statusCode(),
                    closeFrame.reasonText()
            ));
            finalizeSocket();
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        System.out.println("WebSocket Client disconnected!");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
        dispatchEvent(onError,new WebsocketEvent.ErrorEvent(cause.getMessage()));
        finalizeSocket();
    }

    protected static <T> void dispatchEvent(Set<Consumer<T>> consumers, T event){
        for (Consumer<T> consumer : consumers) {
            consumer.accept(event);
        }
    }

    Set<Consumer<WebsocketEvent.CloseEvent>> onClose = new HashSet<>();
    Set<Consumer<WebsocketEvent.OpenEvent>> onOpen = new HashSet<>();
    Set<Consumer<WebsocketEvent.ErrorEvent>> onError = new HashSet<>();
    Set<Consumer<WebsocketEvent.MessageEvent>> onMessage = new HashSet<>();

    public void send(String string) {
        System.out.println("[WS Monitor] TX[TEXT]: "+string);
        this.channel.writeAndFlush(new TextWebSocketFrame(string));
    }

    public void send(ByteBuf buffer) {
        this.channel.writeAndFlush(new BinaryWebSocketFrame(buffer));
    }

    public void close(){
        this.channel.writeAndFlush(new CloseWebSocketFrame());
    }

    public void finalizeSocket(){
        if(isClosed)
            return;
        isClosed = true;
        this.channel.close();
        this.eventLoopGroup.shutdownGracefully();
        this.onOpen.clear();
        this.onError.clear();
        this.onClose.clear();
        this.onMessage.clear();
    }

    public void ping(){
        this.channel.write(new PingWebSocketFrame(Unpooled.wrappedBuffer(new byte[] { 8, 1, 8, 1 })));
    }
}
