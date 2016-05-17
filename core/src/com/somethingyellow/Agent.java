package com.somethingyellow;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.TreeMap;

public class Agent {
	private LinkedList<Statement> _statements;
	private TreeMap<String, List<Statement>> _predicateMap;
	private HashSet<String> _state;
	private Listener _listener;

	public Agent(String[] statementsString) {
		_predicateMap = new TreeMap<String, List<Statement>>();
		_statements = new LinkedList<Statement>();

		addStatements(statementsString);
		resetState();
	}

	public Agent(String[] statementsString, Listener listener) {
		this(statementsString);
		_listener = listener;
	}

	private static Statement ParseStatement(String string) {
		Statement statement;
		LinkedList<Predicate> predicates;

		String[] parts = string.split("->");
		if (parts.length < 2) throw new IllegalArgumentException("Statement should have '->'!");
		if (parts.length > 2) throw new IllegalArgumentException("'->' should only appear once!");

		statement = new Statement();

		// Dealing with premises
		String[] premiseParts = parts[0].split("&");

		predicates = new LinkedList<Predicate>();
		for (String p : premiseParts) {
			p = p.trim();
			if (p.length() == 0) continue;

			if (p.charAt(0) == '!') {
				predicates.add(new Predicate(p.substring(1), true));
			} else {
				predicates.add(new Predicate(p));
			}
		}
		statement.premises = predicates.toArray(new Predicate[predicates.size()]);

		// Dealing with conclusion
		String[] conclusionParts = parts[1].split("&");

		predicates = new LinkedList<Predicate>();
		for (String p : conclusionParts) {
			p = p.trim();
			if (p.length() == 0) continue;

			if (p.charAt(0) == '!') {
				predicates.add(new Predicate(p.substring(1), true));
			} else {
				predicates.add(new Predicate(p));
			}
		}
		statement.conclusions = predicates.toArray(new Predicate[predicates.size()]);

		return statement;
	}

	public Agent resetState() {
		_state = new HashSet<String>();
		return this;
	}

	public Agent set(String id) {
		return set(id, true);
	}

	public Agent set(String id, boolean isTrue) {
		if (isTrue) {
			if (setPredicateTrue(id)) reason(id);
		} else {
			if (setPredicateFalse(id)) reason(id);
		}

		return this;
	}

	public boolean get(String id) {
		return _state.contains(id);
	}

	public Agent addStatements(String[] statementsString) {
		for (String s : statementsString) {
			addStatement(ParseStatement(s));
		}

		return this;
	}

	private void addStatement(Statement statement) {
		_statements.add(statement);

		// add to treemap of predicates based on premises
		for (Predicate p : statement.premises) {
			if (!_predicateMap.containsKey(p.id)) _predicateMap.put(p.id, new LinkedList<Statement>());
			_predicateMap.get(p.id).add(statement);
		}
	}

	private void reason(String id) {
		LinkedList<Statement> _reasonHistory = new LinkedList<Statement>();
		Stack<String> _reasonIds = new Stack<String>();
		_reasonIds.push(id);

		// for every predicate we have changed, including those after reasoning
		while (!_reasonIds.empty()) {
			String curId = _reasonIds.pop();

			if (_predicateMap.containsKey(curId)) {
				List<Statement> statements = _predicateMap.get(curId);

				for (Statement s : statements) {
					// if statement hasn't been reasoned && premises are true,
					// make conclusions true && note changed predicates
					if (!_reasonHistory.contains(s) && isTrue(s.premises)) {
						for (Predicate p : s.conclusions) {
							if (p.negated) {
								if (setPredicateFalse(p.id)) _reasonIds.push(p.id);
							} else {
								if (setPredicateTrue(p.id)) _reasonIds.push(p.id);
							}
						}
					}

					// remember statement has been reasoned
					_reasonHistory.add(s);
				}
			}
		}
	}

	private boolean setPredicateTrue(String id) {
		if (_state.contains(id)) return false;

		_state.add(id);

		if (_listener != null) {
			_listener.validated(id);
			_listener.changed(id);
		}

		return true;
	}

	private boolean setPredicateFalse(String id) {
		if (!_state.contains(id)) return false;

		_state.remove(id);

		if (_listener != null) {
			_listener.invalidated(id);
			_listener.changed(id);
		}

		return true;
	}

	private boolean isTrue(Predicate[] premises) {
		for (Predicate p : premises) {
			if (_state.contains(p.id)) {
				if (p.negated) return false;
			} else {
				if (!p.negated) return false;
			}
		}

		return true;
	}

	public interface Listener {
		void validated(String id);

		void invalidated(String id);

		void changed(String id);
	}

	public static class Predicate {
		public String id;
		public boolean negated = false;

		public Predicate(String id) {
			this.id = id;
		}

		public Predicate(String id, boolean negated) {
			this.id = id;
			this.negated = negated;
		}
	}

	public static class Statement {
		public Predicate[] premises = {};
		public Predicate[] conclusions = {};

		@Override
		public String toString() {
			return premises.length + ", " + conclusions.length;
		}
	}
}
