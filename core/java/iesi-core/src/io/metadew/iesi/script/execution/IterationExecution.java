package io.metadew.iesi.script.execution;

import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.script.configuration.IterationConfiguration;
import io.metadew.iesi.script.configuration.IterationInstance;
import io.metadew.iesi.script.operation.IterationOperation;

public class IterationExecution {

	private FrameworkExecution frameworkExecution;
	private ExecutionControl executionControl;
	private long iterationNumber;
	private IterationOperation iterationOperation;
	private IterationConfiguration iterationConfiguration;
	private boolean iterationOff;
	private String iterationType;
	private String iterationName;

	public IterationExecution() {
		this.setIterationOff(true);
		this.setIterationNumber(0);
	}

	// Methods
	public void initialize(FrameworkExecution frameworkExecution, ExecutionControl executionControl,
			String iterationName) {
		this.setExecutionControl(executionControl);
		this.setIterationName(iterationName);
		this.setIterationOperation(
				this.getExecutionControl().getExecutionRuntime().getIterationOperation(this.getIterationName()));
		this.setIterationConfiguration(new IterationConfiguration(this.getFrameworkExecution(),
				this.getExecutionControl().getExecutionRuntime().getRunCacheFolderName(), executionControl));
		if (this.getIterationOperation().getIteration().getType().trim().equalsIgnoreCase("values")) {
			this.getIterationConfiguration().setIterationValues(this.getExecutionControl().getRunId(),
					this.getIterationOperation().getIteration().getName(),
					this.getIterationOperation().getIteration().getValues());
			this.setIterationType("values");
			this.setIterationOff(false);
		} else if (this.getIterationOperation().getIteration().getType().trim().equalsIgnoreCase("for")) {
			this.getIterationConfiguration().setIterationFor(this.getExecutionControl().getRunId(),
					this.getIterationOperation().getIteration().getName(),
					this.getIterationOperation().getIteration().getFrom(),
					this.getIterationOperation().getIteration().getTo(),
					this.getIterationOperation().getIteration().getStep());
			this.setIterationType("for");
			this.setIterationOff(false);
		} else if (this.getIterationOperation().getIteration().getType().trim().equalsIgnoreCase("condition")) {
			this.setIterationType("condition");
			this.setIterationOff(false);
		} else if (this.getIterationOperation().getIteration().getType().trim().equalsIgnoreCase("list")) {
			this.getIterationConfiguration().setIterationList(this.getExecutionControl().getRunId(),
					this.getIterationOperation().getIteration().getName(),
					this.getIterationOperation().getIteration().getList());
			this.setIterationType("list");
			this.setIterationOff(false);
		}
	}

	public boolean hasNext() {
		this.iterationNumber++;
		if (this.isIterationOff()) {
			if (this.getIterationNumber() == 1) {
				return true;
			} else {
				return false;
			}
		} else if (this.getIterationType().equalsIgnoreCase("values")
				|| this.getIterationType().equalsIgnoreCase("for")) {
			IterationInstance iterationInstance = this.getIterationConfiguration()
					.hasNext(this.getExecutionControl().getRunId(), this.getIterationNumber());
			return !iterationInstance.isEmpty();
		} else if (this.getIterationType().equalsIgnoreCase("condition")) {
			IterationInstance iterationInstance = this.getIterationConfiguration().hasNext(
					this.getExecutionControl().getRunId(), this.getIterationOperation().getIteration().getCondition());
			return !iterationInstance.isEmpty();
		} else if (this.getIterationType().equalsIgnoreCase("list")) {
			IterationInstance iterationInstance = this.getIterationConfiguration().hasNextListItem(
					this.getExecutionControl().getRunId(), this.getIterationName(),
					this.getIterationNumber());
			return !iterationInstance.isEmpty();

		} else {
			return false;
		}
	}

	// Getters and setters
	public long getIterationNumber() {
		return iterationNumber;
	}

	public void setIterationNumber(long iterationNumber) {
		this.iterationNumber = iterationNumber;
	}

	public boolean isIterationOff() {
		return iterationOff;
	}

	public void setIterationOff(boolean iterationOff) {
		this.iterationOff = iterationOff;
	}

	public IterationOperation getIterationOperation() {
		return iterationOperation;
	}

	public void setIterationOperation(IterationOperation iterationOperation) {
		this.iterationOperation = iterationOperation;
	}

	public IterationConfiguration getIterationConfiguration() {
		return iterationConfiguration;
	}

	public void setIterationConfiguration(IterationConfiguration iterationConfiguration) {
		this.iterationConfiguration = iterationConfiguration;
	}

	public ExecutionControl getExecutionControl() {
		return executionControl;
	}

	public void setExecutionControl(ExecutionControl executionControl) {
		this.executionControl = executionControl;
	}

	public FrameworkExecution getFrameworkExecution() {
		return frameworkExecution;
	}

	public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
		this.frameworkExecution = frameworkExecution;
	}

	public String getIterationType() {
		return iterationType;
	}

	public void setIterationType(String iterationType) {
		this.iterationType = iterationType;
	}

	public String getIterationName() {
		return iterationName;
	}

	public void setIterationName(String iterationName) {
		this.iterationName = iterationName;
	}

}