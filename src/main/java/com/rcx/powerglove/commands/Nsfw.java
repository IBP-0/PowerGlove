package com.rcx.powerglove.commands;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Random;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.rcx.powerglove.PowerGlove;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class Nsfw extends Command {

	Random rand = new Random();

	public static String[] message = {
			"No, just no.",
			"Get out of here, right now.",
			"*[Dissapointment intensifies]*",
			"Fricken horny teenagers.",
			"Ker-prank'd",
			"Stop being such a sex addict and play some video games.",
			"Hahaha, no.",
			"This is why we can't have nice things.",
			"You lost 23 power tokens and my respect for using that command.",
			"Jimmies.setRustled(true);",
			"Thank you for participating in this experiment, you have been globally blacklisted from life. Have a nice day.",
			"Pathetic.",
			"Loser, you're a loser, are you feeling sorry for yourself? Well you should be cause you are dirt. You make me sick you big baby. Baby want a bottle? A big dirt bottle?",
			"I don't want to live on this planet anymore.",
			"Nothing personal but, I hate you.",
			"I'm done.",
			"Never gonna give you up, never gonna let you down."
	};

	@Override
	public void execute(String[] arguments, MessageReceivedEvent event) {
		if (!event.getTextChannel().isNSFW()) {
			event.getChannel().sendMessage("This command can only be used in channels marked as NSFW.").queue();
			return;
		}
		JSONObject user = getListcordVote(event.getAuthor().getId());
		if (user != null) {
			Timestamp stamp = new Timestamp(Calendar.getInstance().getTime().getTime());
			Timestamp stamp2 = new Timestamp((long) user.get("nextVote"));
			if (stamp.before(stamp2)) {
				event.getChannel().sendMessage(message[rand.nextInt(message.length)]).queue();
				return;
			}
		}
		event.getChannel().sendMessage("Please vote for Power Glove on listcord to get access to this command\nhttps://listcord.com/bot/439435998078959616").queue();
	}

	public JSONObject getListcordVote(String id) {
		try {
			HttpURLConnection site = (HttpURLConnection) new URL("https://listcord.com/api/bot/439435998078959616/votes/" + id).openConnection();
			site.setRequestProperty("User-Agent", "PowerGlove");
			site.setRequestProperty("Content-Type", "application/json");
			site.setRequestProperty("Authorization", PowerGlove.listcordToken);
			site.setRequestMethod("GET");
			site.setDoOutput(true);
			site.setDoInput(true);
			site.connect();

			DataInputStream rd = new DataInputStream (site.getInputStream());
			BufferedReader d = new BufferedReader(new InputStreamReader(rd));
			String line;
			String lines = "";
			while ((line = d.readLine()) != null) {
				lines += line;
				System.out.println(line);
			}
			d.close();
			rd.close();
			site.disconnect();
			return (JSONObject) new JSONParser().parse(lines);
		} catch (Exception e) {
			return null;
		}
	}
}
