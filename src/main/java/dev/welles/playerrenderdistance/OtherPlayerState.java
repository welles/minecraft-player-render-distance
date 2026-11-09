package dev.welles.playerrenderdistance;

/** Added to the render state of entities since 1.21.9: whether it is the state of another player. */
public interface OtherPlayerState {
    boolean playerrenderdistance$isOtherPlayer();

    void playerrenderdistance$setOtherPlayer(boolean otherPlayer);
}
