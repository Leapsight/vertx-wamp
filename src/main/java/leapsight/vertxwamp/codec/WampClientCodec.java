package leapsight.vertxwamp.codec;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;
import jawampa.WampClient;

public class WampClientCodec implements MessageCodec<WampClient, WampClient> {
    @Override
    public void encodeToWire(Buffer buffer, WampClient wampClient) {

    }

    @Override
    public WampClient decodeFromWire(int pos, Buffer buffer) {
        return null;
    }

    @Override
    public WampClient transform(WampClient wampClient) {
        return wampClient;
    }

    @Override
    public String name() {
        return this.getClass().getSimpleName();
    }

    @Override
    public byte systemCodecID() {
        return -1;
    }
}
