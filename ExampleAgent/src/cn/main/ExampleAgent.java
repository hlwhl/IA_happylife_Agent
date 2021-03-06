package cn.main;

import negotiator.AgentID;
import negotiator.Bid;
import negotiator.actions.Accept;
import negotiator.actions.Action;
import negotiator.actions.Offer;
import negotiator.parties.AbstractNegotiationParty;
import negotiator.parties.NegotiationInfo;

import java.util.List;

import agents.ai2014.group11.OpponentBidHistory;
import agents.anac.y2012.MetaAgent.agents.WinnerAgent.opponentOffers;

/** ExampleAgent returns the bid that maximizes its own utility for half of the negotiation session. In the second half,
 * it offers a random bid. It only accepts the bid on the table in this phase, if the utility of the bid is higher than
 * Example Agent's last bid. */
public class ExampleAgent extends AbstractNegotiationParty {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final String description = "Example Agent";

	private Bid lastReceivedOffer; // offer on the table
	private Bid myLastOffer;
	private List<Bid> OpponentBidHistory;

	@Override
	public void init(NegotiationInfo info) {
		super.init(info);
	}

	/** When this function is called, it is expected that the Party chooses one of the actions from the possible action
	 * list and returns an instance of the chosen action.
	 *
	 * @param list
	 * @return */
	@Override
	public Action chooseAction(List<Class<? extends Action>> list) {
		//System.out.println("helloworld66"+list);
		// According to Stacked Alternating Offers Protocol list includes
		// Accept, Offer and EndNegotiation actions only.
		double time = getTimeLine().getTime(); // Gets the time, running from t = 0 (start) to t = 1 (deadline).
												// The time is normalized, so agents need not be
												// concerned with the actual internal clock.
		System.out.println(time);
//		saveOpponentsOffers(lastReceivedOffer);
		// First half of the negotiation offering the max utility (the best agreement possible) for Example Agent
		if (1 == 1) {
			if (lastReceivedOffer != null && this.utilitySpace.getUtility(lastReceivedOffer) == this.utilitySpace.getUtility(this.getMaxUtilityBid())) {
				return new Accept(this.getPartyId(), lastReceivedOffer); 
			}
			return new Offer(this.getPartyId(), this.getMaxUtilityBid());
		} else {

			// Accepts the bid on the table in this phase,
			// if the utility of the bid is higher than Example Agent's last bid.
			if (lastReceivedOffer != null && myLastOffer != null
					&& this.utilitySpace.getUtility(lastReceivedOffer) > this.utilitySpace.getUtility(myLastOffer)) {

				return new Accept(this.getPartyId(), lastReceivedOffer);
			} else {
				// Offering a random bid
				myLastOffer = generateRandomBid();
				return new Offer(this.getPartyId(), myLastOffer);
			}
		}
	}

	/** This method is called to inform the party that another NegotiationParty chose an Action.
	 * 
	 * @param sender
	 * @param act */
	@Override
	public void receiveMessage(AgentID sender, Action act) {
		super.receiveMessage(sender, act);

		if (act instanceof Offer) { // sender is making an offer
			Offer offer = (Offer) act;

			// storing last received offer
			lastReceivedOffer = offer.getBid();
		}
	}

	/** A human-readable description for this party.
	 * 
	 * @return */
	@Override
	public String getDescription() {
		return description;
	}

	private Bid getMaxUtilityBid() {
		try {
			return this.utilitySpace.getMaxUtilityBid();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private void saveOpponentsOffers(Bid b) {
		OpponentBidHistory.add(b);
	}
}
