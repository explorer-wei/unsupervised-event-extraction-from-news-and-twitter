package model;

import java.util.Date;

public class Event {
	private Topic topic;
	private String who;
	private String when;
	private String where;	
	private Date startDate;
	private Date endDate;
	
	public Event(Topic topic, String who, String when, String where,
			Date startDate, Date endDate) {
		super();
		this.topic = topic;
		this.who = who;
		this.when = when;
		this.where = where;
		this.startDate = startDate;
		this.endDate = endDate;
	}
	
	public Event(Topic topic, String who, String when, String where) {
		super();
		this.topic = topic;
		this.who = who;
		this.when = when;
		this.where = where;
	}
	
	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
	public Topic getTopic() {
		return topic;
	}

	public void setTopic(Topic topic) {
		this.topic = topic;
	}

	public String getWho() {
		return who;
	}

	public void setWho(String who) {
		this.who = who;
	}

	public String getWhen() {
		return when;
	}

	public void setWhen(String when) {
		this.when = when;
	}

	public String getWhere() {
		return where;
	}

	public void setWhere(String where) {
		this.where = where;
	}


}
