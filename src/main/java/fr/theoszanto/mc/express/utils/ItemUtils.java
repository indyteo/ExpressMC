package fr.theoszanto.mc.express.utils;

import io.papermc.paper.inventory.ItemRarity;
import io.papermc.paper.inventory.tooltip.TooltipContext;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;
import org.jetbrains.annotations.Unmodifiable;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

public class ItemUtils {
	public static final @NotNull ItemStack EMPTY = new ItemStack(Material.AIR);
	public static final @NotNull String @NotNull[] NO_LORE = new String[0];
	public static final @NotNull LegacyComponentSerializer COMPONENT_SERIALIZER = LegacyComponentSerializer.legacySection();
	public static final @NotNull LegacyComponentSerializer AMPERSAND_SERIALIZER = LegacyComponentSerializer.legacyAmpersand();

	private static final @NotNull DecimalFormat formatter = new DecimalFormat("0.0", DecimalFormatSymbols.getInstance(Locale.US));

	private ItemUtils() {
		throw new UnsupportedOperationException();
	}

	public static @NotNull ItemStack unmodifiableItemStack(@NotNull ItemStack item) {
		return new UnmodifiableItemStack(item);
	}

	@DelegateDeserialization(ItemStack.class)
	@SuppressWarnings({ "deprecation" })
	private static class UnmodifiableItemStack extends ItemStack {
		private final @NotNull ItemStack item;

		private UnmodifiableItemStack(@NotNull ItemStack item) {
			this.item = item;
		}

		@Override
		public @NotNull Material getType() {
			return this.item.getType();
		}

		@Override
		public int getAmount() {
			return this.item.getAmount();
		}

		@Override
		public @Nullable MaterialData getData() {
			return this.item.getData();
		}

		@Override
		public short getDurability() {
			return this.item.getDurability();
		}

		@Override
		public int getMaxStackSize() {
			return this.item.getMaxStackSize();
		}

		@Override
		public String toString() {
			return this.item.toString();
		}

		@Override
		@SuppressWarnings({ "EqualsWhichDoesntCheckParameterClass", "EqualsDoesntCheckParameterClass" })
		public boolean equals(Object obj) {
			return this.item.equals(obj);
		}

		@Override
		public boolean isSimilar(@Nullable ItemStack stack) {
			return this.item.isSimilar(stack);
		}

		@Override
		@SuppressWarnings("MethodDoesntCallSuperMethod")
		public @NotNull ItemStack clone() {
			return this.item.clone();
		}

		@Override
		public int hashCode() {
			return this.item.hashCode();
		}

		@Override
		public boolean containsEnchantment(@NotNull Enchantment ench) {
			return this.item.containsEnchantment(ench);
		}

		@Override
		public int getEnchantmentLevel(@NotNull Enchantment ench) {
			return this.item.getEnchantmentLevel(ench);
		}

		@Override
		public @NotNull Map<@NotNull Enchantment, @NotNull Integer> getEnchantments() {
			return this.item.getEnchantments();
		}

		@Override
		public @Nullable ItemMeta getItemMeta() {
			return this.item.getItemMeta();
		}

		@Override
		public boolean hasItemMeta() {
			return this.item.hasItemMeta();
		}

		@Override
		public @NotNull ItemStack withType(@NotNull Material type) {
			return this.item.withType(type);
		}

		@Override
		@SuppressWarnings("removal")
		public @NotNull String getTranslationKey() {
			return this.item.getTranslationKey();
		}

		@Override
		public @NotNull ItemStack enchantWithLevels(@Range(from = 1L, to = 30L) int levels, boolean allowTreasure, @NotNull Random random) {
			return this.item.enchantWithLevels(levels, allowTreasure, random);
		}

		@Override
		public @NotNull HoverEvent<HoverEvent.ShowItem> asHoverEvent(@NotNull UnaryOperator<HoverEvent.ShowItem> op) {
			return this.item.asHoverEvent(op);
		}

		@Override
		public @NotNull Component displayName() {
			return this.item.displayName();
		}

		@Override
		public @NotNull ItemStack ensureServerConversions() {
			return this.item.ensureServerConversions();
		}

		@Override
		public byte @NotNull[] serializeAsBytes() {
			return this.item.serializeAsBytes();
		}

		@Override
		public @Nullable String getI18NDisplayName() {
			return this.item.getI18NDisplayName();
		}

		@Override
		public int getMaxItemUseDuration() {
			return this.item.getMaxItemUseDuration();
		}

		@Override
		public @NotNull ItemStack asOne() {
			return this.item.asOne();
		}

		@Override
		public @NotNull ItemStack asQuantity(int qty) {
			return this.item.asQuantity(qty);
		}

		@Override
		public @Nullable List<String> getLore() {
			return this.item.getLore();
		}

		@Override
		public @Nullable List<Component> lore() {
			return this.item.lore();
		}

		@Override
		public @NotNull Set<ItemFlag> getItemFlags() {
			return this.item.getItemFlags();
		}

		@Override
		public boolean hasItemFlag(@NotNull ItemFlag flag) {
			return this.item.hasItemFlag(flag);
		}

		@Override
		public @NotNull String translationKey() {
			return this.item.translationKey();
		}

		@Override
		public @NotNull ItemRarity getRarity() {
			return this.item.getRarity();
		}

		@Override
		public boolean isRepairableBy(@NotNull ItemStack repairMaterial) {
			return this.item.isRepairableBy(repairMaterial);
		}

		@Override
		public boolean canRepair(@NotNull ItemStack toBeRepaired) {
			return this.item.canRepair(toBeRepaired);
		}

		@Override
		public boolean isEmpty() {
			return this.item.isEmpty();
		}

		@Override
		public @NotNull @Unmodifiable List<Component> computeTooltipLines(@NotNull TooltipContext tooltipContext, @Nullable Player player) {
			return this.item.computeTooltipLines(tooltipContext, player);
		}

		@Override
		public void setType(@NotNull Material type) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void setAmount(int amount) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void setData(@Nullable MaterialData data) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void setDurability(short durability) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void addEnchantments(@NotNull Map<Enchantment, Integer> enchantments) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void addEnchantment(@NotNull Enchantment ench, int level) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void addUnsafeEnchantments(@NotNull Map<Enchantment, Integer> enchantments) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void addUnsafeEnchantment(@NotNull Enchantment ench, int level) {
			throw new UnsupportedOperationException();
		}

		@Override
		public int removeEnchantment(@NotNull Enchantment ench) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean setItemMeta(@Nullable ItemMeta itemMeta) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void removeEnchantments() {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean editMeta(@NotNull Consumer<? super ItemMeta> consumer) {
			throw new UnsupportedOperationException();
		}

		@Override
		public <M extends ItemMeta> boolean editMeta(@NotNull Class<M> metaClass, @NotNull Consumer<? super M> consumer) {
			throw new UnsupportedOperationException();
		}

		@Override
		public @NotNull ItemStack add() {
			throw new UnsupportedOperationException();
		}

		@Override
		public @NotNull ItemStack add(int qty) {
			throw new UnsupportedOperationException();
		}

		@Override
		public @NotNull ItemStack subtract() {
			throw new UnsupportedOperationException();
		}

		@Override
		public @NotNull ItemStack subtract(int qty) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void setLore(@Nullable List<String> lore) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void lore(@Nullable List<? extends Component> lore) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void addItemFlags(@NotNull ItemFlag... itemFlags) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void removeItemFlags(@NotNull ItemFlag... itemFlags) {
			throw new UnsupportedOperationException();
		}

		@Override
		public @NotNull ItemStack damage(int amount, @NotNull LivingEntity livingEntity) {
			throw new UnsupportedOperationException();
		}

		@Override
		public @NotNull Map<String, Object> serialize() {
			return this.item.serialize();
		}

		public static @NotNull ItemStack deserialize(@NotNull Map<String, Object> args) {
			return ItemStack.deserialize(args);
		}
	}

	public static @NotNull String name(@NotNull ItemStack item) {
		ItemMeta meta = item.getItemMeta();
		if (meta != null) {
			if (meta.hasDisplayName())
				return COMPONENT_SERIALIZER.serialize(Objects.requireNonNull(meta.displayName()));
		}
		char[] s = item.getType().name().replace('_', ' ').toLowerCase().toCharArray();
		boolean space = true;
		for (int i = 0; i < s.length; i++) {
			if (space)
				s[i] += (char) ('A' - 'a');
			space = s[i] == ' ';
		}
		return new String(s);
	}

	public static void addLoreConditionally(boolean condition, @NotNull ItemStack item, @NotNull String @NotNull... lore) {
		if (condition)
			addLore(item, lore);
	}

	public static void addLore(@NotNull ItemStack item, @NotNull String @NotNull... lore) {
		ItemMeta meta = item.getItemMeta();
		if (meta == null)
			return;
		List<Component> existingLore = meta.hasLore() ? meta.lore() : new ArrayList<>();
		assert existingLore != null;
		existingLore.addAll(Arrays.stream(lore).map(Component::text).toList());
		meta.lore(existingLore);
		item.setItemMeta(meta);
	}

	public static @NotNull ItemStack withAmount(@NotNull ItemStack item, int amount) {
		ItemStack clone = item.clone();
		clone.setAmount(amount);
		return clone;
	}

	public static int stackAmountFromValue(double value) {
		String valueStr = formatter.format(value);
		String firstTwoDigits = value < 1 ? "1" : valueStr.substring(0, value >= 10 ? 2 : 1);
		int amount = Integer.parseInt(firstTwoDigits);
		if (amount > 64)
			amount /= 10;
		return amount;
	}

	public static @NotNull String toString(@NotNull ItemStack item) {
		return new String(Base64Coder.encode(item.serializeAsBytes()));
	}

	public static @NotNull ItemStack fromString(@NotNull String item) {
		return ItemStack.deserializeBytes(Base64Coder.decode(item));
	}

	public static @NotNull String translateAmpersandColorCodes(@NotNull String str) {
		return COMPONENT_SERIALIZER.serialize(AMPERSAND_SERIALIZER.deserialize(str));
	}

	public static @NotNull ItemStack hideTooltip(@NotNull ItemStack item) {
		ItemMeta meta = item.getItemMeta();
		meta.setHideTooltip(true);
		item.setItemMeta(meta);
		return item;
	}

	@SuppressWarnings("deprecation") // Bukkit.getUnsafe() is deprecated for no reason
	public static boolean basicItemEquals(@NotNull ItemStack item1, @NotNull ItemStack item2) {
		Material comparisonType = (item1.getType().isLegacy()) ? Bukkit.getUnsafe().fromLegacy(item1.getData(), true) : item1.getType(); // This may be called from legacy item stacks, try to get the right material
		return comparisonType == item2.getType()
				&& (!(item1.getItemMeta() instanceof Damageable damageable1) || item2.getItemMeta() instanceof Damageable damageable2 && damageable1.getDamage() == damageable2.getDamage())
				&& item1.hasItemMeta() == item2.hasItemMeta()
				&& (!item1.hasItemMeta() || basicMetaEquals(item1.getItemMeta(), item2.getItemMeta()));
	}

	public static boolean basicMetaEquals(@Nullable ItemMeta meta1, @Nullable ItemMeta meta2) {
		if (meta1 == null)
			return meta2 == null;
		if (meta2 == null)
			return false;
		return ((meta1.hasDisplayName() ? meta2.hasDisplayName() && basicComponentEquals(meta1.displayName(), meta2.displayName()) : !meta2.hasDisplayName()))
				&& (meta1.hasEnchants() ? meta2.hasEnchants() && meta1.getEnchants().equals(meta2.getEnchants()) : !meta2.hasEnchants())
				&& (meta1.hasLore() ? meta2.hasLore() && basicComponentsEquals(meta1.lore(), meta2.lore()) : !meta2.hasLore())
				&& (meta1.getItemFlags().equals(meta2.getItemFlags()));
	}

	public static boolean basicComponentsEquals(@Nullable List<@NotNull Component> components1, @Nullable List<@NotNull Component> components2) {
		if (components1 == null)
			return components2 == null;
		if (components2 == null)
			return false;
		if (components1.size() != components2.size())
			return false;
		for (int i = 0; i < components1.size(); i++)
			if (!basicComponentEquals(components1.get(i), components2.get(i)))
				return false;
		return true;
	}

	public static boolean basicComponentEquals(@Nullable Component component1, @Nullable Component component2) {
		if (component1 == null)
			return component2 == null;
		if (component2 == null)
			return false;
		return COMPONENT_SERIALIZER.serialize(component1).equals(COMPONENT_SERIALIZER.serialize(component2));
	}
}
