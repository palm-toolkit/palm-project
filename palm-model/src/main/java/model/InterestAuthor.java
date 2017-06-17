package de.rwth.i9.palm.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import de.rwth.i9.palm.persistence.PersistableResource;

@Entity
@Table( name = "interest_author" )
public class InterestAuthor extends PersistableResource
{
	@Column( columnDefinition = "Decimal(3,3) default '0.000'" )
	double value;

	// relation
	@ManyToOne
	@JoinColumn( name = "interest_id" )
	Interest interest;

	@ManyToOne
	@JoinColumn( name = "author_id" )
	Author author;

	// getter and setter

	public double getValue()
	{
		return value;
	}

	public void setValue( double value )
	{
		this.value = value;
	}

	public Interest getInterest()
	{
		return interest;
	}

	public void setInterest( Interest interest )
	{
		this.interest = interest;
	}

	public Author getAuthor()
	{
		return author;
	}

	public void setAuthor( Author author )
	{
		this.author = author;
	}

}
