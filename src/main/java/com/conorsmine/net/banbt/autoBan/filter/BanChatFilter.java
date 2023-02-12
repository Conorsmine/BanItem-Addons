package com.conorsmine.net.banbt.autoBan.filter;

import com.comphenix.packetwrapper.WrapperPlayServerChat;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.conorsmine.net.banbt.BaNBT;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.chat.ComponentSerializer;
import net.minecraft.network.protocol.game.PacketPlayOutChat;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

public class BanChatFilter extends PacketAdapter implements Listener {

    private final Map<UUID, MessageFilter> filterMap = new HashMap<>();

    public BanChatFilter(BaNBT pl) {
        super(pl, ListenerPriority.NORMAL, PacketType.Play.Server.CHAT);
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        final UUID pId = event.getPlayer().getUniqueId();
        WrapperPlayServerChat chat = new WrapperPlayServerChat(event.getPacket());
        if (chat.getMessage() == null) return;


        BaseComponent[] msgArr = ComponentSerializer.parse(chat.getMessage().getJson());
        String plainMsg = BaseComponent.toPlainText(msgArr);
        String colMsg = BaseComponent.toLegacyText(msgArr);


        if (plainMsg.equals(MessageFilter.SEP))
            filterMap.putIfAbsent(pId, new ChatMessageFilter(event.getPlayer()));

        if (!filterMap.containsKey(pId)) return;

        MessageFilter filter = filterMap.get(pId);
        if (!filter.sendMessage(colMsg)) event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        filterMap.remove(event.getPlayer().getUniqueId());
    }


}
