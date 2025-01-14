package com.rcx.powerglove.commands;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.rcx.powerglove.PowerGlove;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Cancel implements Command {

	public static Random rand = new Random();

	public static String[] cancelMessages = {
			"what they've done cannot be forgiven.",
			"throw your rotten vegetables at them!",
			"send in the angry twitter users.",
			"ladies and gentlemen, we got 'em.",
			"you'll pay for what you've done.",
			"they're never going to live this down.",
			"they're practically just as evil as hitler.",
			"I'm honestly disgusted.",
	};

	public static String[] uncancelMessages = {
			"all is forgiven.",
			"we've moved on.",
			"the accusations were false.",
			"for now...",
			"we've found other people to be angry at.",
			"now behave okay?",
			"I guess we kinda overreacted a bit.",
			"I know you already lost your job and got kicked out of your house, but we've forgiven you.",
	};

	static JSONObject cancelConfig;
	public static List<String> cancelledUsers = new ArrayList<String>();
	public static List<String> immuneUsers = new ArrayList<String>();

	public Cancel() {
		immuneUsers.add("439435998078959616");
		immuneUsers.add("448759521981235211");
		immuneUsers.add("96664175350149120");
		immuneUsers.add(PowerGlove.id);

		try {
			cancelConfig = (JSONObject) new JSONParser().parse(new FileReader("cancelledusers.json"));
		} catch (IOException | ParseException e) {
			cancelConfig = new JSONObject();
		}

		reloadCancelledUsers();
	}

	@Override
	@SuppressWarnings("unchecked")
	public void execute(String[] arguments, MessageReceivedEvent event) {
		if (event.getMessage().getMentions().getUsers().size() != 1) {
			event.getChannel().sendMessage("You must ping exactly one user to cancel.").queue();
			return;
		}

		User cancellingUser = event.getMessage().getAuthor();
		String cancellerName = event.getGuild().isMember(cancellingUser) ? event.getGuild().getMember(cancellingUser).getEffectiveName() : cancellingUser.getName();

		if (cancelledUsers.contains(cancellingUser.getId())) {
			event.getChannel().sendMessage("Sorry " + cancellerName + ", but you can't cancel/uncancel anyone while you're cancelled yourself.").queue();
			return;
		}

		User cancelledUser = event.getMessage().getMentions().getUsers().get(0);
		String cancelledName = event.getGuild().isMember(cancelledUser) ? event.getGuild().getMember(cancelledUser).getEffectiveName() : cancelledUser.getName();

		if (immuneUsers.contains(cancelledUser.getId())) {
			event.getChannel().sendMessage(cancelledName + " is immune to cancellations.").queue();
			return;
		}

		if (cancellingUser.getId().equals(cancelledUser.getId())) {
			//cancelledUsers.add(cancellingUser.getId());
			cancelConfig.putIfAbsent(cancellingUser.getId(), new JSONObject());
			event.getChannel().sendMessage("Whoops, you cancelled yourself, no turning back now.").queue();
			return;
		}

		if (cancelledUsers.contains(cancelledUser.getId())) {
			cancelConfig.remove(cancelledUser.getId());
			//cancelledUsers.remove(cancelledUser.getId());
			event.getChannel().sendMessage(cancelledName + " is no longer cancelled, " + uncancelMessages[rand.nextInt(uncancelMessages.length)]).queue();
		} else {
			cancelConfig.putIfAbsent(cancelledUser.getId(), new JSONObject());
			//cancelledUsers.add(cancelledUser.getId());
			event.getChannel().sendMessage(cancelledName + " has been cancelled, " + cancelMessages[rand.nextInt(cancelMessages.length)]).queue();
		}

		reloadCancelledUsers();
	}

	@SuppressWarnings("unchecked")
	public void reloadCancelledUsers() {
		FileWriter fileWriter;
		try {
			fileWriter = new FileWriter("cancelledusers.json");
			cancelledUsers = new ArrayList<String>();
			for (String userID : (Set<String>) cancelConfig.keySet()) {
				cancelledUsers.add(userID);
			}
			fileWriter.write(cancelConfig.toJSONString());
			fileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
