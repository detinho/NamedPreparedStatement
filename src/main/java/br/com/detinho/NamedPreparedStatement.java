package br.com.detinho;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class NamedPreparedStatement {

	public static NamedPreparedStatement prepareStatement(Connection conn, String string) {
		return new NamedPreparedStatement();
	}

	public PreparedStatement getGeneratedStatement() {
		return null;
	}

}
