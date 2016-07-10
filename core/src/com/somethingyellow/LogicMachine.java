package com.somethingyellow;

import net.sourceforge.jeval.EvaluationException;
import net.sourceforge.jeval.Evaluator;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogicMachine {
	public static final Pattern VariablePattern = Pattern.compile("\\#\\{[\\w]+\\}");

	private Evaluator _evaluator;
	private LinkedList<Expression> _expressions;
	private TreeMap<String, LinkedList<Expression>> _predicateMap;
	private HashSet<String> _state;

	public LogicMachine() {
		_predicateMap = new TreeMap<String, LinkedList<Expression>>();
		_expressions = new LinkedList<Expression>();
		_state = new HashSet<String>();
		_evaluator = new Evaluator();
	}

	public Expression addExpression(String expressionString, Listener listener) {
		Expression expression = new Expression(expressionString, listener);

		// add to treemap based on expression's predicates
		for (String premise : expression.premises()) {
			if (!_predicateMap.containsKey(premise)) {
				_predicateMap.put(premise, new LinkedList<Expression>());
			}
			_predicateMap.get(premise).add(expression);
		}

		return expression;
	}

	public void clear() {
		_predicateMap.clear();
		_expressions.clear();
		_evaluator.clearFunctions();
		resetState();
	}

	public void resetState() {
		_state.clear();
	}

	public void set(String name) {
		set(name, true);
	}

	public void set(String name, boolean isTrue) {
		if (isTrue) {
			if (setPredicateTrue(name)) reason(name);
		} else {
			if (setPredicateFalse(name)) reason(name);
		}
	}

	public boolean get(String name) {
		return _state.contains(name);
	}

	private void reason(String name) {
		LinkedList<Expression> expressions = _predicateMap.get(name);
		if (expressions == null) return;

		for (Expression expression : expressions) {
			expression.evaluate();
		}
	}

	private boolean setPredicateTrue(String name) {
		if (_state.contains(name)) return false;
		_state.add(name);
		return true;
	}

	private boolean setPredicateFalse(String name) {
		if (!_state.contains(name)) return false;
		_state.remove(name);
		return true;
	}

	public interface Listener {
		void expressionChanged(boolean isTrue);
	}

	public class Expression {
		private TreeSet<String> _premises; // Set of predicate names in premise
		private Listener _listener; // External listener of this expression
		private boolean _prevValue;
		private String _string;

		private Expression(String expressionString, Listener listener) {
			_listener = listener;
			_premises = new TreeSet<String>();
			_string = expressionString;
			_prevValue = false;

			// Get premises
			Matcher matcher = VariablePattern.matcher(_string);
			while (matcher.find()) {
				String predicate = matcher.group();
				predicate = predicate.substring(2, predicate.length() - 1);
				_premises.add(predicate);
			}

			_listener.expressionChanged(_prevValue);
		}

		private void evaluate() {
			_evaluator.clearVariables();
			for (String premise : _premises) {
				_evaluator.putVariable(premise, _state.contains(premise) ? "1" : "0");
			}

			try {
				String result = _evaluator.evaluate(_string);

				// if expression is true
				if (Double.parseDouble(result) == 1) {
					if (!_prevValue) {
						_prevValue = true;
						_listener.expressionChanged(_prevValue);
					}
				} else {
					if (_prevValue) {
						_prevValue = false;
						_listener.expressionChanged(_prevValue);
					}
				}

			} catch (EvaluationException exception) {
				System.err.print("Invalid expression in LogicMachine: '" + _string + "'");
			}
		}

		public boolean value() {
			return _prevValue;
		}

		public Set<String> premises() {
			return _premises;
		}
	}

/*	public static void main(String[] args) {
		LogicMachine lm = new LogicMachine();
		lm.addExpression("(#{A} && (#{C} || #{B} || #{A}))", new Listener() {
			@Override
			public void expressionChanged(boolean isTrue) {
				System.out.println("CHANGED: " + isTrue);
			}
		});

		lm.set("C"); // false
		lm.set("B"); // false
		lm.set("A"); // true
		lm.set("B", false); // true
		lm.set("C", false); // true
	}*/
}
