package fr.theoszanto.mc.express.utils;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;
import java.util.logging.Logger;

public interface Logged {
	@NotNull Logger getLogger();

	default void debug(@NotNull String message) {
		this.getLogger().info(message);
		Component component = Component.text("§8[§cDEBUG§8]§r " + message).hoverEvent(Component.text(JavaUtils.caller()));
		for (Player player : Bukkit.getOnlinePlayers())
			if (player.isOp())
				player.sendMessage(component);
	}

	default void log(@NotNull String message) {
		this.getLogger().info(message);
	}

	default void warn(@NotNull String message) {
		this.getLogger().warning(message);
	}

	default void error(@NotNull String message) {
		this.getLogger().severe(message);
	}

	default void error(@NotNull String message, @NotNull Throwable e) {
		this.getLogger().log(Level.SEVERE, message, e);
	}
}
