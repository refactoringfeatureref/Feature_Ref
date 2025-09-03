package br.featureref.organicref.tests.dummy.relation.extension;

public class FloatChild extends GenericParent<Float> {

	@Override
	public void a() {
		super.a();
	}
	
	public <L> void generic(L a) {
		super.generic(a);
	}
	
	public void c() {
		
	}
}
