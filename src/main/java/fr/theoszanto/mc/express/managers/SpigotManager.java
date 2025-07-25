package fr.theoszanto.mc.express.managers;

import fr.theoszanto.mc.express.ExpressObject;
import fr.theoszanto.mc.express.ExpressPlugin;
import fr.theoszanto.mc.express.commands.ExpressCommand;
import fr.theoszanto.mc.express.gui.ExpressGUI;
import fr.theoszanto.mc.express.listeners.ExpressListener;
import fr.theoszanto.mc.express.utils.JavaUtils;
import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class SpigotManager<P extends ExpressPlugin<P>> extends ExpressObject<P> implements Listener {
	private final @NotNull Map<@NotNull Player, @NotNull ExpressGUI<P>> activeGUIs = new HashMap<>();
	private final @NotNull Map<@NotNull Player, @NotNull TextRequest> pendingTextRequests = new HashMap<>();
	private boolean resetting = false;

	private static final @NotNull Timer timer = new Timer();

	public SpigotManager(@NotNull P plugin) {
		super(plugin);
	}

	@SuppressWarnings("rawtypes")
	public void init(@Nullable String commandsPackage, @Nullable String listenersPackage) {
		// Commands
		if (commandsPackage != null) {
			List<? extends ExpressCommand> commands = JavaUtils.instanciateSubClasses(ExpressCommand.class, commandsPackage, this.plugin);
			for (ExpressCommand<?> command : commands) {
				PluginCommand cmd = this.plugin.getCommand(command.getName());
				if (cmd == null)
					throw new IllegalStateException("Command not found in plugin.yml: " + command);
				cmd.setExecutor(command);
				cmd.setTabCompleter(command);
			}
		}

		// Listeners
		if (listenersPackage != null) {
			List<? extends ExpressListener> listeners = JavaUtils.instanciateSubClasses(ExpressListener.class, listenersPackage, this.plugin);
			PluginManager manager = this.plugin.getServer().getPluginManager();
			for (ExpressListener<?> listener : listeners)
				manager.registerEvents(listener, this.plugin);
			manager.registerEvents(this, this.plugin);
		}
	}

	public void showGUI(@NotNull Player player, @NotNull ExpressGUI<P> gui) {
		if (this.resetting)
			return;
		player.openInventory(gui.getInventory());
		ExpressGUI<P> previous = this.activeGUIs.put(player, gui);
		gui.onOpen(player, previous);
	}

	@EventHandler
	private void onGUIClick(@NotNull InventoryClickEvent event) {
		HumanEntity entity = event.getWhoClicked();
		if (entity instanceof Player player) {
			ExpressGUI<P> gui = this.activeGUIs.get(entity);
			if (gui != null && gui.getInventory().equals(event.getInventory())) {
				InventoryType.SlotType slotType = event.getSlotType();
				ClickType click = event.getClick();
				InventoryAction action = event.getAction();
				if (slotType == InventoryType.SlotType.OUTSIDE) {
					if (gui.onClickOutside(player, click, action))
						event.setCancelled(true);
				} else {
					Inventory clickedInventory = event.getClickedInventory();
					int slot = event.getSlot();
					if (gui.getInventory().equals(clickedInventory)) {
						ExpressGUI.SlotData data = gui.getSlotData(slot);
						if (data != null && data.getName().equals(ExpressGUI.CLOSE)) {
							event.setCancelled(true);
							this.run(player::closeInventory);
						} else if (gui.onClick(player, click, action, data))
							event.setCancelled(true);
					} else if (clickedInventory != null)
						if (gui.onClickInOtherInventory(player, clickedInventory, click, action, slot))
							event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler
	private void onGUIDrag(@NotNull InventoryDragEvent event) {
		HumanEntity entity = event.getWhoClicked();
		if (entity instanceof Player) {
			ExpressGUI<P> gui = this.activeGUIs.get(entity);
			if (gui != null && gui.getInventory().equals(event.getInventory())) {
				boolean impactGUI = false;
				boolean impactInv = false;
				for (int slot : event.getRawSlots()) {
					if (gui.getInventory().equals(event.getView().getInventory(slot)))
						impactGUI = true;
					else
						impactInv = true;
				}
				if ((impactGUI && gui.preventDragInside()) || (impactInv && gui.preventDragOutside()))
					event.setCancelled(true);
			}
		}
	}

	@EventHandler
	private void onGUIClose(@NotNull InventoryCloseEvent event) {
		HumanEntity entity = event.getPlayer();
		if (entity instanceof Player) {
			ExpressGUI<P> gui = this.activeGUIs.get(entity);
			if (gui != null && gui.getInventory().equals(event.getInventory())) {
				this.activeGUIs.remove(entity, gui);
				gui.onClose((Player) entity);
			}
		}
	}

	public @NotNull CompletableFuture<@NotNull String> requestText(@NotNull Player player, @NotNull String description, long timeoutDelay, TimeUnit unit) {
		return this.requestText(player, description, timeoutDelay, unit, null);
	}

	@SuppressWarnings("UnstableApiUsage") // Dialog
	public @NotNull CompletableFuture<@NotNull String> requestText(@NotNull Player player, @NotNull String description, long timeoutDelay, TimeUnit unit, @Nullable String original) {
		if (this.resetting)
			return JavaUtils.cancelledCompletableFuture();
		CompletableFuture<String> future = new CompletableFuture<>();
		TimerTask timeout = new TimerTask() {
			@Override
			public void run() {
				future.completeExceptionally(new TimeoutException());
				SpigotManager.this.pendingTextRequests.remove(player);
			}
		};
		timer.schedule(timeout, unit.toMillis(timeoutDelay));
		TextRequest request = new TextRequest(future, timeout);
		TextRequest previous = this.pendingTextRequests.put(player, request);
		if (previous != null)
			previous.abort();
		player.showDialog(Dialog.create(dialog -> dialog.empty()
				.base(DialogBase.create(
						Component.text(this.i18n("dialog.request-text-title")),
						null,
						false,
						false,
						DialogBase.DialogAfterAction.CLOSE,
						List.of(DialogBody.plainMessage(Component.text(description), 500)),
						List.of(DialogInput.text(
								"value",
								500,
								Component.empty(),
								false,
								Objects.requireNonNullElse(original, ""),
								Integer.MAX_VALUE,
								null
						))
				))
				.type(DialogType.confirmation(
						ActionButton.create(
								Component.text(this.i18n("dialog.confirm")),
								null,
								150,
								DialogAction.customClick((response, audience) -> {
									if (audience != player)
										return;
									String value = response.getText("value");
									request.fulfill(Objects.requireNonNullElse(value, ""));
									this.pendingTextRequests.remove(player);
								}, ClickCallback.Options.builder().uses(1).lifetime(Duration.of(timeoutDelay, unit.toChronoUnit())).build())
						),
						ActionButton.create(
								Component.text(this.i18n("dialog.cancel")),
								null,
								150,
								DialogAction.customClick((response, audience) -> {
									if (audience != player)
										return;
									request.abort();
									this.pendingTextRequests.remove(player);
								}, ClickCallback.Options.builder().uses(1).lifetime(Duration.of(timeoutDelay, unit.toChronoUnit())).build())
						)
				))));
		return future;
	}

	public void reset() {
		this.resetting = true;
		this.pendingTextRequests.forEach(this::resetTextRequest);
		this.pendingTextRequests.clear();
		new ArrayList<>(this.activeGUIs.keySet()).forEach(this::resetGUI);
		this.activeGUIs.clear();
		HandlerList.unregisterAll(this.plugin);
		this.resetting = false;
	}

	private void resetGUI(@NotNull Player player) {
		player.closeInventory();
		this.i18nMessage(player, "misc.cancelled-by-reload");
	}

	private void resetTextRequest(@NotNull Player player, @NotNull TextRequest request) {
		request.abort();
		this.i18nMessage(player, "misc.cancelled-by-reload");
	}

	private record TextRequest(@NotNull CompletableFuture<@NotNull String> future, @NotNull TimerTask timeout) {
		public void fulfill(@NotNull String message) {
			this.future.complete(message);
			this.timeout.cancel();
		}

		public void abort() {
			this.future.cancel(false);
			this.timeout.cancel();
		}
	}
}
