package br.featureref.organicref.tests.dummy.relation.methodcalls;

public class MethodCalls {

	public void a() {
		a();
	}
	
	public void b() {
		c();
	}
	
	public void c() {
		a();
	}
	
	public void secondCallGroup1() {
		
	}
	
	public void secondCallGroup2() {
		secondCallGroup1();
	}
}
