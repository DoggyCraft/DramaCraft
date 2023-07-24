package com.dogonfire.dramacraft.votes;

import com.dogonfire.dramacraft.DramaCraft;
import com.dogonfire.dramacraft.RankManager;
import com.dogonfire.dramacraft.RevolutionManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Random;

public class NoneVote extends Vote {
    private final Random random				= new Random();

    public NoneVote(World world, Player voter, String voteText, boolean vote) {
        super(world, voter, voteText, vote);
    }

    public NoneVote() {
        this(null, null, "", false);
    }

    @Override
    public void successAction() {}
    @Override
    public void failedAction() {}

    @Override
    public boolean checkVote() {
        String broadcast = "";
        if(RevolutionManager.isRevolution())
        {
            Bukkit.broadcastMessage(ChatColor.GRAY + "Revolution!! Will the King and Queen survive the attack by the rebels?");
            Bukkit.broadcastMessage(ChatColor.GRAY + "The Revolution will end in " + ChatColor.GOLD + RevolutionManager.getMinutesUntilRevolutionEnd() + " minutes.");
            return false;
        }

        switch (random.nextInt(20)) {
            case 0:
                if (RankManager.getKingName() != null) {
                    broadcast = "Hil vores konge, hans majestæt " + ChatColor.GOLD + RankManager.getKingName() + " kongen af DoggyCraft!";
                }
                break;
            case 1:
                if (RankManager.getQueenName() != null) {
                    broadcast = "Hil vores dronning, hendes majestæt " + ChatColor.GOLD + RankManager.getQueenName() + " dronningen af DoggyCraft!";
                }
                break;
        }

        if(broadcast.length() > 0)
        {
            DramaCraft.broadcastMessage(broadcast);
        }

        return false;
    }
}
