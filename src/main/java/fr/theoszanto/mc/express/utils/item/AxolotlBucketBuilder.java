package fr.theoszanto.mc.express.utils.item;

import fr.theoszanto.mc.express.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Axolotl;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AxolotlBucketBuilder extends ItemBuilder {
	/*static {
		addMaterialBuilder(Material.AXOLOTL_BUCKET, AxolotlBucketBuilder.class);
	}*/

	private @Nullable Axolotl.Variant variant;

	protected AxolotlBucketBuilder(@NotNull Material material, int amount, @Nullable String displayName, @Nullable List<@NotNull String> lore) {
		super(material, amount, displayName, lore);
	}

	public @Nullable Axolotl.Variant getVariant() {
		return this.variant;
	}

	@Contract(value = "_ -> this", mutates = "this")
	public @NotNull AxolotlBucketBuilder setVariant(@Nullable Axolotl.Variant variant) {
		this.variant = variant;
		return this;
	}

	public boolean hasVariant() {
		return this.variant != null;
	}
}
