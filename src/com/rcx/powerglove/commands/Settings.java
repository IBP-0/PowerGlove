package com.rcx.powerglove.commands;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class Settings extends Command {

	public static Map<String, Setting> settings = new HashMap<String, Setting>();
	static JSONObject config;
	String avatarUrl = "https://cdn.discordapp.com/avatars/439435998078959616/94941ff09437eef86861c579e8b5a6fb.png";

	MessageEmbed embed = new EmbedBuilder().setColor(0x419399).setAuthor("Power Glove Settings", null, avatarUrl).setThumbnail(avatarUrl).appendDescription(
			"Here's a list of all the settings:\n"
					+ "\n\u2022 **prefix:** Add an alternate prefix for the bot to use."
					+ "\n\u2022 **talktobots [true/false]:** If the bot should respond to other bots. default: false"
					+ "\n\u2022 **reset [setting name or \"all\"]:** Reset a specific setting or all of them.").build();

	@SuppressWarnings("unchecked")
	public Settings() {
		try {
			config = (JSONObject) new JSONParser().parse(new FileReader("serversettings.json"));
		} catch (IOException | ParseException e) {
			config = new JSONObject();
		}

		config.putIfAbsent("default", new JSONObject());

		reloadSettings();
	}

	@Override
	public void execute(String[] arguments, MessageReceivedEvent event) {
		if (!event.getAuthor().equals(event.getGuild().getOwner().getUser())) {
			event.getChannel().sendMessage("Only the owner of this server can change the settings.").queue();
			return;
		}

		if (arguments.length < 2) {
			event.getChannel().sendMessage(embed).queue();
			return;
		}

		if (arguments.length < 3) {
			event.getChannel().sendMessage("Not enough arguments.").queue();
			return;
		}

		if (arguments[1].equals("prefix")) {
			changeSetting("prefix", arguments[2], event);
		} else if (arguments[1].equals("talktobots")) {
			if (arguments[2].equals("true") || arguments[2].equals("false"))
				changeSetting("talktobots", arguments[2], event);
			else
				event.getChannel().sendMessage("Setting can only be true or false.").queue();
		} else if (arguments[1].equals("reset")) {
			String serverID = event.getGuild().getId();
			if (arguments[2].equals("all")) {
				if (!config.containsKey(serverID)) {
					event.getChannel().sendMessage("No settings could be found for this server.").queue();
					return;
				}
				config.remove(serverID);
				event.getChannel().sendMessage("Reset all settings for this server.").queue();
			} else {
				if (!config.containsKey(serverID)) {
					event.getChannel().sendMessage("No settings could be found for this server.").queue();
					return;
				}
				JSONObject server = (JSONObject) config.get(serverID);
				if (!server.containsKey(arguments[2])) {
					event.getChannel().sendMessage("That setting does not exist or it has already been reset.").queue();
					return;
				}
				server.remove(arguments[2]);
				if (server.keySet().isEmpty())
					config.remove(serverID);
				event.getChannel().sendMessage("Setting \"" + arguments[2] + "\" has been reset.").queue();
			}
			reloadSettings();
		} else {
			event.getChannel().sendMessage("Unknown setting.").queue();
		}
	}

	@SuppressWarnings("unchecked")
	public void changeSetting(String setting, String value, MessageReceivedEvent event) {
		String serverID = event.getGuild().getId();

		config.putIfAbsent(serverID, new JSONObject());
		JSONObject server = (JSONObject) config.get(serverID);
		server.put(setting, value);
		reloadSettings();

		event.getChannel().sendMessage("Setting \"" + setting + "\" has been set to \"" + value + "\"").queue();
	}

	@SuppressWarnings("unchecked")
	public void reloadSettings() {
		FileWriter fileWriter;
		try {
			fileWriter = new FileWriter("serversettings.json");
			settings = new HashMap<String, Setting>();
			for (String serverID : (Set<String>) config.keySet()) {
				JSONObject server = (JSONObject) config.get(serverID);
				if (serverID.equals("default")) {
					server.putIfAbsent("prefix", "pow ");
					server.putIfAbsent("talktobots", Boolean.toString(false));
				}
				settings.put(serverID, new Setting((String) server.getOrDefault("prefix", "pow "), Boolean.parseBoolean((String) server.getOrDefault("talktobots", "false"))));
			}
			fileWriter.write(new org.json.JSONObject(config.toJSONString()).toString(4));
			fileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public class Setting {
		public String prefix;
		public boolean talktobots;

		public Setting(String prefix, boolean talktobots) {
			this.prefix = prefix;
			this.talktobots = talktobots;
		}
	}
}