package it.vdevred.n26.domain;


public class Transaction {

    Double amount;

    Long timestamp;
    
    public Transaction(Double amount, Long timestamp)
    {
    	this.amount = amount;
    	this.timestamp = timestamp;
    }
    
    public Transaction()
    {
    	
    }

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}  
    
}