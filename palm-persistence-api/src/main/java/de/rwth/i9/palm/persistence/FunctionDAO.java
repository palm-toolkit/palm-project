package de.rwth.i9.palm.persistence;

import java.util.List;

import de.rwth.i9.palm.model.Function;
import de.rwth.i9.palm.model.FunctionType;

public interface FunctionDAO extends GenericDAO<Function>, InstantiableDAO
{
	List<Function> getFunctionByFunctionTypeAndGrantType( FunctionType functionType, String grantType );
}
