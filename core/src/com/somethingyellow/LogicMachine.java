package com.somethingyellow;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.TreeMap;

public class LogicMachine {
	public static final String TERM_IMPLY = "->";
	public static final String TERM_AND = "&&";
	public static final String TERM_OR = "||";
	public static final String TERM_NOT = "!";

	private LinkedList<Statement> _statements;
	private TreeMap<String, List<Statement>> _predicateMap;
	private HashSet<String> _state;
	private LinkedList<Listener> _listeners;

	public LogicMachine() {
		_predicateMap = new TreeMap<String, List<Statement>>();
		_statements = new LinkedList<Statement>();
		_listeners = new LinkedList<Listener>();
		resetState();
	}

	public LogicMachine(String[] statementsString) {
		this();
		addStatements(statementsString);
	}

	private static Statement ParseStatement(String string) {
		// TODO: Optimise representation and parsing
		// TODO: Parse more complex expressions

		Statement statement;
		LinkedList<Predicate> predicates;

		String[] parts = string.split(TERM_IMPLY);
		if (parts.length < 2)
			throw new IllegalArgumentException("Statement should have '" + TERM_IMPLY + "'!");
		if (parts.length > 2)
			throw new IllegalArgumentException("'" + TERM_IMPLY + "' should only appear once!");

		statement = new Statement();

		// Dealing with premises
		String[] premiseParts = parts[0].split(TERM_AND);

		predicates = new LinkedList<Predicate>();
		for (String p : premiseParts) {
			p = p.trim();
			if (p.length() == 0) continue;

			if (p.indexOf(TERM_NOT) == 0) {
				predicates.add(new Predicate(p.substring(TERM_NOT.length()), true));
			} else {
				predicates.add(new Predicate(p));
			}
		}
		statement._premises = predicates.toArray(new Predicate[predicates.size()]);

		// Dealing with conclusion
		String[] conclusionParts = parts[1].split(TERM_AND);

		predicates = new LinkedList<Predicate>();
		for (String p : conclusionParts) {
			p = p.trim();
			if (p.length() == 0) continue;

			if (p.indexOf(TERM_NOT) == 0) {
				predicates.add(new Predicate(p.substring(TERM_NOT.length()), true));
			} else {
				predicates.add(new Predicate(p));
			}
		}
		statement._conclusions = predicates.toArray(new Predicate[predicates.size()]);

		return statement;
	}

	public LogicMachine addListener(Listener listener) {
		_listeners.add(listener);
		return this;
	}

	public LogicMachine resetState() {
		_state = new HashSet<String>();
		return this;
	}

	public LogicMachine set(String name) {
		return set(name, true);
	}

	public LogicMachine set(String name, boolean isTrue) {
		if (isTrue) {
			if (setPredicateTrue(name)) reason(name);
		} else {
			if (setPredicateFalse(name)) reason(name);
		}

		return this;
	}

	public boolean get(String name) {
		return _state.contains(name);
	}

	public LogicMachine addStatements(String[] statementsString) {
		for (String s : statementsString) {
			addStatement(ParseStatement(s));
		}

		return this;
	}

	public Statement addStatement(String statementString) {
		return addStatement(ParseStatement(statementString));
	}

	private Statement addStatement(Statement statement) {
		_statements.add(statement);

		// add to treemap of predicates based on premises
		for (Predicate p : statement._premises) {
			if (!_predicateMap.containsKey(p._name))
				_predicateMap.put(p._name, new LinkedList<Statement>());
			_predicateMap.get(p._name).add(statement);
		}

		return statement;
	}

	private void reason(String name) {
		LinkedList<Statement> _reasonHistory = new LinkedList<Statement>();
		Stack<String> _reasonIds = new Stack<String>();
		_reasonIds.push(name);

		// for every predicate we have changed, including those after reasoning
		while (!_reasonIds.empty()) {
			String curId = _reasonIds.pop();

			if (_predicateMap.containsKey(curId)) {
				List<Statement> statements = _predicateMap.get(curId);

				for (Statement s : statements) {
					// if statement hasn't been reasoned && premises are true,
					// make conclusions true && note changed predicates
					if (!_reasonHistory.contains(s) && isTrue(s._premises)) {
						for (Predicate p : s._conclusions) {
							if (p._negated) {
								if (setPredicateFalse(p._name)) _reasonIds.push(p._name);
							} else {
								if (setPredicateTrue(p._name)) _reasonIds.push(p._name);
							}
						}
					}

					// remember statement has been reasoned
					_reasonHistory.add(s);
				}
			}
		}
	}

	private boolean setPredicateTrue(String name) {
		if (_state.contains(name)) return false;

		_state.add(name);

		for (Listener listener : _listeners) {
			listener.validated(name);
			listener.changed(name);
		}

		return true;
	}

	private boolean setPredicateFalse(String name) {
		if (!_state.contains(name)) return false;

		_state.remove(name);

		for (Listener listener : _listeners) {
			listener.invalidated(name);
			listener.changed(name);
		}

		return true;
	}

	private boolean isTrue(Predicate[] premises) {
		for (Predicate p : premises) {
			if (_state.contains(p._name)) {
				if (p._negated) return false;
			} else {
				if (!p._negated) return false;
			}
		}

		return true;
	}

	public interface Listener {
		void validated(String name);

		void invalidated(String name);

		void changed(String name);
	}

	public static class Predicate {
		private String _name;
		private boolean _negated = false;

		public Predicate(String name) {
			_name = name;
		}

		public Predicate(String name, boolean negated) {
			_name = name;
			_negated = negated;
		}

		public String name() {
			return _name;
		}

		public boolean negated() {
			return _negated;
		}
	}

	public static class Statement {
		private Predicate[] _premises = {};
		private Predicate[] _conclusions = {};

		public Predicate[] premises() {
			return _premises;
		}

		public Predicate[] conclusions() {
			return _conclusions;
		}

		@Override
		public String toString() {
			return _premises.length + ", " + _conclusions.length;
		}
	}
}
