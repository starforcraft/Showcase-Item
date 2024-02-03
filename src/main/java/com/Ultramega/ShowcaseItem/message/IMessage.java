package com.ultramega.showcaseitem.message;

import net.minecraftforge.network.NetworkEvent;

import java.io.Serializable;

public interface IMessage extends Serializable {
    boolean receive(NetworkEvent.Context context);
}