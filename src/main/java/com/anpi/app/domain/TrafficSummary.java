package com.anpi.app.domain;

public class TrafficSummary {

	private int direction;
	private long startTime;
	private long endtTime;

	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getEndtTime() {
		return endtTime;
	}

	public void setEndtTime(long endtTime) {
		this.endtTime = endtTime;
	}

}
