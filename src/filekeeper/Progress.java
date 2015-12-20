package filekeeper;

public class Progress {
	private long	maxValue	= 0;
	private long	carentValue	= 0;

	// private ProgressBar bar;

	public Progress(long maxValue) {
		this.maxValue = maxValue;
	}

	public Progress() {

	}

	public void setMaxValue(long maxValue) {
		this.maxValue = maxValue;
	}

	public Long getMaxValue() {
		return this.maxValue;
	}

	// public void setProgressBar(ProgressBar bar) {
	// this.bar = bar;
	// }

	public Integer getIntPersent() {
		return (int) (carentValue * 100 / maxValue);
	}

	public String getCarentPersent() {
		int p = (int) (carentValue * 100 / maxValue);
		return p + " %";
	}

	public void addProgress(long progress) {
		carentValue += progress;
		// if (bar != null) {
		// bar.setSelection(getIntPersent());
		// }
	}

	public void reset() {
		maxValue = 0;
		carentValue = 0;
	}
}
