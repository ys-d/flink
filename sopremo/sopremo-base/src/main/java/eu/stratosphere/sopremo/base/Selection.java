package eu.stratosphere.sopremo.base;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.BooleanNode;

import eu.stratosphere.sopremo.ElementaryOperator;
import eu.stratosphere.sopremo.JsonStream;
import eu.stratosphere.sopremo.Name;
import eu.stratosphere.sopremo.Property;
import eu.stratosphere.sopremo.expressions.BooleanExpression;
import eu.stratosphere.sopremo.expressions.ConstantExpression;
import eu.stratosphere.sopremo.expressions.EvaluationExpression;
import eu.stratosphere.sopremo.pact.JsonCollector;
import eu.stratosphere.sopremo.pact.PactJsonObject;
import eu.stratosphere.sopremo.pact.SopremoMap;

@Name(verb = "select")
public class Selection extends ElementaryOperator {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7687925343684319311L;

	private EvaluationExpression condition = new ConstantExpression(true);

	public Selection(final JsonStream input) {
		super(input);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (this.getClass() != obj.getClass())
			return false;
		return super.equals(obj) && this.condition.equals(((Selection) obj).condition);
	}

	public EvaluationExpression getCondition() {
		return condition;
	}

	@Override
	public int hashCode() {
		final int prime = 37;
		int result = super.hashCode();
		result = prime * result + this.condition.hashCode();
		return result;
	}

	@Property(preferred = true)
	@Name(preposition = "where")
	public void setCondition(EvaluationExpression condition) {
		if (condition == null)
			throw new NullPointerException("condition must not be null");

		this.condition = condition;
	}

	public Selection withCondition(EvaluationExpression condition) {
		setCondition(condition);
		return this;
	}

	//
	// @Override
	// public PactModule asPactModule(EvaluationContext context) {
	// PactModule module = new PactModule(this.toString(), 1, 1);
	// MapContract<PactJsonObject.Key, PactJsonObject, PactJsonObject.Key, PactJsonObject> selectionMap =
	// new MapContract<PactJsonObject.Key, PactJsonObject, PactJsonObject.Key, PactJsonObject>(
	// SelectionStub.class);
	// module.getOutput(0).setInput(selectionMap);
	// selectionMap.setInput(module.getInput(0));
	// SopremoUtil.serialize(selectionMap.getStubParameters(), "condition", this.getCondition());
	// SopremoUtil.setContext(selectionMap.getStubParameters(), context);
	// return module;
	// }

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder(this.getName());
		builder.append(" on ").append(this.condition);
		return builder.toString();
	}

	public static class SelectionStub extends
			SopremoMap<PactJsonObject.Key, PactJsonObject, PactJsonObject.Key, PactJsonObject> {
		private BooleanExpression condition;

		@Override
		protected void map(final JsonNode key, final JsonNode value, final JsonCollector out) {
			if (this.condition.evaluate(value, this.getContext()) == BooleanNode.TRUE)
				out.collect(key, value);
		}

	}
}
