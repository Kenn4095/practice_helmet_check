package com.helmetcheck;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.PlayerSpawned;
import net.runelite.api.events.PlayerChanged;
import net.runelite.api.kit.KitType;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

@Slf4j
@PluginDescriptor(
	name = "Helmet Checker"
)
public class HelmetCheckPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private HelmetCheckConfig config;

	@Override
	protected void startUp() throws Exception
	{
		log.info("Example started!");
	}

	@Override
	protected void shutDown() throws Exception
	{
		log.info("Example stopped!");
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		if (gameStateChanged.getGameState() == GameState.LOGGED_IN)
		{
			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Example says " + config.greeting(), null);
			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Let me check if you're wearing a helmet.", null);

		}
	}

	@Subscribe
	public void onPlayerSpawned(PlayerSpawned event)
	{
		final Player player = event.getPlayer();
		if (client.getLocalPlayer().equals(player))
		{
			if(!checkHelmet(player)){
				client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "YOU AREN'T WEARING A HELMET!", null);
			}else{
				client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Oh thank goodness, you're wearing a helmet.", null);
			}
		}
	}

	@Subscribe
	public void onPlayerChanged(PlayerChanged event){
		final Player player = event.getPlayer();
		if(client.getLocalPlayer().equals(player)){
			if(!checkHelmet(player)){
				client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Why did you take off your helmet?", null);
			}else{
				client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Thank you for putting on your helmet!", null);
			}
		}
	}

	private boolean checkHelmet(Player player){
		if(player.getPlayerComposition().getEquipmentId(KitType.HEAD) == -1){
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
