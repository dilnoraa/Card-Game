package com.game.protocol;

import com.common.PlayerAction;

import java.io.Serializable;

/**
 * Player notifies server about its action
 */
public final class PlayerActionRequest implements Serializable {
    private final PlayerAction playerAction;

    public PlayerActionRequest(PlayerAction playerAction) {
        this.playerAction = playerAction;
    }

    public PlayerAction getPlayerAction() {
        return playerAction;
    }

    @Override
    public String toString() {
        return "PlayerActionRequest{" +
                "playerAction=" + playerAction +
                '}';
    }
}
