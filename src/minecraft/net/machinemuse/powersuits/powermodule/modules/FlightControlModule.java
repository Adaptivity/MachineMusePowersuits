package net.machinemuse.powersuits.powermodule.modules;

import java.util.List;

import net.machinemuse.api.IModularItem;
import net.machinemuse.api.IToggleableModule;
import net.machinemuse.api.MuseCommonStrings;
import net.machinemuse.api.MuseItemUtils;
import net.machinemuse.general.gui.MuseIcon;
import net.machinemuse.powersuits.item.ItemComponent;
import net.minecraft.item.ItemStack;

public class FlightControlModule extends PowerModuleBase implements IToggleableModule {
	public static final String MODULE_FLIGHT_CONTROL = "Flight Control";

	public FlightControlModule(List<IModularItem> validItems) {
		super(validItems);
		addInstallCost(MuseItemUtils.copyAndResize(ItemComponent.controlCircuit, 1));
	}

	@Override
	public MuseIcon getIcon(ItemStack item) {
		return MuseIcon.INDICATOR_1_GREEN;
	}

	@Override
	public String getCategory() {
		return MuseCommonStrings.CATEGORY_SPECIAL;
	}

	@Override
	public String getName() {
		return MODULE_FLIGHT_CONTROL;
	}

	@Override
	public String getDescription() {
		return "An integrated control circuit to help you fly better. Press Z to go down.";
	}

}