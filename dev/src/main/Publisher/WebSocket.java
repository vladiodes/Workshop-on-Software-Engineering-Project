package main.Publisher;

import io.javalin.websocket.WsContext;

public class WebSocket {
    private WsContext ctx;

    public WebSocket(WsContext ctx){
        this.ctx=ctx;
    }

    public boolean send(Notification notification){
        ctx.send(notification.print());
        return true;
    }
}
