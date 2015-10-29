package org.cern.cms.dbloader.model;

import java.math.BigInteger;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(of = "name")
public class OptId {

	private final BigInteger id;
	private final String name;
	
	public OptId(String value) {
		this.name = value;
		BigInteger i = null;
		try {
			i = new BigInteger(value);
		} catch (NumberFormatException ex) {
			i = null;
		}
		this.id = i;
	}
	
	public boolean hasId() {
		return this.id != null;
	}

	@Override
	public String toString() {
		return this.name.toString();
	}
	
}
