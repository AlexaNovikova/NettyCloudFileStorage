import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;


import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NettyServer {

    private ConcurrentHashMap<ChannelHandlerContext, String> clients;
    private BaseAuthService authService;
    public static final Logger logger = Logger.getLogger("");
    public NettyServer(){

        clients=new ConcurrentHashMap();
        // авторизация один поток>
        EventLoopGroup auth = new NioEventLoopGroup(1);

        // thread pool
        EventLoopGroup worker = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(auth, worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(

                                    new ObjectEncoder(),
                                    new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
                                    new AuthHandler(NettyServer.this)


                            );
                        }
                    });

            ChannelFuture future = bootstrap.bind(8189).sync();
            logger.log(Level.INFO,"Server started");
            authService = new BaseAuthService();
            future.channel().closeFuture().sync();
        }
        catch (InterruptedException e) {
            logger.log(Level.SEVERE,"Server was broken");

        }
         finally {
            authService.disconnect();
            auth.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

    public ConcurrentHashMap<ChannelHandlerContext, String> getClients() {
        return clients;
    }

    public BaseAuthService getAuthService() {
        return authService;
    }


    public static void main(String[] args) {
        new NettyServer();
    }
}
