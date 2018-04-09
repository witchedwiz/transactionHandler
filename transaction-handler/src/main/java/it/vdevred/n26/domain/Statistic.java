package it.vdevred.n26.domain;

//not bothering with lombok, so boilerplate inc....
public class Statistic {

	private Double sum = null;
	private Double avg = null;
	private Double max = null;
	private Double min = null;
	private long count = 0;


	public Statistic(int count, Double sum, Double min, Double max) {
		this.count = count;
		this.sum = sum;
		this.min = min;
		this.max = max;
		this.avg = computeAvg();
	}

	public Statistic() {
		// TODO Auto-generated constructor stub
	}

	public Double getSum() {
		return sum;
	}

	public void setSum(double sum) {
		this.sum = sum;
	}

	public Double getAvg() {
		return avg;
	}

	public Double getMax() {
		return max;
	}

	public void setMax(double max) {
		this.max = max;
	}

	public Double getMin() {
		return min;
	}

	public void setMin(double min) {
		this.min = min;
	}

	public long getCount() {
		return count > 0 ? count : 0;
	}

	public void setCount(long count) {
		this.count = count;
	}


	public Statistic register(double addValue)
	{
		this.count = this.count+1;
		this.sum = this.sum != null ? this.sum+addValue : addValue;
		this.min = this.min != null ? Math.min(addValue, this.min) : addValue;
		this.max = this.max != null ? Math.max(addValue, this.max) : addValue;
		this.avg = computeAvg();
		return this;
	}

	public Double computeAvg() {
		if (this.sum == null)
		{
			this.avg = null;
			return this.avg;
		}
		else
		{
			if (getCount() <= 0)
			{
				this.avg = null;
				return this.avg;
			}
			else
			{
				this.avg = getSum() / getCount();
				return this.avg;
			}
		}
	}

}
