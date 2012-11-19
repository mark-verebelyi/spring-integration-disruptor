package org.springframework.integration.disruptor;

public class CustomEvent {

	private volatile Integer marker;
	private volatile Boolean status;

	public Integer getMarker() {
		return this.marker;
	}

	public void setMarker(final Integer marker) {
		this.marker = marker;
	}

	public Boolean getStatus() {
		return this.status;
	}

	public void setStatus(final Boolean status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "CustomEvent [marker=" + this.marker + ", status=" + this.status + "]";
	}

}
