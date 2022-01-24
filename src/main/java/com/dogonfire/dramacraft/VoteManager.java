package com.dogonfire.dramacraft;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;


import com.dogonfire.dramacraft.votes.NoneVote;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.dogonfire.dramacraft.LanguageManager.LANGUAGESTRING;
import com.dogonfire.dramacraft.votes.Vote;


public class VoteManager
{
	public String				name;
	public UUID					lastVoterId 		= UUID.randomUUID();
	private long				lastVoteTime	= 0;

	private VOTE_TYPE			currentVoteType     = VOTE_TYPE.VOTE_NONE;
	private Vote				currentVote			= new NoneVote();

	static private VoteManager 	instance;

	public static enum VOTE_TYPE
	{
		VOTE_NONE,
		VOTE_GENERAL,
		VOTE_DAY,
		VOTE_NIGHT,
		VOTE_RAIN,
		VOTE_SUN,
		VOTE_REVOLUTION,
		VOTE_NOBLE,
		VOTE_NOBLE_KICK,
		VOTE_INNERCIRCLE,
		VOTE_INNERCIRCLE_KICK,
		VOTE_DISABLE_PHANTOMS,
		VOTE_KING,
		VOTE_QUEEN,
		VOTE_RINGLEADER1,
		VOTE_RINGLEADER2,
		VOTE_HOF,
		MASTER_COIN,
		MASTER_LAW,
		MASTER_WAR,
		VOTE_FAME,
		VOTE_SHAME
	}

	private static HashMap<VOTE_TYPE, String> voteRegistry = new HashMap<VOTE_TYPE, String>() {{
		put(VOTE_TYPE.VOTE_NONE, "com.dogonfire.dramacraft.votes.NoneVote");
		put(VOTE_TYPE.VOTE_DAY, "com.dogonfire.dramacraft.votes.general.DayVote");
		put(VOTE_TYPE.VOTE_NIGHT, "com.dogonfire.dramacraft.votes.general.NightVote");
		put(VOTE_TYPE.VOTE_RAIN, "com.dogonfire.dramacraft.votes.general.RainVote");
		put(VOTE_TYPE.VOTE_SUN, "com.dogonfire.dramacraft.votes.general.SunVote");
		put(VOTE_TYPE.VOTE_GENERAL, "com.dogonfire.dramacraft.votes.general.GeneralVote");
		put(VOTE_TYPE.VOTE_DISABLE_PHANTOMS, "com.dogonfire.dramacraft.votes.general.DisablePhantomsVote");
		put(VOTE_TYPE.VOTE_REVOLUTION, "com.dogonfire.dramacraft.votes.drama.RevolutionVote");
		put(VOTE_TYPE.VOTE_NOBLE, "com.dogonfire.dramacraft.votes.drama.NobleVote");
		put(VOTE_TYPE.VOTE_NOBLE_KICK, "com.dogonfire.dramacraft.votes.drama.NobleKickVote");
		put(VOTE_TYPE.VOTE_INNERCIRCLE, "com.dogonfire.dramacraft.votes.drama.InnercircleVote");
		put(VOTE_TYPE.VOTE_INNERCIRCLE_KICK, "com.dogonfire.dramacraft.votes.drama.InnercircleKickVote");
		put(VOTE_TYPE.VOTE_KING, "com.dogonfire.dramacraft.votes.drama.KingVote");
		put(VOTE_TYPE.VOTE_QUEEN, "com.dogonfire.dramacraft.votes.drama.QueenVote");
		put(VOTE_TYPE.VOTE_RINGLEADER1, "com.dogonfire.dramacraft.votes.drama.Ringleader1Vote");
		put(VOTE_TYPE.VOTE_RINGLEADER2, "com.dogonfire.dramacraft.votes.drama.Ringleader2Vote");
	}};
	
	/*
	most
	least
	
	singer
	builder
	funny
	badass
	prettiest
	
	
	boy
	girl
	
	public void setPlayerSign()
	{
		HOF_QUEEN
		HOF_KING
		HOF_CUTEST_GIRL
		HOF_BEST_BUILDER
		HOF_
	}
	*/
		
	VoteManager()
	{
		instance = this;		
	}

	public static void resetVotes()
	{
		instance.currentVote = new NoneVote();
	}
		
	public static void checkVote() {
		instance.currentVote.checkVote();
	}

	public static VOTE_TYPE getCurrentVoteType() {
		return instance.currentVoteType;
	}

	public static Vote getCurrentVote() {
		return instance.currentVote;
	}

	public static boolean newVote(World world, Player voter, String voteText, boolean voteYes, VOTE_TYPE voteType) {
		long voteInterval = DramaCraft.instance().voteSecondsBetween;
		long timeIntervalSeconds = (System.currentTimeMillis() - instance.lastVoteTime)  / (1000);

		if (timeIntervalSeconds < voteInterval)
		{
			int seconds = (int)(instance.lastVoteTime / 1000 + voteInterval) - (int)(System.currentTimeMillis() / 1000);//(int) ((instance.startVoteTime + voteInterval - System.currentTimeMillis()()) / 60000000L); // 60000000L
			LanguageManager.setAmount1(seconds);
			voter.sendMessage(LanguageManager.getLanguageString(LANGUAGESTRING.ERROR_TOOSOON, ChatColor.RED));
			DramaCraft.logDebug(voter.getName() + " tried to start a vote too soon");
			return false;
		}

		Vote vote = null;
		if (voteRegistry.containsKey(voteType)) {
			try {
				Constructor<?> cls = Class.forName(voteRegistry.get(voteType)).getConstructor(World.class, Player.class, String.class, boolean.class);
				vote = (Vote) cls.newInstance(world, voter, voteText, voteYes);
			}
			catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
				DramaCraft.logDebug(e);
			}
		}

		if (vote == null) {
			voter.sendMessage(LanguageManager.getLanguageString(LANGUAGESTRING.ERROR_INVALID_VOTE, ChatColor.RED));
			DramaCraft.logDebug("No vote object found");
			return false;
		}


		if (!vote.tryStartVote()) {
			return false;
		}

		DramaCraft.logDebug("New vote started of type " + vote.getClass().getName());
		instance.currentVote = vote;
		instance.lastVoteTime = System.currentTimeMillis();
		instance.lastVoterId = voter.getUniqueId();

		return true;
	}

	public static void doVote(World world, Player voter, boolean vote)
	{
		instance.currentVote.doVote(world, voter, vote);
	}
}