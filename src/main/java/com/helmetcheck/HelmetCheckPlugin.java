package com.helmetcheck;

import com.google.inject.Provides;
import java.awt.image.BufferedImage;
import javax.inject.Inject;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.ItemComposition;
import net.runelite.api.ItemContainer;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.Player;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.PlayerSpawned;
import net.runelite.client.callback.ClientThread;
import net.runelite.api.kit.KitType;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;

@PluginDescriptor(
	name = "Helmet Checker"
)
public class HelmetCheckPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private InfoBoxManager infoBoxManager;

	@Inject
	private ItemManager itemManager;

	private HelmetBox helmetBox;

	@Inject
	private HelmetCheckConfig config;

	@Override
	protected void startUp() throws Exception
	{
		clientThread.invokeLater(() -> {
			final ItemContainer container = client.getItemContainer(InventoryID.EQUIPMENT);
			if (container != null)
			{
				checkInventory(container.getItems());
			}
		});
	}

	@Override
	protected void shutDown() throws Exception
	{
		infoBoxManager.removeInfoBox(helmetBox);
		helmetBox = null;
	}

	@Subscribe
	public void onItemContainerChanged(ItemContainerChanged event)
	{
		if (event.getItemContainer() != client.getItemContainer(InventoryID.EQUIPMENT))
		{
			return;
		}

		checkInventory(event.getItemContainer().getItems());
	}

	private void checkInventory(final Item[] items)
	{
		if (items.length <= EquipmentInventorySlot.HEAD.getSlotIdx())
		{
			removeInfobox();
			return;
		}

		final Item head = items[EquipmentInventorySlot.HEAD.getSlotIdx()];
		final ItemComposition comp = itemManager.getItemComposition(head.getId());

		updateInfobox(head, comp);
	}

	private void updateInfobox(final Item item, final ItemComposition comp)
	{
		removeInfobox();
		final BufferedImage image = itemManager.getImage(item.getId(), 5, false);
		helmetBox = new HelmetBox(this, item.getId(), comp.getName(), image);
		infoBoxManager.addInfoBox(helmetBox);
	}

	private void removeInfobox()
	{
		infoBoxManager.removeInfoBox(helmetBox);
		helmetBox = null;
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		if (gameStateChanged.getGameState() == GameState.LOGGED_IN)
		{
			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Let me check if you're wearing a helmet.", null);

		}
	}

	@Subscribe
	public void onPlayerSpawned(PlayerSpawned event)
	{
		final Player player = event.getPlayer();
		if (client.getLocalPlayer().equals(player))
		{
			if (!checkHelmet(player))
			{
				client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "YOU AREN'T WEARING A HELMET!", null);
			}
			else
			{
				client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Oh thank goodness, you're wearing a helmet.", null);
			}
		}
	}

	private boolean checkHelmet(Player player)
	{
		if (player.getPlayerComposition().getEquipmentId(KitType.HEAD) == -1)
		{
			return false;
		}
		return true;
	}

	@Provides
	HelmetCheckConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(HelmetCheckConfig.class);
	}
}
