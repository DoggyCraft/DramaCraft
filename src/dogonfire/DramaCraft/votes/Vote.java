package dogonfire.DramaCraft.votes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;


import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;

import dogonfire.DramaCraft.DramaCraft;
import dogonfire.DramaCraft.LanguageManager;
import dogonfire.DramaCraft.LanguageManager.LANGUAGESTRING;
import dogonfire.DramaCraft.VoteManager.VOTE_TYPE;


public abstract class Vote
{
	public VOTE_TYPE 	voteType;
	public float  		reqYesPercentage 	= 0.60f;
	public int 			reqVotes 			= DramaCraft.instance().requiredVotes;
	public int 			voteCost 			= DramaCraft.instance().startVoteCost;
	public long			startVoteTime;
	public List<String>	all					= new ArrayList<String>();
	public List<String>	yes					= new ArrayList<String>();
	public List<String>	no					= new ArrayList<String>();
	
	public boolean isCompleted()
	{			
		if ((this.yes.size() + this.no.size() >= reqVotes) || (System.currentTimeMillis() - this.startVoteTime > DramaCraft.instance().voteLengthSeconds))
		{
			String broadcast = LanguageManager.getLanguageString(LANGUAGESTRING.VOTE_BROADCAST_FINISHED, ChatColor.AQUA);

			DramaCraft.broadcastMessage(broadcast);

			if (this.yes.size() + this.no.size() < reqVotes)
			{
				broadcast = LanguageManager.getLanguageString(LANGUAGESTRING.VOTE_BROADCAST_NOT_ENOUGH_VOTES, ChatColor.RED);
				DramaCraft.broadcastMessage(broadcast);
				//DramaCraft.instance().resetVotes();
				return false;
			}

			boolean success = ((float)this.yes.size()) / ((float)(this.no.size() + this.yes.size())) >= reqYesPercentage;
			
			DramaCraft.logDebug("success " + ((float)this.yes.size()) / ((float)(this.no.size() + this.yes.size())));
			DramaCraft.logDebug("reqYesPercentage/100 " + reqYesPercentage / 100.0);

			broadcast = "MISSING_SUCCESS";
			
			return true;
		}
		
		return false;
	}
	
	public abstract boolean newVote(World world, Player voter, String voteText, boolean vote, VOTE_TYPE voteType); // This is called when a player is trying to start the vote
	public abstract boolean checkVote(int timeFactor);  // This is called periodically while the vote is running
	public abstract boolean tryVote(World world, Player voter, boolean vote, VOTE_TYPE voteType); // This is called when a player is voting yes or no towards this vote		
}