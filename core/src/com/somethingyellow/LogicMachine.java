package com.somethingyellow;

import com.badlogic.gdx.math.Interpolation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Pattern;

public class LogicMachine {
	public static final String TERM_AND = "AND";
	public static final String TERM_OR = "OR";
	public static final String TERM_NOT = "NOT";
	public static final int MAX_PREDICATES = 26;
	public static final String DELIMITER = "`";

	private LinkedList<Expression> _expressions;
	private TreeMap<Integer, LinkedList<Expression>> _predicateMap;
	private HashSet<Integer> _state;
	private HashMap<String, Integer> _predicateIndices;
	private ArrayList<String> _predicatesByIndex;

	public LogicMachine() {
		_predicateMap = new TreeMap<Integer, LinkedList<Expression>>();
		_predicateIndices = new HashMap<String, Integer>();
		_expressions = new LinkedList<Expression>();
		_predicatesByIndex = new ArrayList<String>();
		resetState();
	}

	public Expression addExpression(String expressionString, Listener listener) {
		Expression expression = new Expression(expressionString, listener);

		// add to treemap based on expression's predicates
		for (String p : expression._premises) {
			int index = getPredicateIndex(p);
			if (!_predicateMap.containsKey(index)) {
				_predicateMap.put(index, new LinkedList<Expression>());
			}

			_predicateMap.get(index).add(expression);
		}

		return expression;
	}

	public LogicMachine resetState() {
		_state = new HashSet<Integer>();
		return this;
	}

	public LogicMachine set(String name) {
		return set(name, true);
	}

	public LogicMachine set(String name, boolean isTrue) {
		int index = getPredicateIndex(name);
		if (isTrue) {
			if (setPredicateTrue(index)) reason(index);
		} else {
			if (setPredicateFalse(index)) reason(index);
		}

		return this;
	}

	public boolean get(String name) {
		return _state.contains(getPredicateIndex(name));
	}

	private void reason(int index) {
		LinkedList<Expression> expressions = _predicateMap.get(index);
		if (expressions == null) return;

		for (Expression expression : expressions) {
			expression.evaluate();
		}
	}

	private boolean setPredicateTrue(int index) {
		if (_state.contains(index)) return false;
		_state.add(index);
		return true;
	}

	private boolean setPredicateFalse(int index) {
		if (!_state.contains(index)) return false;
		_state.remove(index);
		return true;
	}

	private int getPredicateIndex(String name) {
		if (!_predicateIndices.containsKey(name)) {
			int index = _predicateIndices.size();
			if (index > MAX_PREDICATES)
				throw new IllegalArgumentException("Max predicate count reached.");
			_predicateIndices.put(name, index);
			_predicatesByIndex.add(index, name);
			return index;
		} else {
			return _predicateIndices.get(name);
		}
	}

	public interface Listener {
		void changed(boolean isTrue);
	}

	public class Expression {
		private TreeSet<String> _premises; // Set of predicate names in premise
		private Pattern _premisePattern; // Pattern representing the logical expression
		private Listener _listener; // External listener of this expression
		private String _matchString; // String that contains predicates to match with pattern
		private boolean _prevValue;

		private Expression(String expressionString, Listener listener) {
			_listener = listener;
			_premises = new TreeSet<String>();

			// Interpret premises
			String[] premisesParts = expressionString.split(TERM_AND + "|" + TERM_OR + "|\\(|\\)");
			String premisesPatternString = expressionString;
			StringBuilder stringBuilder = new StringBuilder();
			for (String premiseRaw : premisesParts) {
				String premise = premiseRaw.trim();
				if (premise.length() == 0) continue;
				boolean negated = false;

				if (premise.indexOf(TERM_NOT) == 0) {
					premise = premise.substring(TERM_NOT.length()).trim();
					negated = true;
				}

				if (!_premises.contains(premise)) {
					_premises.add(premise);
					premisesPatternString = premisesPatternString.replaceAll(TERM_NOT + "[\\s]*" + premise, getRep(premise, true)).
							replace(premise, getRep(premise, false));

				}

				// Build match string based on order of predicates in expression
				stringBuilder.append(getRep(premise, negated));
			}

			_premisePattern = Pattern.compile(premisesPatternString.replaceAll(DELIMITER + "[\\s]+", DELIMITER).
					replaceAll("[\\s]+" + DELIMITER, DELIMITER).
					replace(TERM_AND, "").replace(TERM_OR, "|"));
			_matchString = stringBuilder.toString();
			_prevValue = false;

			_listener.changed(_prevValue);
		}


		private void evaluate() {
			String matchString = _matchString;

			for (String premise : _premises) {
				int index = getPredicateIndex(premise);

				if (_state.contains(index)) {
					matchString = matchString.replace(getRep(premise, true), "");
				} else {
					matchString = matchString.replace(getRep(premise, false), "");
				}
			}

			// System.out.println("For expression " + _premisePattern.pattern() + ", result: " + matchString + " = " + _premisePattern.matcher(matchString).find());
			// if expression is true
			if (_premisePattern.matcher(matchString).find()) {
				if (!_prevValue) {
					_prevValue = true;
					_listener.changed(_prevValue);
				}
			} else {
				if (_prevValue) {
					_prevValue = false;
					_listener.changed(_prevValue);
				}
			}
		}

		public boolean value() {
			return _prevValue;
		}

		public Set<String> premises() {
			return _premises;
		}

		private String getRep(String name, boolean negated) {
			return DELIMITER + (negated ? -1 : 1) * (1 + getPredicateIndex(name)) + DELIMITER;
		}
	}
	/*
	public static void main(String[] args) {
		LogicMachine lm = new LogicMachine();
		lm.addExpression(" ( A            && C ||!B &&   !C)|| D", new Listener() {
			@Override
			public void changed(boolean isTrue) {
				System.out.println(isTrue);
			}
		});

		lm.set("B");
		lm.set("C");
		lm.set("D");
	}

	*/
}
