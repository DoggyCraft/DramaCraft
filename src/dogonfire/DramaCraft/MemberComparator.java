package dogonfire.DramaCraft;

import java.util.Comparator;
import java.util.UUID;

class Member
{
	public UUID PlayerId;
	public long Days;	
	
	public Member(UUID playerId, long days)
	{
		this.PlayerId = playerId;
		this.Days = days;
	}
}


public class MemberComparator implements Comparator<Member>
{
	public MemberComparator()
	{
	}

	public int compare(Member member1, Member member2)
	{
		return (int) (member1.Days - member2.Days);
	}
}
