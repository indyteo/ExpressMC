package fr.theoszanto.mc.express;

import fr.theoszanto.mc.express.managers.I18nManager;
import fr.theoszanto.mc.express.managers.SpigotManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Collections;
import java.util.List;

public abstract class ExpressPlugin<P extends ExpressPlugin<P>> extends JavaPlugin {
	@SuppressWarnings("unchecked")
	private final @NotNull I18nManager<P> i18n = new I18nManager<>((P) this);
	@SuppressWarnings("unchecked")
	private final @NotNull SpigotManager<P> spigot = new SpigotManager<>((P) this);
	private final @Nullable String commandsPackage;
	private final @Nullable String listenersPackage;

	private @Nullable String prefix;

	public ExpressPlugin() {
		this(null, null);
	}

	public ExpressPlugin(@NotNull String basePackage) {
		this(basePackage + ".commands", basePackage + ".listeners");
	}

	public ExpressPlugin(@Nullable String commandsPackage, @Nullable String listenersPackage) {
		this.commandsPackage = commandsPackage;
		this.listenersPackage = listenersPackage;
	}

	@Override
	public final void onEnable() {
		this.saveDefaultConfig();
		this.reloadConfig();
		this.loadConfig(this.getConfig());

		// Initializing i18n module
		String messagesFile = this.getLocalizedMessagesFile();
		this.saveResource(messagesFile, false);
		this.i18n.loadMessages(new File(this.getDataFolder(), messagesFile));
		for (File additionalMessageFile : this.getAdditionalMessageFiles())
			this.i18n.loadMessages(additionalMessageFile);

		// Initializing Spigot plugin stuff
		this.spigot.init(this.commandsPackage, this.listenersPackage);

		// Initialize plugin
		this.init();
	}

	@Override
	public final void onDisable() {
		this.reset();
		this.spigot.reset();
		this.i18n.reset();
	}

	public final void reload() {
		this.onDisable();
		this.prefix = null;
		this.onEnable();
	}

	public abstract void loadConfig(@NotNull FileConfiguration config);

	public abstract @NotNull String getLocale();

	public @NotNull String getLocalizedMessagesFile() {
		return "messages/" + this.getLocale() + ".yml";
	}

	protected @NotNull List<@NotNull File> getAdditionalMessageFiles() {
		return Collections.emptyList();
	}

	protected abstract void init();

	protected abstract void reset();

	public final @NotNull String prefix() {
		if (this.prefix == null)
			this.prefix = this.i18n("prefix");
		return this.prefix;
	}

	public final @NotNull String i18n(@NotNull String key, @Nullable Object @NotNull... format) {
		return this.i18n.getMessage(key, format);
	}

	public final @NotNull SpigotManager<P> spigot() {
		return this.spigot;
	}
}
