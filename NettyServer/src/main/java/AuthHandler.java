import commands.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class AuthHandler extends SimpleChannelInboundHandler<Command> {
    private NettyServer server;

    public AuthHandler(NettyServer server) {
        this.server = server;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
      //  ctx.writeAndFlush("Введите логин и пароль\n");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Command command) throws Exception {
        Command commandFromClient = command;
        if (command.getType().equals(CommandType.AUTH)) {
            AuthCommandData authCommand = (AuthCommandData) commandFromClient.getData();
            String login = authCommand.getLogin();
            String password = authCommand.getPassword();
            BaseAuthService authService = new BaseAuthService();
            String successAuth = authService.checkAuth(login, password);
            if (successAuth != null) {
                ctx.pipeline().remove(AuthHandler.class);
                ctx.pipeline().addLast(new MyFileHandler(server, login));
                ctx.pipeline().get(MyFileHandler.class).channelActive(ctx);
            } else {
                Command errorAuth = new Command().unknownCommand("Укажите верно авторизационные данные в формате: /auth логин пароль.");
                ctx.writeAndFlush(errorAuth);
            }
        }
    }
}

