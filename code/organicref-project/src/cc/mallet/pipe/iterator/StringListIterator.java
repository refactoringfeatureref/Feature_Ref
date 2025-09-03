/* Copyright (C) 2002 Univ. of Massachusetts Amherst, Computer Science Dept.
  This file is part of "MALLET" (MAchine Learning for LanguagE Toolkit).
  http://www.cs.umass.edu/~mccallum/mallet
  This software is provided under the terms of the Common Public License,
  version 1.0, as published by http://www.opensource.org.  For further
  information, see the file `LICENSE' included with this distribution. */

/** 
    <a href="mailto:mccallum@cs.umass.edu">mccallum@cs.umass.edu</a>
*/

package cc.mallet.pipe.iterator;

import java.net.URI;
import java.util.Iterator;
import java.util.List;

import cc.mallet.types.Instance;

public class StringListIterator implements Iterator<Instance>
{
	List<String> data;
	int index;
	
	public StringListIterator (List<String> data)
	{
		this.data = data;
		this.index = 0;
	}

	public Instance next ()
	{
		URI uri = null;
		try { uri = new URI ("List:" + index); }
		catch (Exception e) { e.printStackTrace(); throw new IllegalStateException(); }
		return new Instance (data.get(index++), null, uri, null);
	}

	public boolean hasNext ()	{	return index < data.size();	}
	
	public void remove () {
		data.remove(index);
//		throw new IllegalStateException ("This Iterator<Instance> does not support remove().");
	}

}
