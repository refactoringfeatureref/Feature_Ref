package br.featureref.organicref.model.entities;

import java.util.List;
import java.util.Objects;

public class Concern {

	private int topicId;
	private List<String> wordsInConcern;

	public Concern(){}

	public Concern(int topicId, List<String> wordsInConcern) {
		this.topicId = topicId;
		this.wordsInConcern = wordsInConcern;
	}

	public int getTopicId() {
		return topicId;
	}

	public List<String> getWordsInConcern() {
		return wordsInConcern;
	}

	@Override
	public String toString() {
		return "Concern [topicId=" + topicId + ", wordsInConcern=" + wordsInConcern + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(topicId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Concern other = (Concern) obj;
		return topicId == other.topicId;
	}
}
