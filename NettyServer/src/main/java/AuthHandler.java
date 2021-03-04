import commands.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class AuthHandler extends SimpleChannelInboundHandler<Command> {
    private NettyServer server;
    private BaseAuthService authService;

    public AuthHandler(NettyServer server) {
        this.server = server;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Client connected!");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Client disconnect!");
        ctx.close();

    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Command command) throws Exception {
        if (command.getType().equals(CommandType.AUTH)) {
            AuthCommandData authCommand = (AuthCommandData) command.getData();
            String login = authCommand.getLogin();
            String password = authCommand.getPassword();
            authService = server.getAuthService();
            String successAuth = authService.checkAuth(login, password);
            if (successAuth != null) {
                ctx.pipeline().remove(AuthHandler.class);
                ctx.pipeline().addLast(new MyFileHandler(server, login));
                ctx.pipeline().get(MyFileHandler.class).channelActive(ctx);
            } else {
                Command errorAuth = new Command().error("Неверно введены логин и пароль!");
                ctx.writeAndFlush(errorAuth);
            }
        }
        if (command.getType().equals(CommandType.END)){
            System.out.println("Получена команда END");
            Command commandEndToClient = new Command().closeConnection();
            ctx.writeAndFlush(commandEndToClient);
            ctx.close();
        }
    }
}

