package fr.theoszanto.mc.express.gui;

import fr.theoszanto.mc.express.ExpressPlugin;
import fr.theoszanto.mc.express.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class ExpressPaginatedGUI<P extends ExpressPlugin<P>, T> extends ExpressGUI<P> {
	protected final @NotNull List<@NotNull T> list = new ArrayList<>();
	protected int pageMax;
	protected int page;

	public ExpressPaginatedGUI(@NotNull P plugin, @NotNull Collection<? extends @NotNull T> list, int rows, @NotNull String key, @Nullable Object @NotNull... format) {
		super(plugin, rows, key, format);
		this.setElements(list);
		this.page = 0;
	}

	protected final void setButtons(int previous, int next, int current, int close) {
		// Previous
		int previousPage = this.page - 1;
		if (previousPage >= 0)
			this.set(previous, new ItemBuilder(Material.ARROW, previousPage + 1, this.i18n("menu.previous", "page", previousPage + 1, "total", this.pageMax + 1)), "previous", previousPage);

		// Next
		int nextPage = this.page + 1;
		if (nextPage <= this.pageMax)
			this.set(next, new ItemBuilder(Material.ARROW, nextPage + 1, this.i18n("menu.next", "page", nextPage + 1, "total", this.pageMax + 1)), "next", nextPage);

		// Current page & close button
		this.set(current, new ItemBuilder(Material.PAPER, this.page + 1, this.i18n("menu.page", "page", this.page + 1, "total", this.pageMax + 1)));
		this.setCloseButton(close);
	}

	protected final void setEmptyIndicator(int slot, @NotNull String key, @Nullable Object @NotNull... format) {
		if (this.list.isEmpty())
			this.set(slot, new ItemBuilder(Material.STRUCTURE_VOID, 1, this.i18n(key, format)));
	}

	protected abstract void prepareGUI();

	protected abstract int @NotNull[] contentSlots();

	protected abstract @Nullable ItemStack icon(@NotNull Player player, @NotNull T element);

	@Override
	public final void onOpen(@NotNull Player player, @Nullable ExpressGUI<P> previous) {
		this.prepareGUI();
		if (!this.list.isEmpty()) {
			int[] contentSlots = this.contentSlots();
			int i = this.page * contentSlots.length;
			int slot = 0;
			while (slot < contentSlots.length && i < this.list.size()) {
				T element = this.list.get(i);
				ItemStack icon = this.icon(player, element);
				if (icon != null) {
					this.set(contentSlots[slot], icon, "element", element);
					slot++;
				}
				i++;
			}
		}
	}

	protected boolean onClickOnElement(@NotNull Player player, @NotNull ClickType click, @NotNull InventoryAction action, @NotNull T element) {
		return true;
	}

	protected boolean onOtherClick(@NotNull Player player, @NotNull ClickType click, @NotNull InventoryAction action, @Nullable SlotData data) {
		return true;
	}

	@Override
	public final boolean onClick(@NotNull Player player, @NotNull ClickType click, @NotNull InventoryAction action, @Nullable SlotData data) {
		if (data != null) {
			switch (data.getName()) {
			case "previous":
			case "next":
				this.page = data.getUserData();
				this.refresh(player);
				return true;
			case "element":
				return this.onClickOnElement(player, click, action, data.getUserData());
			}
		}
		return this.onOtherClick(player, click, action, data);
	}

	protected void setElements(@NotNull Collection<? extends @NotNull T> elements) {
		this.list.clear();
		this.list.addAll(elements);
		this.recomputePageMax();
	}

	protected void recomputePageMax() {
		this.pageMax = Math.max(0, (this.list.size() - 1) / this.contentSlots().length);
		this.page = Math.min(this.page, this.pageMax);
	}
}
