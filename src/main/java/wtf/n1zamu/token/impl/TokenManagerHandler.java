package wtf.n1zamu.token.impl;

import me.realized.tokenmanager.TokenManagerPlugin;
import wtf.n1zamu.token.TokenHandler;

public class TokenManagerHandler implements TokenHandler {
    @Override
    public void handle(String name, int amount) {
        TokenManagerPlugin.getInstance().addTokens(name, amount, true);
    }
}
