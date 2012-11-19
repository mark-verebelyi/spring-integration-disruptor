package org.springframework.integration.disruptor;

public class CustomEvent {

	private volatile Integer marker;
	private volatile Boolean status;
	private volatile Object object;

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

	public Object getObject() {
		return this.object;
	}

	public void setObject(final Object object) {
		this.object = object;
	}

	@Override
	public String toString() {
		return "CustomEvent [marker=" + this.marker + ", status=" + this.status + ", object=" + this.object + "]";
	}

}
