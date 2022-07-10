package fr.theoszanto.mc.express.commands;

import fr.theoszanto.mc.express.ExpressObject;
import fr.theoszanto.mc.express.ExpressPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ExpressCommand<P extends ExpressPlugin<P>> extends ExpressObject<P> implements TabExecutor {
	private final @NotNull String name;

	public ExpressCommand(@NotNull P plugin, @NotNull String name) {
		super(plugin);
		this.name = name;
	}

	public @NotNull String getName() {
		return this.name;
	}

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		return false;
	}

	@Nullable
	@Override
	public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
		return null;
	}
}
