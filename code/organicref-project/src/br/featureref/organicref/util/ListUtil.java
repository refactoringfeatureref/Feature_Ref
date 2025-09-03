package br.featureref.organicref.util;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class ListUtil {

	public static <T> T getRandomElementFrom(List<T> elements) {
		int randomIndex = ThreadLocalRandom.current().nextInt(0, elements.size());
		return elements.get(randomIndex);
	}
	
	public static <T> T getAndRemoveRandomElementFrom(List<T> elements) {
		int randomIndex = ThreadLocalRandom.current().nextInt(0, elements.size());
		return elements.remove(randomIndex);
	}
	public static <T> void removeRandomElementFrom(List<T> elements) {
		int randomIndex = ThreadLocalRandom.current().nextInt(0, elements.size());
		elements.remove(randomIndex);
	}

	public static <T> void removeRandomElementFrom(List<T> elements1, List<T> elements2) {
		int selectedList = ThreadLocalRandom.current().nextInt(1, 3);
		if (selectedList == 1) {
			removeRandomElementFrom(elements1);
		} else {
			removeRandomElementFrom(elements2);
		}
	}

	//copied from stack overflow
	public static <E> List<E> pickNRandomElements(List<E> list, int n, Random r) {
		int length = list.size();

		if (length < n) return null;

		//We don't need to shuffle the whole list
		for (int i = length - 1; i >= length - n; --i)
		{
			Collections.swap(list, i , r.nextInt(i + 1));
		}
		return list.subList(length - n, length);
	}

	public static <E> List<E> pickNRandomElements(List<E> list, int n) {
		return pickNRandomElements(list, n, ThreadLocalRandom.current());
	}
}
