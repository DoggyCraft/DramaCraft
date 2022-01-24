package com.dogonfire.dramacraft.votes;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.dogonfire.dramacraft.VoteManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.dogonfire.dramacraft.DramaCraft;
import com.dogonfire.dramacraft.LanguageManager;
import com.dogonfire.dramacraft.LanguageManager.LANGUAGESTRING;

public abstract class Vote
{
	public String		voteType			= "NONE";

	public float		reqYesPercentage    = ((float) DramaCraft.instance().requiredYesPercentage)/100f;
	public int 			reqVotes 			= DramaCraft.instance().requiredVotes;
	public int 			voteCost 			= DramaCraft.instance().startVoteCost;
	public int 			votePayment 		= DramaCraft.instance().votePayment;
	public String		voteText;

	public long			startVoteTime		= 0;
	private long		lastVoteCheckTime	= 0;

	public World		world;
	public Player 		voter;
	public List<UUID>	yes					= new ArrayList<>();
	public List<UUID>	no					= new ArrayList<>();

	/**
	 * Creates a new vote.
	 *
	 * @param world  			The world the player is in.
	 * @param voter  			The one doing the vote.
	 * @param voteText 			Optional vote text.
	 * @param vote  			Whether they are voting yes or no.
	 */
	public Vote(World world, Player voter, String voteText, boolean vote) {
		this.world = world;
		this.voter = voter;
		this.voteText = voteText;

		if (voter != null) {
			if (vote) {
				yes.add(voter.getUniqueId());
			}
			else {
				no.add(voter.getUniqueId());
			}
		}
	}

	/**
	 * Creates a new vote.
	 *
	 * @param voteType			The type of the vote (ex. DAY, KING, NOBLE. The strings used in the Language files).
	 * @param world  			The world the player is in.
	 * @param voter  			The one doing the vote.
	 * @param voteText 			Optional vote text.
	 * @param vote  			Whether they are voting yes or no.
	 */
	public Vote(String voteType, World world, Player voter, String voteText, boolean vote) {
		this(world, voter, voteText, vote);

		this.voteType = voteType;
	}

	/**
	 * Creates a new vote.
	 *
	 * @param voteType			The type of the vote (ex. DAY, KING, NOBLE. The strings used in the Language files).
	 * @param reqYesPercentage	Amount of votes required to be yes (This is a FLOAT. So 60% => 0.6f).
	 * @param reqVotes			Amount of votes requires, at least.
	 * @param voteCost 			Cost to create the vote.
	 * @param votePayment 		Amount to pay every player that votes.
	 * @param world  			The world the player is in.
	 * @param voter  			The one doing the vote.
	 * @param voteText 			Optional vote text.
	 * @param vote  			Whether they are voting yes or no.
	 */
	public Vote(String voteType, float reqYesPercentage, int reqVotes, int voteCost, int votePayment, World world, Player voter, String voteText, boolean vote) {
		this(voteType, world, voter, voteText, vote);

		this.reqYesPercentage = reqYesPercentage;
		this.reqVotes = reqVotes;
		this.voteCost = voteCost;
		this.votePayment = votePayment;
	}

	/**
	 * Creates a new vote.
	 *
	 * @param voteType			The type of the vote (ex. DAY, KING, NOBLE. The strings used in the Language files).
	 * @param reqYesPercentage	Amount of votes required to be yes (This is a DOUBLE. So 60% => 60).
	 * @param reqVotes			Amount of votes requires, at least.
	 * @param voteCost 			Cost to create the vote.
	 * @param votePayment 		Amount to pay every player that votes.
	 * @param world  			The world the player is in.
	 * @param voter  			The one doing the vote.
	 * @param voteText 			Optional vote text.
	 * @param vote  			Whether they are voting yes or no.
	 */
	public Vote(String voteType, double reqYesPercentage, int reqVotes, int voteCost, int votePayment, World world, Player voter, String voteText, boolean vote) {
		this(voteType, ((float) reqYesPercentage)/100f, reqVotes, voteCost, votePayment, world, voter, voteText, vote);
	}

	public boolean timedOut() {
		return System.currentTimeMillis() - startVoteTime > (DramaCraft.instance().voteLengthSeconds * 1000);
	}

	public boolean enoughVotes() {
		return this.yes.size() + this.no.size() >= reqVotes;
	}

	public boolean voteSuccess() {
		boolean success = ((float)this.yes.size()) / ((float)(this.no.size() + this.yes.size())) >= reqYesPercentage;

		DramaCraft.logDebug("Success " + ((float)this.yes.size()) / ((float)(this.no.size() + this.yes.size())));
		DramaCraft.logDebug("reqYesPercentage " + reqYesPercentage);
		DramaCraft.logDebug("TimedOut: " + timedOut());

		return (success && enoughVotes());
	}
	
	public boolean voteFinished()
	{
		// TODO: should we let the voting continue no matter what until the vote times out..?
		return voteSuccess() || timedOut();
	}

	public void finishVote() {
		if (voteFinished()) {
			String broadcast = LanguageManager.getLanguageString(LANGUAGESTRING.VOTE_BROADCAST_FINISHED, ChatColor.AQUA);

			DramaCraft.broadcastMessage(broadcast);

			if (this.yes.size() + this.no.size() < reqVotes) {
				broadcast = LanguageManager.getLanguageString(LANGUAGESTRING.VOTE_BROADCAST_NOT_ENOUGH_VOTES, ChatColor.RED);
				DramaCraft.broadcastMessage(broadcast);
			}

			boolean success = voteSuccess();
			broadcastFinishVote(success);
			if (success) {
				successAction();
			}
			else {
				failedAction();
			}

			VoteManager.resetVotes();
		}
	}
	
	public void broadcastProgress()
	{
		broadcastVote();

		int percent = 100;
		percent = (int) (100.0F * yes.size() / (yes.size() + no.size()));

		LanguageManager.setAmount1(percent);
		LanguageManager.setAmount2(yes.size() + no.size());
		LanguageManager.setAmount3(reqVotes);

		String broadcast = LanguageManager.getLanguageString(LANGUAGESTRING.VOTE_BROADCAST_PROGRESS, ChatColor.AQUA);
		DramaCraft.broadcastMessage(broadcast);

		broadcast = LanguageManager.getLanguageString(LANGUAGESTRING.VOTE_BROADCAST_HELP, ChatColor.AQUA);
		DramaCraft.broadcastMessage(broadcast);
	}

	/**
	 * The generic broadcast message for the vote.
	 */
	public void broadcastVote() {
		DramaCraft.broadcastMessage(LanguageManager.getLanguageString(LANGUAGESTRING.valueOf("VOTE_BROADCAST_"+voteType), ChatColor.AQUA));
	}

	/**
	 * The broadcast message that's sent at the start of a vote.
	 */
	public void broadcastBeginVote() {
		LanguageManager.setPlayerName(voter.getName());
		DramaCraft.broadcastMessage(LanguageManager.getLanguageString(LANGUAGESTRING.valueOf("NEW_VOTE_BROADCAST_"+voteType), ChatColor.WHITE));
	}

	/**
	 * The broadcast that's sent at the end of the vote.
	 *
	 * @param success	Whether the vote succeeded or not.
	 */
	public void broadcastFinishVote(boolean success) {
		LanguageManager.setPlayerName(voteText);
		if (success) {
			DramaCraft.broadcastMessage(LanguageManager.getLanguageString(LANGUAGESTRING.valueOf("VOTE_BROADCAST_"+voteType+"_SUCCESS"), ChatColor.GREEN));
		}
		else {
			DramaCraft.broadcastMessage(LanguageManager.getLanguageString(LANGUAGESTRING.valueOf("VOTE_BROADCAST_"+voteType+"_FAILED"), ChatColor.RED));
		}
	}

	/**
	 * Checks if the player has enough money. Will send them a message.
	 * To be used in tryStartVote().
	 *
	 * @return true if they have enough money.
	 */
	public boolean creatorHasEnoughMoney() {
		// check if they have enough wanks to pay
		if (DramaCraft.economy.getBalance(voter) < voteCost)
		{
			LanguageManager.setAmount1(voteCost);
			String message = LanguageManager.getLanguageString(LANGUAGESTRING.ERROR_NOTENOUGHMONEY, ChatColor.RED);
			voter.sendMessage(message);
			DramaCraft.logDebug(voter.getName() + " tried to start a vote again, but did not have money for it");
			return false;
		}
		return true;
	}

	public void withdrawVoteCost() {
		LanguageManager.setAmount1(voteCost);
		String message = LanguageManager.getLanguageString(LANGUAGESTRING.VOTE_COST, ChatColor.AQUA);
		voter.sendMessage(message);

		DramaCraft.economy.withdrawPlayer(voter, voteCost);
	}

	public boolean enoughOnlinePlayers() {
		if(Bukkit.getServer().getOnlinePlayers().size() < reqVotes)
		{
			LanguageManager.setAmount1(reqVotes);
			voter.sendMessage(LanguageManager.getLanguageString(LANGUAGESTRING.ERROR_TOOFEWPLAYERS, ChatColor.RED));
			DramaCraft.logDebug(voter.getName() + " tried to vote, but there are too few players online");
			return false;
		}
		return true;
	}

	/**
	 * Can be used to check special conditions, like if the player creating the vote is a rebel or not.
	 * Can be used, instead of having to overwrite the tryStartVote() function.
	 *
	 * @return Whether the special conditions were fulfilled.
	 */
	public boolean checkSpecialConditions() {
		return true;
	}

	/**
	 * Try to start the vote.
	 *
	 * @return If the vote can be started.
	 */
	public boolean tryStartVote() {
		if (!creatorHasEnoughMoney()) {
			return false;
		}

		if (!enoughOnlinePlayers()) {
			return false;
		}

		if (!checkSpecialConditions()) {
			return false;
		}

		broadcastBeginVote();
		withdrawVoteCost();

		return true;
	}

	/**
	 * This is called periodically while the vote is running
	 *
	 * @return Whether the vote is over/finished.
	 */
	public boolean checkVote() {
		if (voteFinished()) {
			finishVote();
			return true;
		}
		else {
			if (System.currentTimeMillis() - lastVoteCheckTime < (DramaCraft.instance().voteBroadcastSeconds * 1000L)) {
				return false;
			}
			broadcastProgress();
			lastVoteCheckTime = System.currentTimeMillis();
			return false;
		}
	}

	/**
	 * Whether the voter fulfills the requirements to vote. For example, rebels for rebel votes and imp for imp votes.
	 *
	 * @param votingPlayer The player trying to vote.
	 * @return Whether they can vote.
	 */
	public boolean fulfillsVoteRequirement(Player votingPlayer) {
		return true;
	}

	/**
	 * This is called when a player is voting yes or no towards this vote.
	 *
	 * @param world	The world the player is in.
	 * @param votingPlayer The player that voted.
	 * @param vote  Whether they voted yes or no.
	 */
	public void doVote(World world, Player votingPlayer, boolean vote) {
		if (!fulfillsVoteRequirement(votingPlayer)) {
			return;
		}

		boolean firstVote = true;
		if (vote)
		{
			votingPlayer.sendMessage("You voted yes");
			if (!this.yes.contains(votingPlayer.getUniqueId()))
			{
				this.yes.add(votingPlayer.getUniqueId());
			}
			else
			{
				firstVote = false;
			}

			if (this.no.contains(votingPlayer.getUniqueId()))
			{
				this.no.remove(votingPlayer.getUniqueId());
				firstVote = false;
			}
		}
		else
		{
			votingPlayer.sendMessage("You voted no");

			if (this.yes.contains(votingPlayer.getUniqueId()))
			{
				this.yes.remove(votingPlayer.getUniqueId());
				firstVote = false;
			}

			if (!this.no.contains(votingPlayer.getUniqueId()))
			{
				this.no.add(votingPlayer.getUniqueId());
			}

			else
			{
				firstVote = false;
			}
		}

		if (firstVote)
		{
			LanguageManager.setAmount1(votePayment);
			String message = LanguageManager.getLanguageString(LANGUAGESTRING.VOTE_PAYMENT, ChatColor.AQUA);

			votingPlayer.sendMessage(message);

			DramaCraft.economy.depositPlayer(votingPlayer, votePayment);
		}
	}

	public abstract void successAction();
	public abstract void failedAction();
}