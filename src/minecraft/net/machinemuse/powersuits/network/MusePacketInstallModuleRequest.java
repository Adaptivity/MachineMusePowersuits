/**
 * 
 */
package net.machinemuse.powersuits.network;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.List;

import net.machinemuse.powersuits.item.ItemUtils;
import net.machinemuse.powersuits.powermodule.ModuleManager;
import net.machinemuse.powersuits.powermodule.PowerModule;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import universalelectricity.core.implement.IItemElectric;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;

/**
 * Packet for requesting to purchase an upgrade. Player-to-server. Server
 * decides whether it is a valid upgrade or not and replies with an associated
 * inventoryrefresh packet.
 * 
 * @author MachineMuse
 * 
 */
public class MusePacketInstallModuleRequest extends MusePacket {
	protected ItemStack stack;
	protected int itemSlot;
	protected String moduleName;

	/**
	 * Constructor for sending this packet.
	 * 
	 * @param player
	 *            Player making the request
	 * @param itemSlot
	 *            Slot containing the item for which the upgrade is requested
	 * @param moduleName
	 */
	public MusePacketInstallModuleRequest(Player player, int itemSlot,
			String moduleName) {
		super(player);
		writeInt(itemSlot);
		writeString(moduleName);
	}

	/**
	 * Constructor for receiving this packet.
	 * 
	 * @param player
	 * @param data
	 * @throws IOException
	 * 
	 */
	public MusePacketInstallModuleRequest(DataInputStream data, Player player) {
		super(player, data);
		itemSlot = readInt();
		moduleName = readString(64);
		Side side = FMLCommonHandler.instance().getEffectiveSide();
		if (side == Side.SERVER) {
			EntityPlayerMP srvplayer = (EntityPlayerMP) player;
			stack = srvplayer.inventory.getStackInSlot(itemSlot);
		}
	}

	@Override
	public void handleServer(EntityPlayerMP playerEntity) {
		if (moduleName != null) {
			InventoryPlayer inventory = playerEntity.inventory;
			int entityId = playerEntity.entityId;
			PowerModule moduleType = ModuleManager.getModule(moduleName);
			List<ItemStack> cost = moduleType.getInstallCost();

			if (ItemUtils.hasInInventory(cost, playerEntity.inventory)) {
				List<Integer> slots = ItemUtils.findInInventoryForCost(cost, playerEntity.inventory);
				double amps = 0;
				double volts = ItemUtils.getAsModular(stack.getItem()).getVoltage(stack);
				for (Integer slot : slots) {
					ItemStack stackInSlot = playerEntity.inventory.getStackInSlot(slot);
					if (stackInSlot != null && stackInSlot.getItem() instanceof IItemElectric) {
						IItemElectric electricItem = (IItemElectric) stackInSlot.getItem();
						amps = electricItem.getJoules(stackInSlot) / volts;
					}
				}
				List<Integer> slotsToUpdate = ItemUtils.deleteFromInventory(
						cost, inventory);
				ItemUtils.itemAddModule(stack, moduleType);
				slots.add(this.itemSlot);
				ItemUtils.getAsModular(stack.getItem()).onReceive(amps, volts, stack);
				for (Integer slotiter : slotsToUpdate) {
					MusePacket reply = new MusePacketInventoryRefresh(
							player,
							slotiter, inventory.getStackInSlot(slotiter));
					PacketDispatcher.sendPacketToPlayer(reply.getPacket250(),
							player);
				}
			}
		}
	}

	@Override
	public void handleClient(EntityClientPlayerMP player) {
		// TODO Auto-generated method stub

	}

}