package br.featureref.organicref.clusterization.jaccard;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import br.featureref.organicref.model.entities.Type;
import br.featureref.organicref.tests.util.TypeLoader;

class EntitySetBuilderIntegrationTest {

	private static EntitySetBuilder builder;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		Type targetType = TypeLoader
				.loadOne(new File("test/br/featureref/organicref/tests/dummy/BrainClassWithTwoBrainMethods.java"));

		builder = new EntitySetBuilder(targetType);

		builder.build();
	}

	@Test
	public void typeWith5Methods_5Fields_shouldGenerate_10EntitySets() {
		assertEquals(10, builder.getEntitySets().size());
	}

//	@Test
//	public void fieldUsedByTwoMethods_shouldHave_3AssociatedStrings() {
//		EntitySet<? extends Element> entitySet = builder
//				.getEntitySetOfElement("br.featureref.organicref.tests.dummy.BrainClassWithTwoBrainMethods.d");
//
//		assertEquals(3, entitySet.getAssociatedStrings().size());
//		assertTrue(entitySet.getAssociatedStrings().containsAll(Arrays.asList("d", "brain2", "brain")));
//	}
//
//	@Test
//	public void isolatedMethod_shouldHave_1AssociatedString() {
//		EntitySet<? extends Element> entitySet = builder
//				.getEntitySetOfElement("br.featureref.organicref.tests.dummy.BrainClassWithTwoBrainMethods.cc2(String)");
//
//		assertEquals(1, entitySet.getAssociatedStrings().size());
//		assertTrue(entitySet.getAssociatedStrings().contains("cc2"));
//	}
//	
//	@Test
//	public void methodThatUse5Fields_ShouldHave_6AssociatedStrings() {
//		EntitySet<? extends Element> entitySet = builder
//				.getEntitySetOfElement("br.featureref.organicref.tests.dummy.BrainClassWithTwoBrainMethods.brain()");
//
//		assertEquals(6, entitySet.getAssociatedStrings().size());
//		assertTrue(entitySet.getAssociatedStrings().containsAll(Arrays.asList("brain", "b", "c", "publico", "a", "d")));
//	}

}
