package fr.theoszanto.mc.express.utils;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.TooltipDisplay;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@SuppressWarnings("UnstableApiUsage") // DataComponentType
public class ItemBuilder {
	private @NotNull Material material;
	private int amount;
	private @Nullable String displayName;
	private @Nullable List<@NotNull String> lore;
	private @NotNull Map<@NotNull Enchantment, @NotNull Integer> enchantments = new HashMap<>();
	private @NotNull Set<@NotNull ItemFlag> flags = EnumSet.noneOf(ItemFlag.class);
	private boolean unbreakable;
	private @NotNull Multimap<@NotNull Attribute, @NotNull AttributeModifier> attributes = LinkedHashMultimap.create();
	private @Nullable Boolean glint;
	private @Nullable Integer maxStackSize;
	private @NotNull Set<@NotNull DataComponentType> hiddenDataComponents = new HashSet<>();

	public ItemBuilder(@NotNull Material material) {
		this(material, 1);
	}

	public ItemBuilder(@NotNull Material material, int amount) {
		this(material, amount, null);
	}

	public ItemBuilder(@NotNull Material material, int amount, @Nullable String displayName, @NotNull String @Nullable... lore) {
		this(material, amount, displayName, lore == null || lore.length == 0 ? null : Arrays.asList(lore));
	}

	public ItemBuilder(@NotNull Material material, int amount, @Nullable String displayName, @Nullable List<@NotNull String> lore) {
		this.material = material;
		this.amount = amount;
		this.displayName = displayName;
		this.setLore(lore);
		if (amount > material.getMaxStackSize() && amount < 100)
			this.maxStackSize = amount;
	}
/*
	protected ItemBuilder(@NotNull Material material, int amount, @Nullable String displayName, @Nullable List<@NotNull String> lore) {
		this.material = material;
		this.amount = amount;
		this.displayName = displayName;
		this.setLore(lore);
	}
*/
	public @NotNull Material getMaterial() {
		return this.material;
	}

	@Contract(value = "_ -> this", mutates = "this")
	public @NotNull ItemBuilder setMaterial(@NotNull Material material) {
		this.material = material;
		return this;
	}

	public int getAmount() {
		return this.amount;
	}

	@Contract(value = "_ -> this", mutates = "this")
	public @NotNull ItemBuilder setAmount(int amount) {
		if (this.amount <= 0 || this.amount > this.getMaxStackAmount())
			throw new IllegalArgumentException("Invalid stack amount: " + amount);
		this.amount = amount;
		return this;
	}

	public int getMaxStackAmount() {
		return this.maxStackSize == null ? this.material.getMaxStackSize() : this.maxStackSize;
	}

	public @Nullable String getDisplayName() {
		return this.displayName;
	}

	@Contract(value = "_ -> this", mutates = "this")
	public @NotNull ItemBuilder setDisplayName(@Nullable String displayName) {
		this.displayName = displayName;
		return this;
	}

	public boolean hasDisplayName() {
		return this.displayName != null;
	}

	@UnmodifiableView
	public @Nullable List<@NotNull String> getLore() {
		return this.lore == null ? null : Collections.unmodifiableList(this.lore);
	}

	@Contract(value = "_ -> this", mutates = "this")
	public @NotNull ItemBuilder setLore(@Nullable Collection<@NotNull String> lore) {
		this.lore = lore == null ? null : new ArrayList<>(lore);
		return this;
	}

	@Contract(value = "_ -> this", mutates = "this")
	public @NotNull ItemBuilder addLore(@NotNull String @Nullable... lore) {
		return lore == null ? this : this.addLore(Arrays.asList(lore));
	}

	@Contract(value = "_ -> this", mutates = "this")
	public @NotNull ItemBuilder addLore(@Nullable Collection<@NotNull String> lore) {
		if (lore != null) {
			if (this.lore == null)
				this.lore = new ArrayList<>(lore);
			else
				this.lore.addAll(lore);
		}
		return this;
	}

	@Contract(value = "_, _ -> this", mutates = "this")
	public @NotNull ItemBuilder addLoreConditionally(boolean condition, @NotNull String loreIfTrue) {
		if (condition)
			this.addLore(loreIfTrue);
		return this;
	}

	@Contract(value = "_, _, _ -> this", mutates = "this")
	public @NotNull ItemBuilder addLoreConditionally(boolean condition, @NotNull String loreIfTrue, @NotNull String loreIfFalse) {
		if (condition)
			this.addLore(loreIfTrue);
		else
			this.addLore(loreIfFalse);
		return this;
	}

	@Contract(value = "_ -> this", mutates = "this")
	public @NotNull ItemBuilder removeLore(int index) {
		if (this.lore == null || index < 0 || index >= this.getLoreLinesCount())
			throw new IndexOutOfBoundsException("No lore line at index: " + index);
		this.lore.remove(index);
		return this;
	}

	public int getLoreLinesCount() {
		return this.lore == null ? 0 : this.lore.size();
	}

	public boolean hasLore() {
		return this.lore != null && !this.lore.isEmpty();
	}

	@UnmodifiableView
	public @NotNull Map<@NotNull Enchantment, @NotNull Integer> getEnchantments() {
		return Collections.unmodifiableMap(this.enchantments);
	}

	@Contract(value = "_ -> this", mutates = "this")
	public @NotNull ItemBuilder setEnchantments(@NotNull Map<@NotNull Enchantment, @NotNull Integer> enchantments) {
		this.enchantments = new HashMap<>(enchantments);
		return this;
	}

	@Contract(value = "_ -> this", mutates = "this")
	public @NotNull ItemBuilder addEnchantments(@NotNull Map<@NotNull Enchantment, @NotNull Integer> enchantments) {
		this.enchantments.putAll(enchantments);
		return this;
	}

	@Contract(value = "_, _ -> this", mutates = "this")
	public @NotNull ItemBuilder addEnchantment(@NotNull Enchantment enchantment, int level) {
		this.enchantments.put(enchantment, level);
		return this;
	}

	@Contract(value = "_ -> this", mutates = "this")
	public @NotNull ItemBuilder removeEnchantment(@NotNull Enchantment enchantment) {
		this.enchantments.remove(enchantment);
		return this;
	}

	public int getEnchantmentsCount() {
		return this.enchantments.size();
	}

	public boolean isEnchanted() {
		return !this.enchantments.isEmpty();
	}

	@UnmodifiableView
	public @NotNull Set<@NotNull ItemFlag> getFlags() {
		return Collections.unmodifiableSet(this.flags);
	}

	@Contract(value = "_ -> this", mutates = "this")
	public @NotNull ItemBuilder setFlags(@NotNull Collection<@NotNull ItemFlag> flags) {
		this.flags = EnumSet.copyOf(flags);
		return this;
	}

	@Contract(value = "_ -> this", mutates = "this")
	public @NotNull ItemBuilder addFlags(@NotNull Collection<@NotNull ItemFlag> flags) {
		this.flags.addAll(flags);
		return this;
	}

	@Contract(value = "_ -> this", mutates = "this")
	public @NotNull ItemBuilder addFlags(@NotNull ItemFlag @NotNull... flags) {
		return this.addFlags(Arrays.asList(flags));
	}

	@Contract(value = "_ -> this", mutates = "this")
	public @NotNull ItemBuilder addFlag(@NotNull ItemFlag flag) {
		this.flags.add(flag);
		return this;
	}

	@Contract(value = "_ -> this", mutates = "this")
	public @NotNull ItemBuilder removeFlag(@NotNull ItemFlag flag) {
		this.flags.remove(flag);
		return this;
	}

	public boolean hasFlags() {
		return !this.flags.isEmpty();
	}

	public boolean isUnbreakable() {
		return this.unbreakable;
	}

	@Contract(value = " -> this", mutates = "this")
	public @NotNull ItemBuilder setUnbreakable() {
		return this.setUnbreakable(true);
	}

	@Contract(value = "_ -> this", mutates = "this")
	public @NotNull ItemBuilder setUnbreakable(boolean unbreakable) {
		this.unbreakable = unbreakable;
		return this;
	}

	@UnmodifiableView
	public @NotNull Multimap<@NotNull Attribute, @NotNull AttributeModifier> getAttributes() {
		return Multimaps.unmodifiableMultimap(this.attributes);
	}

	@Contract(value = "_ -> this", mutates = "this")
	public @NotNull ItemBuilder setAttributes(@Nullable Multimap<@NotNull Attribute, @NotNull AttributeModifier> attributes) {
		this.attributes = attributes == null ? LinkedHashMultimap.create() : LinkedHashMultimap.create(attributes);
		return this;
	}

	@Contract(value = "_, _ -> this", mutates = "this")
	public @NotNull ItemBuilder addAttribute(@NotNull Attribute attribute, @NotNull AttributeModifier modifier) {
		this.attributes.put(attribute, modifier);
		return this;
	}

	public boolean hasAttributes() {
		return !this.attributes.isEmpty();
	}

	public boolean hasGlint() {
		return this.glint == null ? !this.enchantments.isEmpty() : this.glint;
	}

	public @Nullable Boolean getGlint() {
		return this.glint;
	}

	@Contract(value = " -> this", mutates = "this")
	public @NotNull ItemBuilder setGlint() {
		return this.setGlint(true);
	}

	@Contract(value = "_ -> this", mutates = "this")
	public @NotNull ItemBuilder setGlint(@Nullable Boolean glint) {
		this.glint = glint;
		return this;
	}

	public @Nullable Integer getMaxStackSize() {
		return this.maxStackSize;
	}

	@Contract(value = "_ -> this", mutates = "this")
	public @NotNull ItemBuilder setMaxStackSize(@Nullable Integer maxStackSize) {
		this.maxStackSize = maxStackSize;
		return this;
	}

	@UnmodifiableView
	public @NotNull Set<@NotNull DataComponentType> getHiddenDataComponents() {
		return Collections.unmodifiableSet(this.hiddenDataComponents);
	}

	@Contract(value = "_ -> this", mutates = "this")
	public @NotNull ItemBuilder setHiddenDataComponents(@NotNull Collection<@NotNull DataComponentType> hiddenDataComponents) {
		this.hiddenDataComponents = new HashSet<>(hiddenDataComponents);
		return this;
	}

	@Contract(value = "_ -> this", mutates = "this")
	public @NotNull ItemBuilder addHiddenDataComponents(@NotNull Collection<@NotNull DataComponentType> hiddenDataComponents) {
		this.hiddenDataComponents.addAll(hiddenDataComponents);
		return this;
	}

	@Contract(value = "_ -> this", mutates = "this")
	public @NotNull ItemBuilder addHiddenDataComponents(@NotNull DataComponentType @NotNull... hiddenDataComponents) {
		return this.addHiddenDataComponents(Arrays.asList(hiddenDataComponents));
	}

	@Contract(value = "_ -> this", mutates = "this")
	public @NotNull ItemBuilder addHiddenDataComponent(@NotNull DataComponentType hiddenDataComponent) {
		this.hiddenDataComponents.add(hiddenDataComponent);
		return this;
	}

	@Contract(value = "_ -> this", mutates = "this")
	public @NotNull ItemBuilder removeHiddenDataComponent(@NotNull DataComponentType hiddenDataComponent) {
		this.hiddenDataComponents.remove(hiddenDataComponent);
		return this;
	}

	public boolean hasHiddenDataComponents() {
		return !this.hiddenDataComponents.isEmpty();
	}

	@MustBeInvokedByOverriders
	protected void buildMeta(@NotNull ItemMeta meta) {
		meta.displayName(this.displayName == null ? null : ItemUtils.component(this.displayName));
		meta.lore(this.lore == null ? null : this.lore.stream().map(ItemUtils::component).toList());
		this.flags.forEach(meta::addItemFlags);
		meta.setUnbreakable(this.unbreakable);
		meta.setAttributeModifiers(this.attributes);
		meta.setEnchantmentGlintOverride(this.glint);
		meta.setMaxStackSize(this.maxStackSize);
	}

	public @NotNull ItemStack build() {
		ItemStack item = ItemStack.of(this.material, 1);
		item.addUnsafeEnchantments(this.enchantments);
		item.editMeta(this::buildMeta);
		item.setAmount(this.amount);
		item.setData(DataComponentTypes.TOOLTIP_DISPLAY, TooltipDisplay.tooltipDisplay().hiddenComponents(this.hiddenDataComponents).build());
		return item;
	}

	public @NotNull ItemStack buildUnmodifiable() {
		return ItemUtils.unmodifiableItemStack(this.build());
	}

	@Override
	public @NotNull String toString() {
		return ItemUtils.toString(this.build());
	}

	public static @NotNull ItemBuilder of(@NotNull ItemStack item) {
		//ItemBuilder builder = create(item.getType(), item.getAmount(), null);
		ItemBuilder builder = new ItemBuilder(item.getType(), item.getAmount(), null);
		builder.setEnchantments(item.getEnchantments());
		ItemMeta meta = item.getItemMeta();
		if (meta == null)
			return builder;
		if (meta.hasDisplayName())
			builder.setDisplayName(ItemUtils.COMPONENT_SERIALIZER.serialize(Objects.requireNonNull(meta.displayName())));
		if (meta.hasLore())
			builder.setLore(Objects.requireNonNull(meta.lore()).stream().map(ItemUtils.COMPONENT_SERIALIZER::serialize).toList());
		builder.setFlags(meta.getItemFlags());
		builder.setUnbreakable(meta.isUnbreakable());
		if (meta.hasAttributeModifiers())
			builder.setAttributes(meta.getAttributeModifiers());
		builder.setGlint(meta.getEnchantmentGlintOverride());
		return builder;
	}

	public static @NotNull ItemBuilder fromString(@NotNull String item) {
		return of(ItemUtils.fromString(item));
	}
/*
	private static final @NotNull Map<@NotNull Material, @NotNull Constructor<? extends ItemBuilder>> builders = new HashMap<>();
	private static final @NotNull Class<?> @NotNull[] constructorTypes = { Material.class, int.class, String.class, List.class };

	public static @NotNull ItemBuilder create(@NotNull Material material) {
		return create(material, 1);
	}

	public static @NotNull ItemBuilder create(@NotNull Material material, int amount) {
		return create(material, amount, null);
	}

	public static @NotNull ItemBuilder create(@NotNull Material material, int amount, @Nullable String displayName, @NotNull String @Nullable... lore) {
		return create(material, amount, displayName, lore == null || lore.length == 0 ? null : Arrays.asList(lore));
	}

	public static @NotNull ItemBuilder create(@NotNull Material material, int amount, @Nullable String displayName, @Nullable List<@NotNull String> lore) {
		try {
			Constructor<? extends ItemBuilder> builder = builders.get(material);
			return builder.newInstance(material, amount, displayName, lore);
		} catch (ReflectiveOperationException | NullPointerException e) {
			return new ItemBuilder(material, amount, displayName, lore);
		}
	}

	protected static void addMaterialBuilder(@NotNull Material material, @NotNull Class<? extends ItemBuilder> builder) {
		try {
			builders.put(material, builder.getDeclaredConstructor(constructorTypes));
		} catch (NoSuchMethodException e) {
			throw new IllegalArgumentException("Item builder class doesn't have a valid constructor: " + builder);
		}
	}*/
}
