package fr.theoszanto.mc.express.listeners;

import fr.theoszanto.mc.express.ExpressObject;
import fr.theoszanto.mc.express.ExpressPlugin;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public class ExpressListener<P extends ExpressPlugin<P>> extends ExpressObject<P> implements Listener {
	public ExpressListener(@NotNull P plugin) {
		super(plugin);
	}
}
